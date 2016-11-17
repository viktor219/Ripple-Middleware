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

import org.apache.camel.Produce;
import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.RepoSourceTypes;
import org.rippleosi.patient.documents.common.model.GenericDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class ActiveMQProxyDocumentStore implements DocumentStore {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQProxyDocumentStore.class);

    @Value("${repository.config.jms:500}")
    private int priority;

    @Produce(uri = "activemq:topic:VirtualTopic.Ripple.Documents.Create")
    private DocumentStore createTopic;

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public RepoSourceType getSource() {
        return RepoSourceTypes.ACTIVEMQ;
    }

    @Override
    public void create(String patientId, GenericDocument document) {
        try {
            createTopic.create(patientId, document);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
