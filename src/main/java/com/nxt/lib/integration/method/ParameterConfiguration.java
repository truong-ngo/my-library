package com.nxt.lib.integration.method;

import lombok.Data;

import java.util.Map;

/**
 * Parameter configuration
 * */
@Data
public class ParameterConfiguration {

    /**
     * Indicate parameter index
     * */
    private Integer paramIndex;

    /**
     * Simple parameter value extractor<br/>
     * Only use simple or composite type not both
     * @see #compositeExtractExpression
     * */
    private String simpleExtractExpression;

    /**
     * Composite parameter value extractor in case input value is from many source<br/>
     * Key: Parameter attribute<br/>
     * Value: Parameter attribute value's extractor<br/>
     * @see #simpleExtractExpression
     * */
    private Map<String, String> compositeExtractExpression;
}
