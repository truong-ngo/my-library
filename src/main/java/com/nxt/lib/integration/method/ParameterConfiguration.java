package com.nxt.lib.integration.method;

import lombok.Data;

/**
 * Configuration for parameter
 * */
@Data
public class ParameterConfiguration {

    /**
     * Parameter index, start from zero
     * */
    private Integer index;

    /**
     * Parameter java type. Eg: java.util.Date etc...
     * */
    private String parametersType;

    /**
     * Expression that use to retrieve parameter value
     * */
    private String paramsConfig;
}
