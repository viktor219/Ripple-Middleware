package org.rippleosi.common.service.strategies.query;

import java.util.Map;

public abstract class AbstractPostQueryStrategy<T> implements QueryStrategy<T> {

    private final String patientId;

    protected AbstractPostQueryStrategy(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    public abstract Map<String, Object> getQueryParameters(String namespace, String patientId);
}
