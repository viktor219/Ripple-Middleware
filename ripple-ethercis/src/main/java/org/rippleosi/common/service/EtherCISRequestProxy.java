/*
 *  Copyright 2015 Ripple OSI
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
 *
 */

package org.rippleosi.common.service;

import org.springframework.http.ResponseEntity;

/**
 */
public interface EtherCISRequestProxy {

    <T> ResponseEntity<T> getWithSession(String uri, Class<T> cls, String sessionId);

    <T> ResponseEntity<T> postWithSession(String uri, Class<T> cls, String sessionId, Object body);

    <T> ResponseEntity<T> putWithSession(String uri, Class<T> cls, String sessionId, Object body);

    <T> ResponseEntity<T> createSession(String uri, Class<T> cls);

    <T> ResponseEntity<T> killSession(String uri, Class<T> cls, String sessionId);
}
