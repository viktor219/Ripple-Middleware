/*
 *  Copyright 2015 Ripple OSI
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
 *
 */
package org.rippleosi.patient.medication.rest;

import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.RepoSourceTypes;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.patient.medication.model.MedicationDetails;
import org.rippleosi.patient.medication.model.MedicationHeadline;
import org.rippleosi.patient.medication.model.MedicationSummary;
import org.rippleosi.patient.medication.search.MedicationSearch;
import org.rippleosi.patient.medication.search.MedicationSearchFactory;
import org.rippleosi.patient.medication.store.MedicationStore;
import org.rippleosi.patient.medication.store.MedicationStoreFactory;
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
@RequestMapping("/patients/{patientId}/medications")
public class MedicationsController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;
             
    @Autowired
    private MedicationSearchFactory medicationSearchFactory;

    @Autowired
    private MedicationStoreFactory medicationStoreFactory;

    @RequestMapping(method = RequestMethod.GET)
    public List<MedicationSummary> findAllMedications(@PathVariable("patientId") String patientId,
                                                      @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MedicationSearch etherCISSearch = medicationSearchFactory.select(sourceType);
        List<MedicationSummary> medication = etherCISSearch.findAllMedication(patientId);

        MedicationSearch openehrSearch = medicationSearchFactory.select(RepoSourceTypes.MARAND);
        medication.addAll(openehrSearch.findAllMedication(patientId));

        return medication;
    }

    @RequestMapping(value = "/headlines", method = RequestMethod.GET)
    public List<MedicationHeadline> findMedicationHeadlines(@PathVariable("patientId") String patientId,
                                                            @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MedicationSearch etherCISSearch = medicationSearchFactory.select(sourceType);
        List<MedicationHeadline> medicationHeadlines = etherCISSearch.findMedicationHeadlines(patientId);

        MedicationSearch openehrSearch = medicationSearchFactory.select(RepoSourceTypes.MARAND);
        medicationHeadlines.addAll(openehrSearch.findMedicationHeadlines(patientId));

        return medicationHeadlines;
    }

    @RequestMapping(value = "/{medicationId}", method = RequestMethod.GET)
    public MedicationDetails findMedication(@PathVariable("patientId") String patientId,
                                            @PathVariable("medicationId") String medicationId,
                                            @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MedicationSearch medicationSearch = medicationSearchFactory.select(sourceType);

        return medicationSearch.findMedication(patientId, medicationId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createMedication(@PathVariable("patientId") String patientId,
                                 @RequestParam(required = false) String source,
                                 @RequestBody MedicationDetails medication) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MedicationStore medicationStore = medicationStoreFactory.select(sourceType);

        medicationStore.create(patientId, medication);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateMedication(@PathVariable("patientId") String patientId,
                                 @RequestParam(required = false) String source,
                                 @RequestBody MedicationDetails medication) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        MedicationStore medicationStore = medicationStoreFactory.select(sourceType);

        medicationStore.update(patientId, medication);
    }
}
