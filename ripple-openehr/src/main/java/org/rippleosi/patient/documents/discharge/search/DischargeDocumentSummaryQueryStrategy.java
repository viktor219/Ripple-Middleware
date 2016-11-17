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
package org.rippleosi.patient.documents.discharge.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.rippleosi.common.service.strategies.query.list.AbstractListGetQueryStrategy;
import org.rippleosi.patient.documents.common.model.AbstractDocumentSummary;

/**
 */
public class DischargeDocumentSummaryQueryStrategy extends AbstractListGetQueryStrategy<AbstractDocumentSummary> {

    DischargeDocumentSummaryQueryStrategy(String patientId) {
        super(patientId);
    }

    @Override
    public String getQuery(String namespace, String patientId) {
        return "select a/uid/value as uid, " +
            "a/name/value as documentType, " +
            "a/context/start_time/value as dischargeDate " +
            "from EHR e " +
            "contains COMPOSITION a[openEHR-EHR-COMPOSITION.transfer_summary.v1] " +
            "where a/name/value='Discharge summary' " +
            "and e/ehr_status/subject/external_ref/namespace = '" + namespace + "' " +
            "and e/ehr_status/subject/external_ref/id/value = '" + patientId + "'";
    }

    @Override
    public List<AbstractDocumentSummary> transform(List<Map<String, Object>> resultSet) {
        return CollectionUtils.collect(resultSet, new DischargeDocumentSummaryTransformer(), new ArrayList<>());
    }
}
