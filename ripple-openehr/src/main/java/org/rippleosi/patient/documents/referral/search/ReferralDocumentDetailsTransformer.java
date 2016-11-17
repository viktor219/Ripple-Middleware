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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Transformer;
import org.rippleosi.common.util.DateFormatter;
import org.rippleosi.patient.allergies.model.AllergyDetails;
import org.rippleosi.patient.documents.referral.model.NameDateElement;
import org.rippleosi.patient.documents.referral.model.ReferralDocumentDetails;
import org.rippleosi.patient.medication.model.MedicationDetails;

/**
 */
public class ReferralDocumentDetailsTransformer implements Transformer<Map<String, Object>, ReferralDocumentDetails> {

    @Override
    public ReferralDocumentDetails transform(Map<String, Object> input) {
        return new ReferralDocumentDetails();
    }

    public ReferralDocumentDetails transformWithRepeatingGroups(List<Map<String, Object>> resultSet) {

        ReferralDocumentDetails referralDocument = new ReferralDocumentDetails();

        // From first row/map set all the none repeating groups as they will be the same on all rows.
        Map<String, Object> input = resultSet.get(0);

        referralDocument.setSource("Marand");
        referralDocument.setSourceId(MapUtils.getString(input, "uid"));

        referralDocument.setDocumentType("Healthlink " + MapUtils.getString(input, "documentType"));
        referralDocument.setDocumentDate(MapUtils.getString(input, "referralDateTime"));

        referralDocument.setReferralDateTime(DateFormatter.toDate(MapUtils.getString(input, "referralDateTime")));
        referralDocument.setComposerName(MapUtils.getString(input, "authorName"));
        referralDocument.setFacility(MapUtils.getString(input, "facility"));
        referralDocument.setReferralType(MapUtils.getString(input, "referralType"));
        referralDocument.setReferralComments(MapUtils.getString(input, "referralComment"));
        referralDocument.setPriorityOfReferral(MapUtils.getString(input, "priorityOfReferral"));
        referralDocument.setReferralReferenceNumber(MapUtils.getString(input, "referralReferenceNumber"));
        referralDocument.setReferredFrom(MapUtils.getString(input, "referredFrom"));
        referralDocument.setReferredTo(MapUtils.getString(input, "referredTo"));

        referralDocument.setProviderContact_organisationName(MapUtils.getString(input, "providerContactOrgName"));
        referralDocument.setProviderContact_id(MapUtils.getString(input, "providerContactId"));
        referralDocument.setProviderContact_workNumber(MapUtils.getString(input, "providerContactWorkNumber"));
        referralDocument.setProviderContact_emergencyNumber(MapUtils.getString(input, "providerContactEmgNumber"));
        referralDocument.setProviderContact_email(MapUtils.getString(input, "providerContactEmail"));

        referralDocument.setReferralStatus_code(MapUtils.getString(input, "referralStatusCode"));
        referralDocument.setReferralStatus_value(MapUtils.getString(input, "referralStatusValue"));
        switch (MapUtils.getString(input, "referralStatusCode")) {
            case "526":
                referralDocument.setReferralStatus_mapped("Pending");
                break;
            case "529":
                referralDocument.setReferralStatus_mapped("Accepted");
                break;
            case "528":
                referralDocument.setReferralStatus_mapped("Rejected");
                break;
            case "531":
                referralDocument.setReferralStatus_mapped("Expired");
                break;
        }

        referralDocument.setClinicalNarrative(MapUtils.getString(input, "clinicalNarrative"));
        referralDocument.setPresentIllness(MapUtils.getString(input, "presentIllness"));
        referralDocument.setClinicalSynopsisComments(MapUtils.getString(input, "clinicalSynopsis"));
        referralDocument.setPreviousHospitalAttendance(MapUtils.getString(input, "previousHospitalAttendance"));
        referralDocument.setMedication_anticoagulation_use(MapUtils.getString(input, "anticoagulationUse"));
        referralDocument.setTobaccoUse(MapUtils.getString(input, "tobaccoUse"));
        referralDocument.setAlcholUse(MapUtils.getString(input, "alcoholUse"));
        referralDocument.setPhysicalImparement(MapUtils.getString(input, "physicalMobility"));
        referralDocument.setSystolicBP(MapUtils.getString(input, "systolicBP"));
        referralDocument.setSystolicBP_units(MapUtils.getString(input, "systolicBPUnits"));
        referralDocument.setDiastolicBP(MapUtils.getString(input, "diastolicBP"));
        referralDocument.setDiastolicBP_units(MapUtils.getString(input, "diastolicBPUnits"));
        referralDocument.setPulse(MapUtils.getString(input, "pulseRate"));
        referralDocument.setPulse_units(MapUtils.getString(input, "pulseRateUnits"));
        referralDocument.setHeight(MapUtils.getString(input, "height"));
        referralDocument.setHeight_units(MapUtils.getString(input, "heightUnits"));
        referralDocument.setWeight(MapUtils.getString(input, "weight"));
        referralDocument.setWeight_units(MapUtils.getString(input, "weightUnits"));
        referralDocument.setBodyMass(MapUtils.getString(input, "bodyMassIndex"));
        referralDocument.setBodyMass_units(MapUtils.getString(input, "bodyMassIndexUnits"));
        referralDocument.setOtherExaminationFindings(MapUtils.getString(input, "OtherFindings"));

        HashSet referralReasons = new HashSet();
        HashSet pastIllnesses = new HashSet();
        HashSet surgicalProcedures = new HashSet();
        HashSet medications = new HashSet();
        HashSet allergies = new HashSet();

        for (Map<String, Object> row : resultSet) {

            referralReasons.add(MapUtils.getString(input, "reasonForReferral"));

            NameDateElement pastIllnesse = new NameDateElement();
            pastIllnesse.setDate(MapUtils.getString(input, "pastIllnessDateTime"));
            pastIllnesse.setValue(MapUtils.getString(input, "pastIllness"));
            pastIllnesses.add(pastIllnesse);

            NameDateElement surgicalProcedure = new NameDateElement();
            surgicalProcedure.setDate(MapUtils.getString(input, "pastIllnessDateTime"));
            surgicalProcedure.setValue(MapUtils.getString(input, "pastIllness"));
            surgicalProcedures.add(surgicalProcedure);

            MedicationDetails medication = new MedicationDetails();
            medication.setName(MapUtils.getString(input, "medication"));
            medication.setStartDate(DateFormatter.toDate(MapUtils.getString(input, "medicationDateTime")));
            medication.setStartTime(DateFormatter.toDate(MapUtils.getString(input, "medicationDateTime")));
            medications.add(medication);

            AllergyDetails allergy = new AllergyDetails();
            allergy.setCause(MapUtils.getString(input, "allergy"));
            allergy.setDateCreated(DateFormatter.toDate(MapUtils.getString(input, "allergyDateTime")));
            allergies.add(allergy);
        }

        referralDocument.setReasonForReferral(new ArrayList(referralReasons));
        referralDocument.setPastIllensses(new ArrayList(pastIllnesses));
        referralDocument.setSurgicalProcedures(new ArrayList(surgicalProcedures));
        referralDocument.setMedications(new ArrayList(medications));
        referralDocument.setAllergies(new ArrayList(allergies));

        return referralDocument;
    }
}
