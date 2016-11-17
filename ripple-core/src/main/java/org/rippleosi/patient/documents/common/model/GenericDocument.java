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
package org.rippleosi.patient.documents.common.model;

import java.io.Serializable;

/**
 */
public class GenericDocument implements Serializable {

    private String documentType;        // Possibly be an enum for supported document types
    private Object documentContent;     // Payload of document, XML, binary etc.
    private String originalSource;      // Sending system which the document originated from

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Object getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(Object documentContent) {
        this.documentContent = documentContent;
    }

    public String getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(String originalSource) {
        this.originalSource = originalSource;
    }
}
