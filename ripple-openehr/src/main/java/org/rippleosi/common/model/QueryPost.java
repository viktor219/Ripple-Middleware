package org.rippleosi.common.model;

import java.util.Map;

public class QueryPost {

    private String aql;
    private Map aqlParameters;

    public String getAql() {
        return aql;
    }

    public void setAql(String aql) {
        this.aql = aql;
    }

    public Map getAqlParameters() {
        return aqlParameters;
    }

    public void setAqlParameters(Map aqlParameters) {
        this.aqlParameters = aqlParameters;
    }
}
