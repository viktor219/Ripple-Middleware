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
package org.rippleosi.common.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.util.DefaultUriTemplateHandler;

public class DefaultEtherCISURITemplateHandler extends DefaultUriTemplateHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEtherCISService.class);

    @Override
    public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
        return encodeUriTemplate(uriTemplate);
    }

    @Override
    public URI expand(String uriTemplate, Object... uriVariableValues) {
        return encodeUriTemplate(uriTemplate);
    }

    private URI encodeUriTemplate(String uriTemplate) {
        try {
            return new URI(uriTemplate);
        }
        catch (URISyntaxException e) {
            LOGGER.debug("Malformed URI", e);
            return null;
        }
    }
}
