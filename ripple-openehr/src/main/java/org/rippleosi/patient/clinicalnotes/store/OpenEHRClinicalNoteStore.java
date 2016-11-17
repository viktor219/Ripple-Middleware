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
package org.rippleosi.patient.clinicalnotes.store;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Consume;
import org.rippleosi.common.service.AbstractOpenEhrService;
import org.rippleosi.common.service.strategies.store.CreateStrategy;
import org.rippleosi.common.service.strategies.store.DefaultStoreStrategy;
import org.rippleosi.common.service.strategies.store.UpdateStrategy;
import org.rippleosi.patient.clinicalnotes.model.ClinicalNoteDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenEHRClinicalNoteStore extends AbstractOpenEhrService implements ClinicalNoteStore {

    @Value("${c4hOpenEHR.clinicalNotesTemplate}")
    private String clinicalNotesTemplate;

    private static final String CLINICAL_NOTE_PREFIX = "clinical_notes/clinical_synopsis:0/";

    @Override
    @Consume(uri = "activemq:Consumer.C4HOpenEHR.VirtualTopic.Ripple.ClinicalNotes.Create")
    public void create(String patientId, ClinicalNoteDetails clinicalNote) {
        final Map<String, Object> content = createFlatJsonContent(clinicalNote);

        final CreateStrategy createStrategy = new DefaultStoreStrategy(patientId, clinicalNotesTemplate, content);

        createData(createStrategy);
    }

    @Override
    @Consume(uri = "activemq:Consumer.C4HOpenEHR.VirtualTopic.Ripple.ClinicalNotes.Update")
    public void update(String patientId, ClinicalNoteDetails clinicalNote) {
        final Map<String, Object> content = createFlatJsonContent(clinicalNote);

        final UpdateStrategy updateStrategy = new DefaultStoreStrategy(clinicalNote.getSourceId(), patientId,
                                                                       clinicalNotesTemplate, content);

        updateData(updateStrategy);
    }

    private Map<String, Object> createFlatJsonContent(ClinicalNoteDetails clinicalNote) {
        final Map<String, Object> content = new HashMap<>();

        content.put("ctx/language", "en");
        content.put("ctx/territory", "GB");
        content.put("ctx/composer_name", clinicalNote.getAuthor());

        content.put(CLINICAL_NOTE_PREFIX + "_name|value", clinicalNote.getType());
        content.put(CLINICAL_NOTE_PREFIX + "notes", clinicalNote.getNote());

        return content;
    }
}
