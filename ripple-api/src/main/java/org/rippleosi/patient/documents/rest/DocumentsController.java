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
package org.rippleosi.patient.documents.rest;

import java.util.Collections;
import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.common.util.DateFormatter;
import org.rippleosi.patient.documents.common.model.AbstractDocumentSummary;
import org.rippleosi.patient.documents.common.model.GenericDocument;
import org.rippleosi.patient.documents.common.store.DocumentStore;
import org.rippleosi.patient.documents.common.store.DocumentStoreFactory;
import org.rippleosi.patient.documents.discharge.model.DischargeDocumentDetails;
import org.rippleosi.patient.documents.discharge.search.DischargeDocumentSearch;
import org.rippleosi.patient.documents.discharge.search.DischargeDocumentSearchFactory;
import org.rippleosi.patient.documents.referral.model.ReferralDocumentDetails;
import org.rippleosi.patient.documents.referral.search.ReferralDocumentSearch;
import org.rippleosi.patient.documents.referral.search.ReferralDocumentSearchFactory;
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
@RequestMapping("/patients/{patientId}/documents")
public class DocumentsController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;

    @Autowired
    private DocumentStoreFactory documentStoreFactory;

    @Autowired
    private DischargeDocumentSearchFactory dischargeDocumentSearchFactory;

    @Autowired
    private ReferralDocumentSearchFactory referralDocumentSearchFactory;

    @RequestMapping(value = "/referral", method = RequestMethod.POST, consumes = "application/xml")
    public void createReferral(@PathVariable("patientId") String patientId,
                               @RequestParam(required = false) String source,
                               @RequestBody String body) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);

        GenericDocument document = new GenericDocument();
        document.setDocumentType("hl7Referral");
        document.setOriginalSource(source);
        document.setDocumentContent(body);

        DocumentStore contactStore = documentStoreFactory.select(sourceType);
        contactStore.create(patientId, document);
    }

    @RequestMapping(value = "/discharge", method = RequestMethod.POST, consumes = "application/xml")
    public void createDischarge(@PathVariable("patientId") String patientId,
                                @RequestParam(required = false) String source,
                                @RequestBody String body) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);

        GenericDocument document = new GenericDocument();
        document.setDocumentType("hl7Discharge");
        document.setOriginalSource(source);
        document.setDocumentContent(body);

        DocumentStore contactStore = documentStoreFactory.select(sourceType);
        contactStore.create(patientId, document);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<AbstractDocumentSummary> findAllDocuments(@PathVariable("patientId") String patientId,
                                                          @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);

        DischargeDocumentSearch dischargeDocumentSearch = dischargeDocumentSearchFactory.select(sourceType);
        List<AbstractDocumentSummary> documents = dischargeDocumentSearch.findAllDischargeDocuments(patientId);

        ReferralDocumentSearch referralDocumentSearch = referralDocumentSearchFactory.select(sourceType);
        documents.addAll(referralDocumentSearch.findAllReferralDocuments(patientId));

        // Sort by date
        Collections.sort(documents, (gds1, gds2) ->
            DateFormatter.toDate(gds2.getDocumentDate()).compareTo(DateFormatter.toDate(gds1.getDocumentDate())));

        return documents;
    }

    @RequestMapping(value = "/discharge/{documentId}", method = RequestMethod.GET)
    public DischargeDocumentDetails findDischargeDocument(@PathVariable("patientId") String patientId,
                                                          @PathVariable("documentId") String documentId,
                                                          @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        DischargeDocumentSearch documentSearch = dischargeDocumentSearchFactory.select(sourceType);

        return documentSearch.findDischargeDocument(patientId, documentId);
    }

    @RequestMapping(value = "/referral/{documentId}", method = RequestMethod.GET)
    public ReferralDocumentDetails findReferralDocument(@PathVariable("patientId") String patientId,
                                                        @PathVariable("documentId") String documentId,
                                                        @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ReferralDocumentSearch documentSearch = referralDocumentSearchFactory.select(sourceType);

        return documentSearch.findReferralDocument(patientId, documentId);
    }
}
