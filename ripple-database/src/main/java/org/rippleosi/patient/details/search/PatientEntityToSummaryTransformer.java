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
package org.rippleosi.patient.details.search;

import org.apache.commons.collections4.Transformer;
import org.rippleosi.patient.details.model.PatientEntity;
import org.rippleosi.patient.summary.model.PatientSummary;
import org.springframework.stereotype.Component;

@Component
public class PatientEntityToSummaryTransformer implements Transformer<PatientEntity, PatientSummary> {

    @Override
    public PatientSummary transform(PatientEntity patientEntity) {
        PatientSummary patientSummary = new PatientSummary();

        String name = patientEntity.getFirstName() + " " + patientEntity.getLastName();
        String address = patientEntity.getAddress1() + ", " +
            patientEntity.getAddress2() + ", " +
            patientEntity.getAddress3() + ", " +
            patientEntity.getPostcode();

        patientSummary.setId(patientEntity.getNhsNumber());
        patientSummary.setName(name);
        patientSummary.setAddress(address);
        patientSummary.setDateOfBirth(patientEntity.getDateOfBirth());
        patientSummary.setGender(patientEntity.getGender());
        patientSummary.setNhsNumber(patientEntity.getNhsNumber());
        patientSummary.setDepartment(patientEntity.getDepartment().getDepartment());

        return patientSummary;
    }
}
