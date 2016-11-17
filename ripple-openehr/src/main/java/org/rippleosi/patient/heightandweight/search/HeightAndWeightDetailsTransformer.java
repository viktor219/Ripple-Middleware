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

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.rippleosi.common.util.DateFormatter;
import org.rippleosi.patient.heightandweight.model.HeightAndWeightDetails;

public class HeightAndWeightDetailsTransformer implements Transformer<Map<String, Object>, HeightAndWeightDetails> {

    @Override
    public HeightAndWeightDetails transform(final Map<String, Object> input) {

        final String uid = MapUtils.getString(input, "uid");
        final Double height = MapUtils.getDouble(input, "height");
        final Double weight = MapUtils.getDouble(input, "weight");
        final String heightRecorded = MapUtils.getString(input, "height_recorded");
        final String weightRecorded = MapUtils.getString(input, "weight_recorded");

        final HeightAndWeightDetails heightAndWeight = new HeightAndWeightDetails();

        heightAndWeight.setSource("openehr");
        heightAndWeight.setSourceId(uid);

        heightAndWeight.setHeight(height);
        heightAndWeight.setWeight(weight);

        heightAndWeight.setHeightRecorded(DateFormatter.toDate(heightRecorded));
        heightAndWeight.setWeightRecorded(DateFormatter.toDate(weightRecorded));

        return heightAndWeight;
    }
}
