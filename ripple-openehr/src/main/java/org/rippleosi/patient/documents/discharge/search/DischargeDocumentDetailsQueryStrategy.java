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

import java.util.List;
import java.util.Map;

import org.rippleosi.common.exception.DataNotFoundException;
import org.rippleosi.common.service.strategies.query.details.AbstractDetailsGetQueryStrategy;
import org.rippleosi.patient.documents.discharge.model.DischargeDocumentDetails;

/**
 */
public class DischargeDocumentDetailsQueryStrategy extends AbstractDetailsGetQueryStrategy<DischargeDocumentDetails> {

    private final String documentId;

    DischargeDocumentDetailsQueryStrategy(String patientId, String documentId) {
        super(patientId);
        this.documentId = documentId;
    }

    @Override
    public String getQuery(String namespace, String patientId) {
        return "select a/uid/value as uid, " +
            "a/name/value as documentType, " +
            "a/context/start_time/value as dischargeDate, " +
            "a/composer/name as authorName, " +
            "a/composer/external_ref/id/value as authorId, " +
            "a/composer/external_ref/id/scheme as authorIdScheme, " +
            "a/context/health_care_facility/name as facility, " +
            "a/content[openEHR-EHR-SECTION.admission_details_rcp.v1|Admission details|]/items[openEHR-EHR-ADMIN_ENTRY.inpatient_admission_uk.v1|Inpatient admission|]/data[at0001]/items[at0002|Date of admission|]/value/value as dateOfAdmission, " +
            "a/content[openEHR-EHR-SECTION.adhoc.v1, 'Discharge details']/items[openEHR-EHR-ADMIN_ENTRY.discharge_details_uk_v1.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.individual_professional.v1, 'Responsible professional']/items[openEHR-EHR-CLUSTER.person_name.v1, 'Professional name']/items[at0001, 'Name']/value/value as professionalName, " +
            "a/content[openEHR-EHR-SECTION.adhoc.v1, 'Discharge details']/items[openEHR-EHR-ADMIN_ENTRY.discharge_details_uk_v1.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.individual_professional.v1, 'Responsible professional']/items[at0011]/value/id as professionalId, " +
            "a/content[openEHR-EHR-SECTION.adhoc.v1, 'Discharge details']/items[openEHR-EHR-ADMIN_ENTRY.discharge_details_uk_v1.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.individual_professional.v1, 'Responsible professional']/items[at0011]/value/type as professionalType, " +
            "a/content[openEHR-EHR-SECTION.adhoc.v1, 'Discharge details']/items[openEHR-EHR-ADMIN_ENTRY.discharge_details_uk_v1.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.organisation.v1, 'Discharging organisation']/items[at0001]/value/value as dischargeOrganisation, " +
            "a/content[openEHR-EHR-SECTION.adhoc.v1, 'Discharge details']/items[openEHR-EHR-ADMIN_ENTRY.discharge_details_uk_v1.v1]/data[at0001]/items[at0006]/value/value as dischargeDateTime, " +
            "a/content[openEHR-EHR-SECTION.clinical_summary_rcp.v1]/items[openEHR-EHR-EVALUATION.clinical_synopsis.v1]/data[at0001]/items[at0002]/value/value as synopsis " +
            "from EHR e contains COMPOSITION a[openEHR-EHR-COMPOSITION.transfer_summary.v1] " +
            whereClause(namespace, patientId);
    }

    public String getIdentifierQuery(String namespace, String patientId) {
        return "select a/uid/value as uid, b_a/items[at0016|MRN|]/value/id as patientIdMrn, b_a/items[at0016|MRN|]/value/type as patientIdMrnType " +
            "from EHR e contains COMPOSITION a[openEHR-EHR-COMPOSITION.transfer_summary.v1] contains CLUSTER b_a[openEHR-EHR-CLUSTER.individual_personal_uk.v1] " +
            whereClause(namespace, patientId);
    }

    public String getDiagnosisQuery(String namespace, String patientId) {
        return "select a/uid/value as uid, " +
            "a/content[openEHR-EHR-SECTION.diagnoses_rcp.v1]/items[openEHR-EHR-EVALUATION.problem_diagnosis.v1]/data[at0001]/items[at0002]/value/value as diagnosisName, " +
            "a/content[openEHR-EHR-SECTION.diagnoses_rcp.v1]/items[openEHR-EHR-EVALUATION.problem_diagnosis.v1]/data[at0001]/items[at0003, 'Diagnosis Date/time']/value/value as diagnosisTime " +
            "from EHR e " +
            "contains COMPOSITION a[openEHR-EHR-COMPOSITION.transfer_summary.v1] " +
            whereClause(namespace, patientId);
    }

    private String whereClause(String namespace, String patientId) {
        return "where a/name/value='Discharge summary' " +
            "and a/uid/value='" + documentId + "' " +
            "and e/ehr_status/subject/external_ref/namespace='" + namespace + "' " +
            "and e/ehr_status/subject/external_ref/id/value='" + patientId + "'";
    }

    @Override
    public DischargeDocumentDetails transform(List<Map<String, Object>> resultSet) {
        if (resultSet.isEmpty()) {
            throw new DataNotFoundException("No results found");
        }

        Map<String, Object> data = resultSet.get(0);

        return new DischargeDocumentDetailsTransformer().transform(data);
    }

    public DischargeDocumentDetails transformPatientIdentifiers(List<Map<String, Object>> resultSet, DischargeDocumentDetails currentDocumentDetails) {
        if (!resultSet.isEmpty()) {
            currentDocumentDetails = new DischargeDocumentDetailsTransformer().transformIdentifiers(resultSet, currentDocumentDetails);
        }

        return currentDocumentDetails;
    }

    public DischargeDocumentDetails transformDiagnosis(List<Map<String, Object>> resultSet, DischargeDocumentDetails currentDocumentDetails) {
        if (!resultSet.isEmpty()) {
            currentDocumentDetails = new DischargeDocumentDetailsTransformer().transformDiagnosis(resultSet, currentDocumentDetails);
        }

        return currentDocumentDetails;
    }
}
