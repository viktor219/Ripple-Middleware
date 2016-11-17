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
package org.rippleosi.patient.documents.referral.model;

import java.util.List;
import java.util.Date;

import org.rippleosi.patient.documents.common.model.AbstractDocumentSummary;

/**
 */
public class ReferralDocumentDetails extends AbstractDocumentSummary {

    private String documentOriginalSource;

    private String composerName;
    private String facility;
    private String providerId;

    private Date referralDateTime;
    private String referralType;
    private List<String> reasonForReferral;         // Repeating element
    private String referralComments;
    private String priorityOfReferral;
    private String referralReferenceNumber;
    private String referredFrom;
    private String referredTo;

    private String providerContact_organisationName;
    private String providerContact_id;
    private String providerContact_workNumber;
    private String providerContact_emergencyNumber;
    private String providerContact_email;

    private String referralStatus_code;
    private String referralStatus_value;
    private String referralStatus_mapped; // Mapped from code to origional message value

    private String clinicalNarrative;
    private String presentIllness;
    private String clinicalSynopsisComments;
    private String previousHospitalAttendance;

    List<NameDateElement> pastIllensses;            // Repeating element

    List<NameDateElement> surgicalProcedures;       // Repeating element

    List<MedicationDetails> medications;              // Repeating element

    private String medication_anticoagulation_use;

    List<AllergyDetails> allergies;                // Repeating element

    private String tobaccoUse;
    private String alcholUse;
    private String physicalImparement;

    private String systolicBP;
    private String systolicBP_units;
    private String diastolicBP;
    private String diastolicBP_units;
    private String pulse;
    private String pulse_units;
    private String height;
    private String height_units;
    private String weight;
    private String weight_units;
    private String bodyMass;
    private String bodyMass_units;

    private String otherExaminationFindings;

    public String getDocumentOriginalSource() {
        return documentOriginalSource;
    }

    public void setDocumentOriginalSource(String documentOriginalSource) {
        this.documentOriginalSource = documentOriginalSource;
    }

    public String getComposerName() {
        return composerName;
    }

    public void setComposerName(String composerName) {
        this.composerName = composerName;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Date getReferralDateTime() {
        return referralDateTime;
    }

    public void setReferralDateTime(Date referralDateTime) {
        this.referralDateTime = referralDateTime;
    }

    public String getReferralType() {
        return referralType;
    }

    public void setReferralType(String referralType) {
        this.referralType = referralType;
    }

    public List<String> getReasonForReferral() {
        return reasonForReferral;
    }

    public void setReasonForReferral(List<String> reasonForReferral) {
        this.reasonForReferral = reasonForReferral;
    }

    public String getReferralComments() {
        return referralComments;
    }

    public void setReferralComments(String referralComments) {
        this.referralComments = referralComments;
    }

    public String getPriorityOfReferral() {
        return priorityOfReferral;
    }

    public void setPriorityOfReferral(String priorityOfReferral) {
        this.priorityOfReferral = priorityOfReferral;
    }

    public String getReferralReferenceNumber() {
        return referralReferenceNumber;
    }

    public void setReferralReferenceNumber(String referralReferenceNumber) {
        this.referralReferenceNumber = referralReferenceNumber;
    }

    public String getReferredFrom() {
        return referredFrom;
    }

    public void setReferredFrom(String referredFrom) {
        this.referredFrom = referredFrom;
    }

    public String getReferredTo() {
        return referredTo;
    }

    public void setReferredTo(String referredTo) {
        this.referredTo = referredTo;
    }

    public String getProviderContact_organisationName() {
        return providerContact_organisationName;
    }

    public void setProviderContact_organisationName(String providerContact_organisationName) {
        this.providerContact_organisationName = providerContact_organisationName;
    }

    public String getProviderContact_id() {
        return providerContact_id;
    }

    public void setProviderContact_id(String providerContact_id) {
        this.providerContact_id = providerContact_id;
    }

    public String getProviderContact_workNumber() {
        return providerContact_workNumber;
    }

    public void setProviderContact_workNumber(String providerContact_workNumber) {
        this.providerContact_workNumber = providerContact_workNumber;
    }

    public String getProviderContact_emergencyNumber() {
        return providerContact_emergencyNumber;
    }

    public void setProviderContact_emergencyNumber(String providerContact_emergencyNumber) {
        this.providerContact_emergencyNumber = providerContact_emergencyNumber;
    }

    public String getProviderContact_email() {
        return providerContact_email;
    }

    public void setProviderContact_email(String providerContact_email) {
        this.providerContact_email = providerContact_email;
    }

    public String getReferralStatus_code() {
        return referralStatus_code;
    }

    public void setReferralStatus_code(String referralStatus_code) {
        this.referralStatus_code = referralStatus_code;
    }

    public String getReferralStatus_value() {
        return referralStatus_value;
    }

    public void setReferralStatus_value(String referralStatus_value) {
        this.referralStatus_value = referralStatus_value;
    }

    public String getReferralStatus_mapped() {
        return referralStatus_mapped;
    }

    public void setReferralStatus_mapped(String referralStatus_mapped) {
        this.referralStatus_mapped = referralStatus_mapped;
    }

    public String getClinicalNarrative() {
        return clinicalNarrative;
    }

    public void setClinicalNarrative(String clinicalNarrative) {
        this.clinicalNarrative = clinicalNarrative;
    }

    public String getPresentIllness() {
        return presentIllness;
    }

    public void setPresentIllness(String presentIllness) {
        this.presentIllness = presentIllness;
    }

    public String getClinicalSynopsisComments() {
        return clinicalSynopsisComments;
    }

    public void setClinicalSynopsisComments(String clinicalSynopsisComments) {
        this.clinicalSynopsisComments = clinicalSynopsisComments;
    }

    public String getPreviousHospitalAttendance() {
        return previousHospitalAttendance;
    }

    public void setPreviousHospitalAttendance(String previousHospitalAttendance) {
        this.previousHospitalAttendance = previousHospitalAttendance;
    }

    public List<NameDateElement> getPastIllensses() {
        return pastIllensses;
    }

    public void setPastIllensses(List<NameDateElement> pastIllensses) {
        this.pastIllensses = pastIllensses;
    }

    public List<NameDateElement> getSurgicalProcedures() {
        return surgicalProcedures;
    }

    public void setSurgicalProcedures(List<NameDateElement> surgicalProcedures) {
        this.surgicalProcedures = surgicalProcedures;
    }

    public List<MedicationDetails> getMedications() {
        return medications;
    }

    public void setMedications(List<MedicationDetails> medications) {
        this.medications = medications;
    }

    public String getMedication_anticoagulation_use() {
        return medication_anticoagulation_use;
    }

    public void setMedication_anticoagulation_use(String medication_anticoagulation_use) {
        this.medication_anticoagulation_use = medication_anticoagulation_use;
    }

    public List<AllergyDetails> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergyDetails> allergies) {
        this.allergies = allergies;
    }

    public String getTobaccoUse() {
        return tobaccoUse;
    }

    public void setTobaccoUse(String tobaccoUse) {
        this.tobaccoUse = tobaccoUse;
    }

    public String getAlcholUse() {
        return alcholUse;
    }

    public void setAlcholUse(String alcholUse) {
        this.alcholUse = alcholUse;
    }

    public String getPhysicalImparement() {
        return physicalImparement;
    }

    public void setPhysicalImparement(String physicalImparement) {
        this.physicalImparement = physicalImparement;
    }

    public String getSystolicBP() {
        return systolicBP;
    }

    public void setSystolicBP(String systolicBP) {
        this.systolicBP = systolicBP;
    }

    public String getSystolicBP_units() {
        return systolicBP_units;
    }

    public void setSystolicBP_units(String systolicBP_units) {
        this.systolicBP_units = systolicBP_units;
    }

    public String getDiastolicBP() {
        return diastolicBP;
    }

    public void setDiastolicBP(String diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public String getDiastolicBP_units() {
        return diastolicBP_units;
    }

    public void setDiastolicBP_units(String diastolicBP_units) {
        this.diastolicBP_units = diastolicBP_units;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getPulse_units() {
        return pulse_units;
    }

    public void setPulse_units(String pulse_units) {
        this.pulse_units = pulse_units;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeight_units() {
        return height_units;
    }

    public void setHeight_units(String height_units) {
        this.height_units = height_units;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight_units() {
        return weight_units;
    }

    public void setWeight_units(String weight_units) {
        this.weight_units = weight_units;
    }

    public String getBodyMass() {
        return bodyMass;
    }

    public void setBodyMass(String bodyMass) {
        this.bodyMass = bodyMass;
    }

    public String getBodyMass_units() {
        return bodyMass_units;
    }

    public void setBodyMass_units(String bodyMass_units) {
        this.bodyMass_units = bodyMass_units;
    }

    public String getOtherExaminationFindings() {
        return otherExaminationFindings;
    }

    public void setOtherExaminationFindings(String otherExaminationFindings) {
        this.otherExaminationFindings = otherExaminationFindings;
    }
}
