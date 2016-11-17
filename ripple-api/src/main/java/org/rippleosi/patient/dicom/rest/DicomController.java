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
package org.rippleosi.patient.dicom.rest;

import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.patient.dicom.model.DicomInstanceId;
import org.rippleosi.patient.dicom.model.DicomInstanceSummary;
import org.rippleosi.patient.dicom.model.DicomSeriesDetails;
import org.rippleosi.patient.dicom.model.DicomSeriesSummary;
import org.rippleosi.patient.dicom.model.DicomStudySummary;
import org.rippleosi.patient.dicom.search.DicomSearch;
import org.rippleosi.patient.dicom.search.DicomSearchFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients/{patientId}/dicom")
public class DicomController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;
    
    @Autowired
    private DicomSearchFactory dicomSearchFactory;

    @RequestMapping(value = "/studies", method = RequestMethod.GET)
    public List<DicomStudySummary> findAllDicomStudies(@PathVariable("patientId") String patientId,
                                                       @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        DicomSearch dicomSearch = dicomSearchFactory.select(sourceType);

        return dicomSearch.findAllDicomStudies(patientId, sourceType);
    }

    @RequestMapping(value = "/studies/{studyId}/series", method = RequestMethod.GET)
    public DicomSeriesSummary findAllDicomSeriesInStudy(@PathVariable("patientId") String patientId,
                                                        @PathVariable("studyId") String studyId,
                                                        @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        DicomSearch dicomSearch = dicomSearchFactory.select(sourceType);

        return dicomSearch.findAllDicomSeriesInStudy(patientId, studyId, sourceType);
    }

    @RequestMapping(value = "/series/{seriesId}", method = RequestMethod.GET)
    public DicomSeriesDetails findSeriesDetails(@PathVariable("patientId") String patientId,
                                                @PathVariable("seriesId") String seriesId,
                                                @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        DicomSearch dicomSearch = dicomSearchFactory.select(sourceType);

        return dicomSearch.findSeriesDetails(patientId, seriesId, sourceType);
    }

    @RequestMapping(value = "/instances/{instanceId}", method = RequestMethod.GET)
    public DicomInstanceSummary findInstanceSummary(@PathVariable("patientId") String patientId,
                                                    @PathVariable("instanceId") String instanceId,
                                                    @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        DicomSearch dicomSearch = dicomSearchFactory.select(sourceType);

        return dicomSearch.findInstanceSummary(patientId, instanceId, sourceType);
    }

    @RequestMapping(value = "/series/{seriesId}/instance", method = RequestMethod.GET)
    public DicomInstanceId findFirstInstanceIdInSeries(@PathVariable("patientId") String patientId,
                                                       @PathVariable("seriesId") String seriesId,
                                                       @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        DicomSearch dicomSearch = dicomSearchFactory.select(sourceType);

        return dicomSearch.findFirstInstanceIdInSeries(patientId, seriesId, sourceType);
    }
}
