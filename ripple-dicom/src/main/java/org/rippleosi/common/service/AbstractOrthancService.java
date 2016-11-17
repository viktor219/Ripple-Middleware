/*
 *   Copyright 2016 Ripple OSI
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package org.rippleosi.common.service;

import java.util.ArrayList;
import java.util.List;

import org.rippleosi.common.model.InstanceDetailsResponse;
import org.rippleosi.common.model.SeriesDetailsResponse;
import org.rippleosi.common.model.StudyDetailsResponse;
import org.rippleosi.common.repo.Repository;
import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.RepoSourceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class AbstractOrthancService implements Repository {

    @Value("${repository.config.orthancServer:900}")
    private int priority;

    @Value("${orthancServer.address}")
    private String orthancServerAddress;

    @Autowired
    private DicomRequestProxy dicomRequestProxy;

    @Override
    public RepoSourceType getSource() {
        return RepoSourceTypes.ORTHANC;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @SuppressWarnings("unchecked")
    protected List<String> findAllStudiesIds() {
        ResponseEntity response = dicomRequestProxy.getWithoutSession(studiesListUri(), List.class);

        List<String> results = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            results = (List<String>) response.getBody();
        }

        return results;
    }

    protected StudyDetailsResponse findStudyDetails(String studyId) {
        ResponseEntity<StudyDetailsResponse> response = dicomRequestProxy.getWithoutSession(studyDetailsUri(studyId),
                                                                                            StudyDetailsResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return new StudyDetailsResponse();
    }

    protected SeriesDetailsResponse findSeriesDetails(String seriesId) {
        ResponseEntity<SeriesDetailsResponse> response = dicomRequestProxy.getWithoutSession(seriesDetailsUri(seriesId),
                                                                                             SeriesDetailsResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return new SeriesDetailsResponse();
    }

    protected InstanceDetailsResponse findInstanceDetails(String instanceId) {
        ResponseEntity<InstanceDetailsResponse> response = dicomRequestProxy.getWithoutSession(instanceDetailsUri(instanceId),
                                                                                               InstanceDetailsResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return new InstanceDetailsResponse();
    }

    private String studiesListUri() {
        return UriComponentsBuilder.fromHttpUrl(orthancServerAddress + "/studies")
                                   .build()
                                   .toUriString();
    }

    private String studyDetailsUri(String studyId) {
        return UriComponentsBuilder.fromHttpUrl(orthancServerAddress + "/studies")
                                   .path("/" + studyId)
                                   .build()
                                   .toUriString();
    }

    private String seriesDetailsUri(String seriesId) {
        return UriComponentsBuilder.fromHttpUrl(orthancServerAddress + "/series")
                                   .path("/" + seriesId)
                                   .build()
                                   .toUriString();
    }

    private String instanceDetailsUri(String instanceId) {
        return UriComponentsBuilder.fromHttpUrl(orthancServerAddress + "/instances")
                                   .path("/" + instanceId)
                                   .build()
                                   .toUriString();
    }
}
