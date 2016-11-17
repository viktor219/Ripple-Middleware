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
package org.rippleosi.patient.labresults.search;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.Transformer;
import org.rippleosi.patient.labresults.model.LabResultDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class TestResultTransformer implements Transformer<Map<String, Object>, LabResultDetails.TestResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultTransformer.class);

    private String getValueAsString(Map<String, Object> input, String path) {
        try {
            Object value = PropertyUtils.getNestedProperty(input, path);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception ex) {
            LOGGER.debug("{}: {}", ex.getClass().getName(), ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    public LabResultDetails.TestResult transform(Map<String, Object> input) {

        LabResultDetails.TestResult testResult = new LabResultDetails.TestResult();

        String name = getValueAsString(input, "(items)[0].name.value");
        String value = getValueAsString(input, "(items)[0].value.magnitude");
        String units = getValueAsString(input, "(items)[0].value.units");
        String comment = getValueAsString(input, "(items)[1].value.value");

        testResult.setResult(name);
        testResult.setValue(value);
        testResult.setUnit(units);
        testResult.setComment(comment);

        return testResult;
    }
}
