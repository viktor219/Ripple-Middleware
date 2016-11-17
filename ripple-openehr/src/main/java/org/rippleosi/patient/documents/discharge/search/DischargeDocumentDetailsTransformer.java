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
import java.util.ArrayList;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Transformer;
import org.rippleosi.common.util.DateFormatter;
import org.rippleosi.patient.documents.discharge.model.DischargeDocumentDetails;
import org.rippleosi.patient.documents.discharge.model.ProblemDetails;

/**
 */
public class DischargeDocumentDetailsTransformer implements Transformer<Map<String, Object>, DischargeDocumentDetails> {

    @Override
    public DischargeDocumentDetails transform(Map<String, Object> input) {

        DischargeDocumentDetails dischargeDocument = new DischargeDocumentDetails();

        dischargeDocument.setSource("Marand");
        dischargeDocument.setSourceId(MapUtils.getString(input, "uid"));

        dischargeDocument.setDocumentType("Healthlink " + MapUtils.getString(input, "documentType"));
        dischargeDocument.setDocumentDate(MapUtils.getString(input, "dischargeDate"));

        dischargeDocument.setAuthor_name(MapUtils.getString(input, "authorName"));
        dischargeDocument.setAuthor_id(MapUtils.getString(input, "authorId"));
        dischargeDocument.setAuthor_idScheme(MapUtils.getString(input, "authorIdScheme"));

        dischargeDocument.setFacility(MapUtils.getString(input, "facility"));
        dischargeDocument.setDateOfAdmission(MapUtils.getString(input, "dateOfAdmission"));

        dischargeDocument.setResponsibleProfessional_name(MapUtils.getString(input, "professionalName"));
        dischargeDocument.setResponsibleProfessional_id(MapUtils.getString(input, "professionalId"));
        dischargeDocument.setResponsibleProfessional_id(MapUtils.getString(input, "professionalId"));
        dischargeDocument.setResponsibleProfessional_idType(MapUtils.getString(input, "professionalType"));

        dischargeDocument.setDischargingOrganisation(MapUtils.getString(input, "dischargeOrganisation"));
        dischargeDocument.setDateTimeOfDischarge(MapUtils.getString(input, "dischargeDateTime"));
        dischargeDocument.setClinicalSynopsis(MapUtils.getString(input, "synopsis"));

        return dischargeDocument;
    }

    public DischargeDocumentDetails transformIdentifiers(List<Map<String, Object>> resultSet, DischargeDocumentDetails currentDocumentDetails) {

        boolean mrnSet = false;
        boolean othSet = false;
        boolean gmsSet = false;

        for(Map<String, Object> result : resultSet){

            String id = MapUtils.getString(result, "patientIdMrn");
            String type = MapUtils.getString(result, "patientIdMrnType");

            if(!mrnSet && type.equalsIgnoreCase("MRN")){
                currentDocumentDetails.setPatientIdentifier_mrn(id);
                currentDocumentDetails.setPatientIdentifier_mrnType(type);
                mrnSet = true;
            }
            else if(!othSet && type.equalsIgnoreCase("OTH")){
                currentDocumentDetails.setPatientIdentifier_oth(id);
                currentDocumentDetails.setPatientIdentifier_othType(type);
                othSet = true;
            }
            else if(!gmsSet && type.equalsIgnoreCase("GMS")){
                currentDocumentDetails.setPatientIdentifier_gms(id);
                currentDocumentDetails.setPatientIdentifier_gmsType(type);
                gmsSet = true;
            }
        }
        
        return currentDocumentDetails;
    }
    
    public DischargeDocumentDetails transformDiagnosis(List<Map<String, Object>> resultSet, DischargeDocumentDetails currentDocumentDetails) {

        List<ProblemDetails> diagnosisList = new ArrayList<>();
        
        for(Map<String, Object> result : resultSet){
            
            String name = MapUtils.getString(result, "diagnosisName");
            String time = MapUtils.getString(result, "diagnosisTime");

            ProblemDetails problemDetails = new ProblemDetails();
            problemDetails.setProblem(name);
            problemDetails.setDateOfOnset(DateFormatter.toDate(time));

            diagnosisList.add(problemDetails);
        }
        
        currentDocumentDetails.setDiagnosisList(diagnosisList);
        return currentDocumentDetails;
    }
}
