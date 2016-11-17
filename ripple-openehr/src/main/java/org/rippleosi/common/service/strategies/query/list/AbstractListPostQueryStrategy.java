package org.rippleosi.common.service.strategies.query.list;

import java.util.List;

import org.rippleosi.common.service.strategies.query.AbstractPostQueryStrategy;
import org.rippleosi.common.service.strategies.query.details.AbstractDetailsGetQueryStrategy;
import org.rippleosi.common.service.strategies.query.details.AbstractDetailsPostQueryStrategy;

/**
 *  AbstractListPostQueryStrategy can be directly subclassed in order to retrieve a list of OpenEHR objects
 *  relating to a particular data type.
 *  <p>
 *
 *  The query strategy is facilitated by an HTTP POST, and is used when your OpenEHR query is very large. By using a
 *  POST, the query takes the form of a Map and is sent in the body of the request, rather than as a String that is
 *  appended to the request URI.
 *  <p>
 *
 *  If you are wanting to use an HTTP GET then instead subclass AbstractListGetQueryStrategy
 *
 *  @see AbstractListGetQueryStrategy
 *  @see AbstractDetailsGetQueryStrategy
 *  @see AbstractDetailsPostQueryStrategy
 */
public abstract class AbstractListPostQueryStrategy<T> extends AbstractPostQueryStrategy<List<T>> {

    protected AbstractListPostQueryStrategy(String patientId) {
        super(patientId);
    }
}
