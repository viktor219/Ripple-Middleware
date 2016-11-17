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
package org.rippleosi.patient.clinicalnotes.search;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Transformer;
import org.rippleosi.common.util.DateFormatter;
import org.rippleosi.patient.clinicalnotes.model.ClinicalNoteSummary;

public class ClinicalNoteSummaryTransformer implements Transformer<Map<String, Object>, ClinicalNoteSummary> {

    @Override
    public ClinicalNoteSummary transform(Map<String, Object> input) {

        final String uid = MapUtils.getString(input, "uid");
        final String type = MapUtils.getString(input, "type");
        final String author = MapUtils.getString(input, "author");
        final String dateCreated = MapUtils.getString(input, "date_created");

        final ClinicalNoteSummary clinicalNote = new ClinicalNoteSummary();

        clinicalNote.setSource("openehr");
        clinicalNote.setSourceId(uid);
        clinicalNote.setType(type);
        clinicalNote.setAuthor(author);
        clinicalNote.setDateCreated(DateFormatter.toDate(dateCreated));

        return clinicalNote;
    }
}
