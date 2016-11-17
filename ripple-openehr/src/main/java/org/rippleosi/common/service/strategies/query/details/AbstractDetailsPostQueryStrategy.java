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
package org.rippleosi.common.service.strategies.query.details;

import org.rippleosi.common.service.strategies.query.AbstractPostQueryStrategy;
import org.rippleosi.common.service.strategies.query.list.AbstractListGetQueryStrategy;
import org.rippleosi.common.service.strategies.query.list.AbstractListPostQueryStrategy;

/**
 *  AbstractDetailsPostQueryStrategy can be directly subclassed in order to find the details of a single OpenEHR object.
 *  <p>
 *
 *  The query strategy is facilitated by an HTTP POST, and is used when your OpenEHR query is very large. By using a
 *  POST, the query takes the form of a Map and is sent in the body of the request, rather than as a String that is
 *  appended to the request URI.
 *  <p>
 *
 *  If you are wanting to use an HTTP GET then instead subclass AbstractDetailsGetQueryStrategy
 *
 *  @see AbstractDetailsGetQueryStrategy
 *  @see AbstractListGetQueryStrategy
 *  @see AbstractListPostQueryStrategy
 */
public abstract class AbstractDetailsPostQueryStrategy<T> extends AbstractPostQueryStrategy<T> {

    protected AbstractDetailsPostQueryStrategy(String patientId) {
        super(patientId);
    }
}
