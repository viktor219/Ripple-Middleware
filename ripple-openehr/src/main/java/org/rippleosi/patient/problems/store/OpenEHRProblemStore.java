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
package org.rippleosi.patient.problems.store;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Consume;
import org.apache.commons.lang3.StringUtils;
import org.rippleosi.common.service.AbstractOpenEhrService;
import org.rippleosi.common.service.strategies.store.CreateStrategy;
import org.rippleosi.common.service.strategies.store.DefaultStoreStrategy;
import org.rippleosi.common.service.strategies.store.UpdateStrategy;
import org.rippleosi.common.util.DateFormatter;
import org.rippleosi.patient.problems.model.ProblemDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class OpenEHRProblemStore extends AbstractOpenEhrService implements ProblemStore {

    @Value("${c4hOpenEHR.problemTemplate}")
    private String problemTemplate;

    private static final String PROBLEM_PREFIX = "problem_list/problems_and_issues:0/problem_diagnosis:0";

    @Override
    @Consume(uri = "activemq:Consumer.C4HOpenEHR.VirtualTopic.Marand.Problems.Create")
    public void create(String patientId, ProblemDetails problem) {

        Map<String, Object> content = createFlatJsonContent(problem);

        CreateStrategy createStrategy = new DefaultStoreStrategy(patientId, problemTemplate, content);

        createData(createStrategy);
    }

    @Override
    @Consume(uri = "activemq:Consumer.C4HOpenEHR.VirtualTopic.Marand.Problems.Update")
    public void update(String patientId, ProblemDetails problem) {

        Map<String, Object> content = createFlatJsonContent(problem);

        UpdateStrategy updateStrategy = new DefaultStoreStrategy(problem.getSourceId(), patientId, problemTemplate, content);

        updateData(updateStrategy);
    }

    private Map<String, Object> createFlatJsonContent(ProblemDetails problem) {

        Map<String, Object> content = new HashMap<>();

        content.put("ctx/language", "en");
        content.put("ctx/territory", "GB");

        String author = problem.getAuthor();
        content.put("ctx/composer_name", author != null ? author : "Dr Tony Shannon");

        if (StringUtils.isBlank(problem.getCode())) {
            problem.setCode("00001");
        }

        if (StringUtils.isBlank(problem.getTerminology())) {
            problem.setTerminology("SNOMED-CT");
        }

        String dateOfOnset = DateFormatter.toString(problem.getDateOfOnset());

        content.put(PROBLEM_PREFIX + "/problem_diagnosis|value", problem.getProblem());
        content.put(PROBLEM_PREFIX + "/clinical_description", problem.getDescription());
        content.put(PROBLEM_PREFIX + "/problem_diagnosis|code", problem.getCode());
        content.put(PROBLEM_PREFIX + "/problem_diagnosis|terminology", problem.getTerminology());
        content.put(PROBLEM_PREFIX + "/date_time_of_onset", dateOfOnset);

        return content;
    }
}
