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
package org.rippleosi.patient.documents.discharge.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rippleosi.common.model.QueryResponse;
import org.rippleosi.common.service.AbstractOpenEhrService;
import org.rippleosi.common.service.proxies.RequestProxy;
import org.rippleosi.patient.documents.common.model.AbstractDocumentSummary;
import org.rippleosi.patient.documents.discharge.model.DischargeDocumentDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 */
@Service
public class OpenEHRDischargeDocumentSearch extends AbstractOpenEhrService implements DischargeDocumentSearch {

    @Value("${c4hOpenEHR.subjectNamespace}")
    private String openEhrSubjectNamespace;

    @Autowired
    private RequestProxy requestProxy;

    @Value("${c4hOpenEHR.address}")
    private String openEhrAddress;

    @Override
    public List<AbstractDocumentSummary> findAllDischargeDocuments(String patientId) {
        DischargeDocumentSummaryQueryStrategy query = new DischargeDocumentSummaryQueryStrategy(patientId);

        return findData(query);
    }

    @Override
    public DischargeDocumentDetails findDischargeDocument(String patientId, String documentId) {

        DischargeDocumentDetailsQueryStrategy queryStrategy = new DischargeDocumentDetailsQueryStrategy(patientId, documentId);

        // Query openEHR for individual (Non repeating fields)
        String query = queryStrategy.getQuery(openEhrSubjectNamespace, queryStrategy.getPatientId());
        ResponseEntity<QueryResponse> response = requestProxy.getQueryWithoutSession(getQueryURI(query), QueryResponse.class);
        List<Map<String, Object>> results = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            results = response.getBody().getResultSet();
        }
        DischargeDocumentDetails dischargeDocumentDetails = queryStrategy.transform(results);

        // Query repeating patient identifier
        query = queryStrategy.getIdentifierQuery(openEhrSubjectNamespace, queryStrategy.getPatientId());
        response = requestProxy.getQueryWithoutSession(getQueryURI(query), QueryResponse.class);
        results = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            results = response.getBody().getResultSet();
        }
        dischargeDocumentDetails = queryStrategy.transformPatientIdentifiers(results, dischargeDocumentDetails);

        // Query repeating diagnosis
        query = queryStrategy.getDiagnosisQuery(openEhrSubjectNamespace, queryStrategy.getPatientId());
        response = requestProxy.getQueryWithoutSession(getQueryURI(query), QueryResponse.class);
        results = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            results = response.getBody().getResultSet();
        }
        dischargeDocumentDetails = queryStrategy.transformDiagnosis(results, dischargeDocumentDetails);

        return dischargeDocumentDetails;
    }

    private String getQueryURI(String query) {
        UriComponents components = UriComponentsBuilder
            .fromHttpUrl(openEhrAddress + "/query")
            .queryParam("aql", query)
            .build();

        return components.toUriString();
    }
}
