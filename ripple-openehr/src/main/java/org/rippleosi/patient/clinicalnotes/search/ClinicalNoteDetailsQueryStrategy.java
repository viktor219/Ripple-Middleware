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
package org.rippleosi.patient.clinicalnotes.search;

import java.util.List;
import java.util.Map;

import org.rippleosi.common.exception.DataNotFoundException;
import org.rippleosi.common.service.strategies.query.details.AbstractDetailsGetQueryStrategy;
import org.rippleosi.patient.clinicalnotes.model.ClinicalNoteDetails;

public class ClinicalNoteDetailsQueryStrategy extends AbstractDetailsGetQueryStrategy<ClinicalNoteDetails> {

    private final String clinicalNoteId;

    ClinicalNoteDetailsQueryStrategy(String patientId, String clinicalNoteId) {
        super(patientId);
        this.clinicalNoteId = clinicalNoteId;
    }

    @Override
    public String getQuery(String namespace, String patientId) {
        return "select a/uid/value as uid, " +
            "a/context/start_time/value as date_created, " +
            "a/composer/name as author, " +
            "a_a/name/value as type, " +
            "a_a/data[at0001]/items[at0002|Notes|]/value/value as note " +
            "from EHR e " +
            "contains COMPOSITION a[openEHR-EHR-COMPOSITION.encounter.v1] " +
            "contains EVALUATION a_a[openEHR-EHR-EVALUATION.clinical_synopsis.v1] " +
            "where a/name/value='Clinical Notes' " +
            "and a/uid/value='" + clinicalNoteId + "' " +
            "and e/ehr_status/subject/external_ref/namespace = '" + namespace + "' " +
            "and e/ehr_status/subject/external_ref/id/value = '" + patientId + "'";
    }

    @Override
    public ClinicalNoteDetails transform(List<Map<String, Object>> resultSet) {
        if (resultSet.isEmpty()) {
            throw new DataNotFoundException("No results found");
        }

        final Map<String, Object> data = resultSet.get(0);

        return new ClinicalNoteDetailsTransformer().transform(data);
    }
}
