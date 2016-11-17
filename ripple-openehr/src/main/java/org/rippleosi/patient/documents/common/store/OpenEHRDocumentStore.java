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
package org.rippleosi.patient.documents.common.store;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Consume;
import org.rippleosi.common.service.AbstractOpenEhrService;
import org.rippleosi.common.service.strategies.store.CreateStrategy;
import org.rippleosi.common.service.strategies.store.DefaultStoreStrategy;
import org.rippleosi.common.util.DateFormatter;
import org.rippleosi.patient.documents.common.model.GenericDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

/**
 */
@Service
public class OpenEHRDocumentStore extends AbstractOpenEhrService implements DocumentStore {

    private static final Logger logger = LoggerFactory.getLogger(OpenEHRDocumentStore.class);

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat DATE_TIME_FORMAT_SHORT = new SimpleDateFormat("yyyyMMddHHmm");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Value("${c4hOpenEHR.referralDocumentTemplate}")
    private String referralDocumentTemplate;

    @Value("${c4hOpenEHR.dischargeDocumentTemplate}")
    private String dischargeDocumentTemplate;

    @Override
    @Consume(uri = "activemq:Consumer.C4HOpenEHR.VirtualTopic.Ripple.Documents.Create")
    public void create(String patientId, GenericDocument document) {

        Map<String, Object> content = null;
        String documentTemplate = null;

        if ("hl7Referral".equalsIgnoreCase(document.getDocumentType())) {
            content = createReferralFlatJsonContent(document);
            documentTemplate = referralDocumentTemplate;
        }
        else if ("hl7Discharge".equalsIgnoreCase(document.getDocumentType())) {
            content = createDischargeFlatJsonContent(document);
            documentTemplate = dischargeDocumentTemplate;
        }

        if (documentTemplate != null && content != null) {
            CreateStrategy createStrategy = new DefaultStoreStrategy(patientId, documentTemplate, content);
            createData(createStrategy);
        }
    }

    private Map<String, Object> createReferralFlatJsonContent(GenericDocument document) {

        final String REFERRAL_REQUEST_PREFIX = "referral/referral_details/service_request/";
        final String REFERRAL_STATUS_PREFIX = "referral/referral_details/referral_status/";

        String hl7DocumentXML = (String) document.getDocumentContent();

        Map<String, Object> content = new HashMap<>();

        try {
            DocumentBuilder xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document hl7Document = xmlDocumentBuilder.parse(new ByteArrayInputStream(hl7DocumentXML.getBytes(StandardCharsets.UTF_8)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            content.put("ctx/language", "en");
            content.put("ctx/territory", "GB");
            content.put("ctx/id_namespace", "iEHR");
            content.put("ctx/id_scheme", "iEHR");

            if (!evaluateXPath(xPath, "//*:MSH/*:MSH.4", hl7Document).isEmpty()) {
                content.put("ctx/composer_name", evaluateXPath(xPath, "//*:MSH/*:MSH.4/*:HD.1", hl7Document));
                content.put("ctx/health_care_facility|id", evaluateXPath(xPath, "//*:MSH.4/*:HD.2", hl7Document));
            }
            if (!(evaluateXPath(xPath, "//*:MSH/*:MSH.6", hl7Document)).isEmpty()) {
                content.put(REFERRAL_REQUEST_PREFIX + "referred_to_provider/identifier", evaluateXPath(xPath, "//*:MSH.6/*:HD.1", hl7Document));
            }
            if (!(evaluateXPath(xPath, "//*:MSH/*:MSH.7", hl7Document)).isEmpty()) {
                String time = evaluateXPath(xPath, "//*:MSH.7/*:TS.1", hl7Document);
                content.put("ctx/time", parseDateTime(time));
            }

            content.put(REFERRAL_REQUEST_PREFIX + "referral_control_number", evaluateXPath(xPath, "//*:MSH.10", hl7Document));
            content.put(REFERRAL_REQUEST_PREFIX + "request:0/timing", "Boilerplate timing string");

            String referralStatus = evaluateXPath(xPath, "//*:RF1.1/*:CE.1", hl7Document);
            if (!referralStatus.isEmpty()) {
                if (referralStatus.equalsIgnoreCase("P")) {
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|code", "526");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|value", "planned");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|code", "at0002");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|value", "Referral planned");
                }
                else if (referralStatus.equalsIgnoreCase("A")) {
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|code", "529");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|value", "scheduled");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|code", "at0003");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|value", "Appoinment scheduled");
                }
                else if (referralStatus.equalsIgnoreCase("R")) {
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|code", "528");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|value", "cancelled");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|code", "at009");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|value", "Referral cancelled");
                }
                else if (referralStatus.equalsIgnoreCase("E")) {
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|code", "531");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/current_state|value", "aborted");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|code", "at023");
                    content.put(REFERRAL_STATUS_PREFIX + "ism_transition/careflow_step|value", "Referral expired");
                }
            }

            if (!(evaluateXPath(xPath, "//*:RF1.3", hl7Document)).isEmpty()) {
                String referralType = evaluateXPath(xPath, "//*:RF1.3/*:CE.1", hl7Document);

                content.put(REFERRAL_STATUS_PREFIX + "referral_type", referralType);
                content.put(REFERRAL_REQUEST_PREFIX + "request:0/referral_type", referralType);
                content.put("referral/referral_details/service_request/narrative", referralType);

                String priority = evaluateXPath(xPath, "//*:RF1.3/*:CE.2", hl7Document);

                if (priority.equalsIgnoreCase("U")) {
                    content.put(REFERRAL_REQUEST_PREFIX + "request:0/priority|code", "at0136");
                }
                else if (priority.equalsIgnoreCase("E")) {
                    content.put(REFERRAL_REQUEST_PREFIX + "request:0/priority|code", "at0137");
                }
                else if (priority.equalsIgnoreCase("R")) {
                    content.put(REFERRAL_REQUEST_PREFIX + "request:0/priority|code", "at0138");
                }

                content.put(REFERRAL_REQUEST_PREFIX + "request:0/comments", priority);
            }

            content.put(REFERRAL_REQUEST_PREFIX + "referring_provider/identifier", evaluateXPath(xPath, "//*:RF1.6/*:EI.1", hl7Document));

            if (!(evaluateXPath(xPath, "//*:RF1.7", hl7Document)).isEmpty()) {
                String time = evaluateXPath(xPath, "//*:RF1.7/*:TS.1", hl7Document);

                content.put(REFERRAL_STATUS_PREFIX + "time", parseDateTime(time));
            }

            String organisationName = evaluateXPath(xPath, "//*:REF_I12.PROVIDER_CONTACT[1]/*:PRD/*:PRD.4/*:PL.1", hl7Document);
            if (!organisationName.isEmpty()) {
                content.put("ctx/health_care_facility|name", organisationName);
                content.put(REFERRAL_REQUEST_PREFIX + "distribution:0/individual_recipient:0/gp/name_of_organisation", organisationName);
                content.put(REFERRAL_REQUEST_PREFIX + "referring_provider/name_of_organisation", organisationName);
            }

            String prd5XPath = "//*:REF_I12.PROVIDER_CONTACT[1]/*:PRD/*:PRD.5";
            if (!(evaluateXPath(xPath, prd5XPath, hl7Document)).isEmpty()) {
                int prd5Count = evaluateIntegerXPath(xPath, "count(" + prd5XPath + ")", hl7Document);

                for (int prd5index = 1; prd5index <= prd5Count; prd5index++) {
                    String contactValue = evaluateXPath(xPath, prd5XPath + "[" + prd5index + "]/*:XTN.1", hl7Document);
                    String contactType = evaluateXPath(xPath, prd5XPath + "[" + prd5index + "]/*:XTN.2", hl7Document).toLowerCase();

                    if (contactType.equalsIgnoreCase("wpn")) {
                        content.put(REFERRAL_REQUEST_PREFIX + "distribution/individual_recipient/gp/wpn/work_number", contactValue);
                    }
                    else if (contactType.equalsIgnoreCase("emr")) {
                        content.put(REFERRAL_REQUEST_PREFIX + "distribution/individual_recipient/gp/emr/emergency_number", contactValue);
                    }
                    else if (contactType.equalsIgnoreCase("net")) {
                        content.put(REFERRAL_REQUEST_PREFIX + "distribution/individual_recipient/gp/net/internet", contactValue);
                    }
                }
            }

            String referringProviderId = evaluateXPath(xPath, "//*:REF_I12.PROVIDER_CONTACT[1]/*:PRD/*:PRD.7/*:PI.1", hl7Document);
            if (!(referringProviderId.isEmpty())) {
                content.put(REFERRAL_REQUEST_PREFIX + "referring_provider/identifier", referringProviderId);
            }

            String referringProviderName = evaluateXPath(xPath, "//*:REF_I12.PROVIDER_CONTACT[2]/*:PRD/*:PRD.3/*:XAD.2", hl7Document);
            if (!referringProviderName.isEmpty()) {
                content.put(REFERRAL_REQUEST_PREFIX + "referred_to_provider/name_of_organisation", referringProviderName);
            }

            String observationsXPath = "//*:REF_I12.OBSERVATION";
            int numberOfObservationSections = evaluateIntegerXPath(xPath, "count(" + observationsXPath + ")", hl7Document);

            for (int obsSect = 1; obsSect <= numberOfObservationSections; obsSect++) {
                String obsSectionXPath = observationsXPath + "[" + obsSect + "]";
                int numberOfResultNotes = evaluateIntegerXPath(xPath, "count(" + obsSectionXPath + "/*:REF_I12.RESULTS_NOTES)", hl7Document);

                int medicationIndex = 0;
                int allergyIndex = 0;
                int referralReasonIndex = 0;
                int surgicalProcIndex = 0;
                int familyHistoryIndex = 0;
                int pastIllnessIndex = 0;

                for (int resNoteIndex = 1; resNoteIndex <= numberOfResultNotes; resNoteIndex++) {
                    String resultsNotesXPath = obsSectionXPath + "/*:REF_I12.RESULTS_NOTES[" + resNoteIndex + "]";

                    //Loop through all the result notes and add the nodes for the results.
                    String type = evaluateXPath(xPath, resultsNotesXPath + "/*:OBX/*:OBX.3/*:CE.2", hl7Document).toLowerCase();
                    String value = evaluateXPath(xPath, resultsNotesXPath + "/*:OBX/*:OBX.5", hl7Document);
                    String units = evaluateXPath(xPath, resultsNotesXPath + "/*:OBX/*:OBX.6/*:CE.1", hl7Document);
                    String timeValue = evaluateXPath(xPath, resultsNotesXPath + "/*:OBX/*:OBX.14/*:TS.1", hl7Document);

                    if (timeValue.length() == 8) {
                        timeValue = DateFormatter.toDateTimeString(DATE_FORMAT.parse(timeValue));
                    }
                    else if (timeValue.length() == 12) {
                        timeValue = DateFormatter.toDateTimeString(DATE_TIME_FORMAT_SHORT.parse(timeValue));
                    }
                    else if (timeValue.length() == 14) {
                        timeValue = DateFormatter.toDateTimeString(DATE_TIME_FORMAT.parse(timeValue));
                    }

                    switch (type) {
                        case "reason for referral":
                            content.put(REFERRAL_REQUEST_PREFIX + "request:" + referralReasonIndex + "/reason_for_referral", value);
                            referralReasonIndex++;
                            break;

                        case "previous hospital attendance":
                            boolean previousHospitalAttendance = "No".equalsIgnoreCase(value);
                            content.put("referral/history_of_present_illness/hospital_attendances_summary/previous_attendances", previousHospitalAttendance);
                            break;

                        case "history of present illness":
                            content.put("referral/history_of_present_illness/story_history/history_of_present_illness", value);
                            break;

                        case "history of surgical procedures":
                            if ("NIL".equalsIgnoreCase(value)) {
                                content.put("referral/history_of_surgical_procedures/exclusion_of_a_procedure/exclusion_statement", "No significant procedures");
                                content.put("referral/history_of_surgical_procedures/exclusion_of_a_procedure/date_last_updated", timeValue);
                            }
                            else {
                                content.put("referral/history_of_surgical_procedures/procedure:" + surgicalProcIndex + "/ism_transition/current_state|code", "532");
                                content.put("referral/history_of_surgical_procedures/procedure:" + surgicalProcIndex + "/ism_transition/current_state|value", "completed");
                                content.put("referral/history_of_surgical_procedures/procedure:" + surgicalProcIndex + "/history_of_surgical_procedure", value);
                                content.put("referral/history_of_surgical_procedures/procedure:" + surgicalProcIndex + "/time", timeValue);
                                surgicalProcIndex++;
                            }
                            break;

                        case "history of allergies":
                            if ("NIL".equalsIgnoreCase(value)) {
                                content.put("referral/history_of_allergies/exclusion_of_an_adverse_reaction/exclusion_statement", "No known allergies or adverse reactions");
                                content.put("referral/history_of_allergies/exclusion_of_an_adverse_reaction/date_last_updated", timeValue);
                            }
                            else {
                                content.put("referral/history_of_allergies/adverse_reaction_risk:" + allergyIndex + "/history_of_allergy", value);
                                content.put("referral/history_of_allergies/adverse_reaction_risk:" + allergyIndex + "/last_updated", timeValue);
                                allergyIndex++;
                            }
                            break;

                        case "history of family member diseases":
                            if ("NIL".equalsIgnoreCase(value)) {
                                content.put("referral/history_of_family_member_diseases/exclusion_of_family_history/exclusion_statement", "No significant family history");
                                content.put("referral/history_of_family_member_diseases/exclusion_of_family_history/date_last_updated", timeValue);
                            }
                            else {
                                content.put("referral/history_of_family_member_diseases/family_history:" + familyHistoryIndex + "/history_of_family_member_diseases", value);
                                content.put("referral/history_of_family_member_diseases/family_history:" + familyHistoryIndex + "/last_updated", timeValue);
                                familyHistoryIndex++;
                            }
                            break;

                        case "history of past illness":
                            if ("NIL".equalsIgnoreCase(value)) {
                                content.put("referral/history_of_past_illness/exclusion_of_a_problem_diagnosis/exclusion_statement", "No significant past history");
                                content.put("referral/history_of_past_illness/exclusion_of_a_problem_diagnosis/last_updated", timeValue);
                            }
                            else {
                                content.put("referral/history_of_past_illness/problem_diagnosis:" + pastIllnessIndex + "/history_of_past_illness", value);
                                content.put("referral/history_of_past_illness/problem_diagnosis:" + pastIllnessIndex + "/date_time_clinically_recognised", timeValue);
                                pastIllnessIndex++;
                            }
                            break;

                        case "comments":
                            content.put("referral/comments/clinical_synopsis/comments", value);
                            break;

                        case "interpreter required":
                            if ("No".equalsIgnoreCase(value)) {
                                content.put("referral/referral_details/service_request/request:0/interpreter_details/interpreter_required", "false");
                            }
                            else {
                                content.put("referral/referral_details/service_request/request:0/interpreter_details/interpreter_required", "true");
                            }
                            break;

                        case "physical mobility impairment":
                            content.put("referral/social_context/physical_mobility_impairment/physical_mobility_impairment", value);
                            break;

                        case "history of tobacco use":
                            content.put("referral/social_context/history_of_tobacco_use/smoking_details:0/history_of_tobacco_use", value);
                            break;

                        case "history of alcohol use":
                            content.put("referral/social_context/history_of_alcohol_use/history_of_alcohol_use", value);
                            break;

                        case "pulse":
                            content.put("referral/examination_findings/vital_signs/pulse_heart_beat/pulse_rate|magnitude", value);
                            content.put("referral/examination_findings/vital_signs/pulse_heart_beat/pulse_rate|unit", "/min");
                            break;

                        case "systolic blood pressure":
                            content.put("referral/examination_findings/vital_signs/blood_pressure/systolic|magnitude", value);
                            content.put("referral/examination_findings/vital_signs/blood_pressure/systolic|unit", "mm[Hg]");
                            break;

                        case "diastolic blood pressure":
                            content.put("referral/examination_findings/vital_signs/blood_pressure/diastolic|magnitude", value);
                            content.put("referral/examination_findings/vital_signs/blood_pressure/diastolic|unit", "mm[Hg]");
                            break;

                        case "body height":
                            if ("metres".equalsIgnoreCase(units)) {
                                value = String.valueOf(Double.valueOf(value) * 100);
                            }
                            content.put("referral/examination_findings/height_length/any_event:0/height_length|magnitude", value);
                            content.put("referral/examination_findings/height_length/any_event:0/height_length|unit", "cm");
                            break;

                        case "weight":
                            content.put("referral/examination_findings/body_weight/weight|magnitude", value);
                            content.put("referral/examination_findings/body_weight/weight|unit", "kg");
                            break;

                        case "body mass index":
                            content.put("referral/examination_findings/body_mass_index/any_event:0/body_mass_index|magnitude", value);
                            content.put("referral/examination_findings/body_mass_index/any_event:0/body_mass_index|unit", "kg/m2");
                            break;

                        case "physical exam.total":
                            content.put("referral/examination_findings/physical_examination_findings/description", value);
                            break;

                        case "anticoagulant use":
                            if ("Yes".equalsIgnoreCase(value)) {
                                content.put("referral/medication_and_medical_devices/anticoagulation_use/anticoagulation_use", "true");
                            }
                            else {
                                content.put("referral/medication_and_medical_devices/anticoagulation_use/anticoagulation_use", "false");
                            }
                            break;

                        case "current medication":
                            if ("NIL".equalsIgnoreCase(value)) {
                                content.put("referral/medication_and_medical_devices/exclusion_of_a_medication/exclusion_statement", "No current medication");
                                content.put("referral/medication_and_medical_devices/exclusion_of_a_medication/date_last_updated", timeValue);
                            }
                            else {
                                content.put("referral/medication_and_medical_devices/medication_order:" + medicationIndex + "/order/medication_item", value);
                                content.put("referral/medication_and_medical_devices/medication_order:" + medicationIndex + "/narrative", value);
                                content.put("referral/medication_and_medical_devices/medication_order:" + medicationIndex + "/order/timing", "Boilerplate timing");
                                content.put("referral/medication_and_medical_devices/medication_order:" + medicationIndex + "/order/course_details/order_start_date_time", timeValue);
                                medicationIndex++;
                            }
                            break;
                    }
                }
            }
        }
        catch (Exception e) {
            content = null;
            logger.error(e.getMessage());
        }

        return content;
    }

    private Map<String, Object> createDischargeFlatJsonContent(GenericDocument document) {

        final String DISCHARGE_DETAILS_UK_PREFIX = "discharge_summary/discharge_details/discharge_details_uk_v1/";
        final String DISCHARGE_IDENTIFIER_PREFIX = "discharge_summary/context/patient_identifiers/";

        String hl7DocumentXML = (String) document.getDocumentContent();

        Map<String, Object> content = new HashMap<>();

        try {
            DocumentBuilder xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document hl7Document = xmlDocumentBuilder.parse(new ByteArrayInputStream(hl7DocumentXML.getBytes(StandardCharsets.UTF_8)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            content.put("ctx/language", "en");
            content.put("ctx/territory", "GB");
            content.put("ctx/id_namespace", "iEHR");
            content.put("ctx/id_scheme", "iEHR");

            String facilityXPath = "//*:MSH/*:MSH.4";
            if (!(evaluateXPath(xPath, facilityXPath, hl7Document)).isEmpty()) {
                content.put("ctx/health_care_facility|name", evaluateXPath(xPath, facilityXPath + "/*:HD.1", hl7Document));
                content.put("ctx/health_care_facility|id", evaluateXPath(xPath, facilityXPath + "/*:HD.2", hl7Document));
            }

            String composerXPath = "//*:MSH/*:MSH.6";
            if (!(evaluateXPath(xPath, composerXPath, hl7Document)).isEmpty()) {
                content.put("ctx/composer_name", evaluateXPath(xPath, composerXPath + "/*:HD.1", hl7Document));
                content.put("ctx/composer_id", evaluateXPath(xPath, composerXPath + "/*:HD.2", hl7Document));
                content.put("discharge_summary/composer|id_scheme", evaluateXPath(xPath, composerXPath + "/*:HD.3", hl7Document));
            }

            String time = evaluateXPath(xPath, "//*:MSH.7/*:TS.1", hl7Document);
            if (!time.isEmpty()) {
                content.put("ctx/time", parseDateTime(time));
            }

            String pid3XPath = "//*:PID/*:PID.3";
            int numberOfPID3 = evaluateIntegerXPath(xPath, "count(" + pid3XPath + ")", hl7Document);
            for (int pid3 = 1; pid3 <= numberOfPID3; pid3++) {
                String pid3ItemXPath = pid3XPath + "[" + pid3 + "]";

                String cx1 = evaluateXPath(xPath, pid3ItemXPath + "/*:CX.1", hl7Document);
                String cx4hd1 = evaluateXPath(xPath, pid3ItemXPath + "/*:CX.4/*:HD.1", hl7Document);
                String cx5 = evaluateXPath(xPath, pid3ItemXPath + "/*:CX.5", hl7Document).toLowerCase();

                switch (cx5) {
                    case "mrn":
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "mrn", cx1);
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "mrn|issuer", "iEHR");
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "mrn|assigner", "iEHR");
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "mrn|type", "MRN");
                        if (cx4hd1 != null && !cx4hd1.isEmpty()) {
                            content.put(DISCHARGE_DETAILS_UK_PREFIX + "discharging_organisation/name_of_organisation", cx4hd1);
                        }
                        break;
                    case "gms":
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "gms", cx1);
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "gms|issuer", "iEHR");
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "gms|assigner", "iEHR");
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "gms|type", "GMS");
                        break;
                    case "oth":
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "oth", cx1);
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "oth|issuer", "iEHR");
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "oth|assigner", "iEHR");
                        content.put(DISCHARGE_IDENTIFIER_PREFIX + "oth|type", "OTH");
                        break;
                }
            }

            int diagnosisIndex = 0;
            int numberOfDG1Segments = evaluateIntegerXPath(xPath, "count(//*:DG1)", hl7Document);
            for (int dg1Index = 1; dg1Index <= numberOfDG1Segments; dg1Index++) {

                content.put("discharge_summary/diagnoses/problem_diagnosis:" + diagnosisIndex + "/problem_diagnosis_name",
                            evaluateXPath(xPath, "//*:DG1[" + dg1Index + "]/*:DG1.3/*:CE.1", hl7Document));

                String status = evaluateXPath(xPath, "//*:DG1[" + dg1Index + "]/*:DG1.6", hl7Document);
                String statusCode = "";

                if (status.equalsIgnoreCase("admitting")) {
                    statusCode = "at0016";
                }
                else if (statusCode.equalsIgnoreCase("working")) {
                    statusCode = "at0017";
                }
                else if (statusCode.equalsIgnoreCase("final")) {
                    statusCode = "at0018";
                }

                content.put("discharge_summary/diagnoses/problem_diagnosis:" + diagnosisIndex + "/problem_diagnosis_status/diagnostic_status|code", statusCode);
                diagnosisIndex++;
            }

            String responsibleProfXPath = "//*:REF_I12.PATIENT_VISIT/*:PV1/*:PV1.7";
            String responsibleProfId = evaluateXPath(xPath, responsibleProfXPath + "/*:XCN.1", hl7Document);

            if (!responsibleProfId.isEmpty()) {
                content.put(DISCHARGE_DETAILS_UK_PREFIX + "responsible_professional/professional_identifier", responsibleProfId);
                content.put(DISCHARGE_DETAILS_UK_PREFIX + "responsible_professional/professional_identifier|issuer", "iEHR");
                content.put(DISCHARGE_DETAILS_UK_PREFIX + "responsible_professional/professional_identifier|assigner", "iEHR");
                content.put(DISCHARGE_DETAILS_UK_PREFIX + "responsible_professional/professional_identifier|type", "MCN");

                String responsibleProfName = evaluateXPath(xPath, responsibleProfXPath + "/*:XCN.2/*:FN.1", hl7Document);
                content.put(DISCHARGE_DETAILS_UK_PREFIX + "responsible_professional/professional_name/name", responsibleProfName);
            }

            String clinicalSynopsis = evaluateXPath(xPath, "//*:NTE/*:NTE.3", hl7Document);
            if (!clinicalSynopsis.isEmpty()) {
                content.put("discharge_summary/clinical_summary/clinical_synopsis/synopsis", clinicalSynopsis);
            }
        }
        catch (Exception e) {
            content = null;
            logger.error(e.getMessage());
        }

        return content;
    }

    private Integer evaluateIntegerXPath(XPath xPath, String expression, Document hl7Document)
        throws XPathExpressionException {
        return ((Double) xPath.compile(expression).evaluate(hl7Document, XPathConstants.NUMBER)).intValue();
    }

    private String evaluateXPath(XPath xPath, String expression, Document hl7Document) throws XPathExpressionException {
        return xPath.compile(expression).evaluate(hl7Document);
    }

    private String parseDateTime(String time) throws ParseException {
        if (time != null && !time.isEmpty()) {
            if (time.length() == 8) {
                time = DateFormatter.toSimpleDateString(DATE_FORMAT.parse(time));
            }
            else if (time.length() == 12) {
                time = DateFormatter.toSimpleDateString(DATE_TIME_FORMAT_SHORT.parse(time));
            }
            else if (time.length() == 14) {
                time = DateFormatter.toSimpleDateString(DATE_TIME_FORMAT.parse(time));
            }
        }

        return time;
    }
}
