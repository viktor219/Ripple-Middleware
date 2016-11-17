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
package org.rippleosi.patient.clinicalnotes.rest;

import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.patient.clinicalnotes.model.ClinicalNoteDetails;
import org.rippleosi.patient.clinicalnotes.model.ClinicalNoteSummary;
import org.rippleosi.patient.clinicalnotes.search.ClinicalNoteSearch;
import org.rippleosi.patient.clinicalnotes.search.ClinicalNoteSearchFactory;
import org.rippleosi.patient.clinicalnotes.store.ClinicalNoteStore;
import org.rippleosi.patient.clinicalnotes.store.ClinicalNoteStoreFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("patients/{patientId}/clinicalNotes")
public class ClinicalNotesController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;

    @Autowired
    private ClinicalNoteSearchFactory clinicalNoteSearchFactory;

    @Autowired
    private ClinicalNoteStoreFactory clinicalNoteStoreFactory;

    @RequestMapping(method = RequestMethod.GET)
    public List<ClinicalNoteSummary> findAllClinicalNotes(@PathVariable("patientId") String patientId,
                                                          @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ClinicalNoteSearch search = clinicalNoteSearchFactory.select(sourceType);

        return search.findAllClinicalNotes(patientId);
    }

    @RequestMapping(value = "/{clinicalNoteId}", method = RequestMethod.GET)
    public ClinicalNoteDetails findClinicalNote(@PathVariable("patientId") String patientId,
                                                @PathVariable("clinicalNoteId") String clinicalNoteId,
                                                @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ClinicalNoteSearch search = clinicalNoteSearchFactory.select(sourceType);

        return search.findClinicalNote(patientId, clinicalNoteId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createClinicalNote(@PathVariable("patientId") String patientId,
                                   @RequestParam(required = false) String source,
                                   @RequestBody ClinicalNoteDetails clinicalNote) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ClinicalNoteStore store = clinicalNoteStoreFactory.select(sourceType);

        store.create(patientId, clinicalNote);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateClinicalNote(@PathVariable("patientId") String patientId,
                                   @RequestParam(required = false) String source,
                                   @RequestBody ClinicalNoteDetails clinicalNote) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ClinicalNoteStore store = clinicalNoteStoreFactory.select(sourceType);

        store.update(patientId, clinicalNote);
    }
}
