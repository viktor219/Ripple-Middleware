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
package org.rippleosi.patient.heightandweight.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.rippleosi.common.service.strategies.query.list.AbstractListGetQueryStrategy;
import org.rippleosi.patient.heightandweight.model.HeightAndWeightDetails;

public class HeightAndWeightSummaryQueryStrategy extends AbstractListGetQueryStrategy<HeightAndWeightDetails> {

    HeightAndWeightSummaryQueryStrategy(String patientId) {
        super(patientId);
    }

    @Override
    public String getQuery(String namespace, String patientId) {
        return "select a/uid/value as uid, " +
            "a_a/items[openEHR-EHR-OBSERVATION.height.v1]/data[at0001|history|]/events[at0002|Event|]/data[at0003]/items[at0004|Height|]/value/magnitude as height, " +
            "a_a/items[openEHR-EHR-OBSERVATION.height.v1]/data[at0001|history|]/origin/value as height_recorded, " +
            "a_a/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002|history|]/events[at0003|Any event|]/data[at0001]/items[at0004|Weight|]/value/magnitude as weight, " +
            "a_a/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002|history|]/origin/value as weight_recorded " +
            "from EHR e " +
            "contains COMPOSITION a " +
            "contains SECTION a_a[openEHR-EHR-SECTION.examination_findings_rcp.v1] " +
            "where a/name/value='Patient visit - Height_weight' " +
            "and e/ehr_status/subject/external_ref/namespace = '" + namespace + "' " +
            "and e/ehr_status/subject/external_ref/id/value = '" + patientId + "'";
    }

    @Override
    public List<HeightAndWeightDetails> transform(List<Map<String, Object>> resultSet) {
        return CollectionUtils.collect(resultSet, new HeightAndWeightDetailsTransformer(), new ArrayList<>());
    }
}
