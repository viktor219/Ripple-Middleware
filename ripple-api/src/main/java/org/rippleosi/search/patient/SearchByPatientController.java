/*
 *   Copyright 2015 Ripple OSI
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

package org.rippleosi.search.patient;

import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.patient.summary.model.PatientSummary;
import org.rippleosi.patient.summary.search.PatientSearch;
import org.rippleosi.patient.summary.search.PatientSearchFactory;
import org.rippleosi.search.patient.stats.PatientStatsSearch;
import org.rippleosi.search.patient.stats.PatientStatsSearchFactory;
import org.rippleosi.search.patient.stats.model.PatientTableQuery;
import org.rippleosi.search.patient.stats.model.SearchTableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search/patient")
public class SearchByPatientController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;
    @Autowired
    private PatientSearchFactory patientSearchFactory;

    @Autowired
    private PatientStatsSearchFactory patientStatsSearchFactory;

    @RequestMapping(value = "/table", method = RequestMethod.POST)
    public SearchTableResults findPatientTableData(@RequestParam(required = false) String patientSource,
                                                   @RequestParam(required = false) String patientDataSource,
                                                   @RequestBody PatientTableQuery tableQuery) {
        final RepoSourceType patientSourceType = repoSourceLookup.lookup(patientSource);
        PatientSearch patientSearch = patientSearchFactory.select(patientSourceType);
        List<PatientSummary> patientSummaries = patientSearch.findPatientsBySearchString(tableQuery);

        final RepoSourceType patientDataSourceType = repoSourceLookup.lookup(patientDataSource);
        PatientStatsSearch patientStatsSearch = patientStatsSearchFactory.select(patientDataSourceType);
        SearchTableResults associatedData = patientStatsSearch.findAssociatedPatientData(tableQuery, patientSummaries);

        Long total = patientSearch.countPatientsBySearchString(tableQuery);
        associatedData.setTotalPatients(String.valueOf(total));

        return associatedData;
    }
}
