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
package org.rippleosi.patient.documents.referral.search;

import java.util.List;

import org.rippleosi.common.service.AbstractOpenEhrService;
import org.rippleosi.patient.documents.common.model.AbstractDocumentSummary;
import org.rippleosi.patient.documents.referral.model.ReferralDocumentDetails;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class OpenEHRReferralDocumentSearch extends AbstractOpenEhrService implements ReferralDocumentSearch {

    @Override
    public List<AbstractDocumentSummary> findAllReferralDocuments(String patientId) {
        ReferralDocumentSummaryQueryStrategy query = new ReferralDocumentSummaryQueryStrategy(patientId);

        return findData(query);
    }

    @Override
    public ReferralDocumentDetails findReferralDocument(String patientId, String documentId) {
        ReferralDocumentDetailsQueryStrategy query = new ReferralDocumentDetailsQueryStrategy(patientId, documentId);

        return findData(query);
    }
}
