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
package org.rippleosi.common.service.strategies.query.list;

import java.util.List;

import org.rippleosi.common.service.strategies.query.AbstractGetQueryStrategy;
import org.rippleosi.common.service.strategies.query.details.AbstractDetailsGetQueryStrategy;
import org.rippleosi.common.service.strategies.query.details.AbstractDetailsPostQueryStrategy;

/**
 *  AbstractListGetQueryStrategy can be directly subclassed in order to retrieve a list of OpenEHR objects
 *  relating to a particular data type.
 *  <p>
 *
 *  The query strategy is facilitated by an HTTP GET, which means that the query being used is a String and is
 *  appended to the request URI as a query parameter.
 *  <p>
 *
 *  If you are wanting to post the query in the body of the HTTP request then instead subclass
 *  AbstractListPostQueryStrategy
 *
 *  @see AbstractListPostQueryStrategy
 *  @see AbstractDetailsGetQueryStrategy
 *  @see AbstractDetailsPostQueryStrategy
 */
public abstract class AbstractListGetQueryStrategy<T> extends AbstractGetQueryStrategy<List<T>> {

    protected AbstractListGetQueryStrategy(String patientId) {
        super(patientId);
    }
}
