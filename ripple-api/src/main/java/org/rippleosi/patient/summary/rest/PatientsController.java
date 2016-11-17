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
package org.rippleosi.patient.summary.rest;

import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.patient.summary.model.PatientDetails;
import org.rippleosi.patient.summary.model.PatientQueryParams;
import org.rippleosi.patient.summary.model.PatientSummary;
import org.rippleosi.patient.summary.search.PatientSearch;
import org.rippleosi.patient.summary.search.PatientSearchFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 */
@RestController
@RequestMapping("/patients")
public class PatientsController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;
    
    @Autowired
    private PatientSearchFactory patientSearchFactory;

    @RequestMapping(method = RequestMethod.GET)
    public List<PatientSummary> findAllPatients(@RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        PatientSearch patientSearch = patientSearchFactory.select(sourceType);

        return patientSearch.findAllPatients();
    }

    @RequestMapping(value = "/{patientId}", method = RequestMethod.GET)
    public PatientDetails findPatientByNHSNumber(@PathVariable("patientId") String patientId,
                                                 @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        PatientSearch patientSearch = patientSearchFactory.select(sourceType);

        return patientSearch.findPatient(patientId);
    }

    @RequestMapping(value = "/advancedSearch", method = RequestMethod.POST)
    public List<PatientSummary> findPatientsByQueryObject(@RequestParam(required = false) String source,
                                                          @RequestBody PatientQueryParams queryParams) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        PatientSearch patientSearch = patientSearchFactory.select(sourceType);

        return patientSearch.findPatientsByQueryObject(queryParams);
    }
}
