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
package org.rippleosi.patient.mdtreports.rest;

import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.patient.mdtreports.model.MDTReportDetails;
import org.rippleosi.patient.mdtreports.model.MDTReportSummary;
import org.rippleosi.patient.mdtreports.search.MDTReportSearch;
import org.rippleosi.patient.mdtreports.search.MDTReportSearchFactory;
import org.rippleosi.patient.mdtreports.store.MDTReportStore;
import org.rippleosi.patient.mdtreports.store.MDTReportStoreFactory;
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
@RequestMapping("/patients/{patientId}/mdtreports")
public class MDTReportsController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;
    
    @Autowired
    private MDTReportSearchFactory mdtReportSearchFactory;

    @Autowired
    private MDTReportStoreFactory mdtReportStoreFactory;

    @RequestMapping(method = RequestMethod.GET)
    public List<MDTReportSummary> findAllMDTReports(@PathVariable("patientId") String patientId,
                                                    @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MDTReportSearch mdtReportSearch = mdtReportSearchFactory.select(sourceType);

        return mdtReportSearch.findAllMDTReports(patientId);
    }

    @RequestMapping(value = "/{reportId}", method = RequestMethod.GET)
    public MDTReportDetails findMDTReport(@PathVariable("patientId") String patientId,
                                          @PathVariable("reportId") String reportId,
                                          @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MDTReportSearch mdtReportSearch = mdtReportSearchFactory.select(sourceType);

        return mdtReportSearch.findMDTReport(patientId, reportId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createMDTReport(@PathVariable("patientId") String patientId,
                                @RequestParam(required = false) String source,
                                @RequestBody MDTReportDetails mdtReport) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MDTReportStore mdtReportStore = mdtReportStoreFactory.select(sourceType);

        mdtReportStore.create(patientId, mdtReport);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateMDTReport(@PathVariable("patientId") String patientId,
                                @RequestParam(required = false) String source,
                                @RequestBody MDTReportDetails mdtReport) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MDTReportStore mdtReportStore = mdtReportStoreFactory.select(sourceType);

        mdtReportStore.update(patientId, mdtReport);
    }
}
