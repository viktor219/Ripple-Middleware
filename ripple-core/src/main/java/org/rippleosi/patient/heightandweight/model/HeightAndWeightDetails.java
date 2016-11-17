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
package org.rippleosi.patient.heightandweight.model;

import java.io.Serializable;
import java.util.Date;

public class HeightAndWeightDetails implements Serializable {

    private String sourceId;
    private String source;
    private Double height;
    private Date heightRecorded;
    private Double weight;
    private Date weightRecorded;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Date getHeightRecorded() {
        return heightRecorded;
    }

    public void setHeightRecorded(Date heightRecorded) {
        this.heightRecorded = heightRecorded;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Date getWeightRecorded() {
        return weightRecorded;
    }

    public void setWeightRecorded(Date weightRecorded) {
        this.weightRecorded = weightRecorded;
    }
}
