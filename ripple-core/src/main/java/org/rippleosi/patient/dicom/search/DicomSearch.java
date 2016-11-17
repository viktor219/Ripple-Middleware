/*
 *   Copyright 2016 Ripple OSI
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
package org.rippleosi.patient.dicom.search;

import java.util.List;

import org.rippleosi.common.repo.Repository;
import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.patient.dicom.model.DicomInstanceId;
import org.rippleosi.patient.dicom.model.DicomInstanceSummary;
import org.rippleosi.patient.dicom.model.DicomSeriesDetails;
import org.rippleosi.patient.dicom.model.DicomSeriesSummary;
import org.rippleosi.patient.dicom.model.DicomStudySummary;

public interface DicomSearch extends Repository {

    List<DicomStudySummary> findAllDicomStudies(String patientId, RepoSourceType source);

    DicomSeriesSummary findAllDicomSeriesInStudy(String patientId, String studyId, RepoSourceType source);

    DicomSeriesDetails findSeriesDetails(String patientId, String seriesId, RepoSourceType source);

    DicomInstanceSummary findInstanceSummary(String patientId, String instanceId, RepoSourceType source);

    DicomInstanceId findFirstInstanceIdInSeries(String patientId, String seriesId, RepoSourceType source);
}
