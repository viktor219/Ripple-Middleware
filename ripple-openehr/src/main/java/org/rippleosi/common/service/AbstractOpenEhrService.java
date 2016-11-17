/*
 * Copyright 2015 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rippleosi.common.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.LazyMap;
import org.rippleosi.common.exception.DataNotFoundException;
import org.rippleosi.common.exception.UpdateFailedException;
import org.rippleosi.common.model.ActionRestResponse;
import org.rippleosi.common.model.EhrResponse;
import org.rippleosi.common.model.QueryPost;
import org.rippleosi.common.model.QueryResponse;
import org.rippleosi.common.repo.Repository;
import org.rippleosi.common.service.proxies.RequestProxy;
import org.rippleosi.common.service.strategies.query.AbstractGetQueryStrategy;
import org.rippleosi.common.service.strategies.query.AbstractPostQueryStrategy;
import org.rippleosi.common.service.strategies.query.QueryStrategy;
import org.rippleosi.common.service.strategies.store.CreateStrategy;
import org.rippleosi.common.service.strategies.store.UpdateStrategy;
import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.RepoSourceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 */
public abstract class AbstractOpenEhrService implements Repository {

    @Value("${repository.config.c4hOpenEHR:1000}")
    private int priority;

    @Value("${c4hOpenEHR.address}")
    private String openEhrAddress;

    @Value("${c4hOpenEHR.subjectNamespace}")
    private String openEhrSubjectNamespace;

    @Autowired
    private RequestProxy requestProxy;

    private final Map<String, String> idCache = Collections.synchronizedMap(LazyMap.lazyMap(new LRUMap<>(), new EhrIdLookup()));

    @Override
    public RepoSourceType getSource() {
        return RepoSourceTypes.MARAND;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    protected <T> T findData(QueryStrategy<T> queryStrategy) {

        ResponseEntity<QueryResponse> response;

        if (queryStrategy instanceof AbstractGetQueryStrategy) {
            response = queryByHttpGet((AbstractGetQueryStrategy) queryStrategy);
        }
        else if (queryStrategy instanceof AbstractPostQueryStrategy) {
            response = queryByHttpPost((AbstractPostQueryStrategy) queryStrategy);
        }
        else {
            throw new DataNotFoundException("Could not query for data using " + queryStrategy.getClass());
        }

        List<Map<String, Object>> results = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            results = response.getBody().getResultSet();
        }

        return queryStrategy.transform(results);
    }

    private ResponseEntity<QueryResponse> queryByHttpGet(AbstractGetQueryStrategy queryStrategy) {

        String query = queryStrategy.getQuery(openEhrSubjectNamespace, queryStrategy.getPatientId());

        return requestProxy.getQueryWithoutSession(getQueryByGetUri(query), QueryResponse.class);
    }

    private ResponseEntity<QueryResponse> queryByHttpPost(AbstractPostQueryStrategy queryStrategy) {

        String query = queryStrategy.getQuery(openEhrSubjectNamespace, queryStrategy.getPatientId());

        Map queryParams = queryStrategy.getQueryParameters(openEhrSubjectNamespace, queryStrategy.getPatientId());

        QueryPost queryPost = new QueryPost();
        queryPost.setAql(query);
        queryPost.setAqlParameters(queryParams);

        return requestProxy.postQueryWithoutSession(getQueryByPostUri(), QueryResponse.class, queryPost);
    }

    protected void createData(CreateStrategy createStrategy) {

        String patientId = createStrategy.getPatientId();
        String ehrId = findEhrIdByNHSNumber(patientId);
        String template = createStrategy.getTemplate();
        Map<String, Object> content = createStrategy.getContent();

        String uri = getCreateUri(template, ehrId);

        ResponseEntity<ActionRestResponse> response = requestProxy.postWithoutSession(uri, ActionRestResponse.class, content);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new UpdateFailedException("Could not create " + template + " for patient " + patientId);
        }
    }

    protected void updateData(UpdateStrategy updateStrategy) {

        String patientId = updateStrategy.getPatientId();
        String ehrId = findEhrIdByNHSNumber(patientId);
        String compositionId = updateStrategy.getCompositionId();
        String template = updateStrategy.getTemplate();
        Map<String, Object> content = updateStrategy.getContent();

        String uri = getUpdateUri(compositionId, template, ehrId);

        ResponseEntity<ActionRestResponse> response = requestProxy.putWithoutSession(uri, ActionRestResponse.class, content);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new UpdateFailedException("Could not update " + template + " (" + compositionId + ") for patient " + patientId);
        }
    }

    public String findEhrIdByNHSNumber(String nhsNumber) {
        return idCache.get(nhsNumber);
    }

    private String getQueryByGetUri(String query) {
        UriComponents components = UriComponentsBuilder
                .fromHttpUrl(openEhrAddress + "/query")
                .queryParam("aql", query)
                .build();

        return components.toUriString();
    }

    private String getQueryByPostUri() {
        UriComponents components = UriComponentsBuilder
                .fromHttpUrl(openEhrAddress + "/query")
                .build();

        return components.toUriString();
    }

    private String getCreateUri(String template, String ehrId) {
        UriComponents components = UriComponentsBuilder
                .fromHttpUrl(openEhrAddress + "/composition")
                .queryParam("templateId", template)
                .queryParam("ehrId", ehrId)
                .queryParam("format", "FLAT")
                .build();

        return components.toUriString();
    }

    private String getUpdateUri(String compositionId, String template, String ehrId) {
        UriComponents components = UriComponentsBuilder
                .fromHttpUrl(openEhrAddress + "/composition/" + compositionId)
                .queryParam("templateId", template)
                .queryParam("ehrId", ehrId)
                .queryParam("format", "FLAT")
                .build();

        return components.toUriString();
    }

    public class EhrIdLookup implements Transformer<String, String> {

        @Override
        public String transform(String nhsNumber) {
            ResponseEntity<EhrResponse> response = requestProxy.getQueryWithoutSession(getEhrIdUri(nhsNumber), EhrResponse.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new DataNotFoundException("OpenEHR query returned with status code " + response.getStatusCode());
            }

            return response.getBody().getEhrId();
        }

        private String getEhrIdUri(String nhsNumber) {
            UriComponents components = UriComponentsBuilder
                    .fromHttpUrl(openEhrAddress + "/ehr")
                    .queryParam("subjectId", nhsNumber)
                    .queryParam("subjectNamespace", openEhrSubjectNamespace)
                    .build();

            return components.toUriString();
        }
    }
}
