package com.nxt.lib.integration.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nxt.lib.integration.OperationalConfiguration;
import com.nxt.lib.integration.ValueSource;
import lombok.Data;

import java.util.Map;

/**
 * Configuration for api calling
 * @see ApiExecutor
 * @author Truong Ngo
 * */
@Data
public class ApiConfiguration implements OperationalConfiguration {

    /**
     * Api call condition
     * */
    private String invokeCondition;

    /**
     * Indicate api name, use as api cache key
     * */
    private String apiName;

    /**
     * Api url
     * */
    private String url;

    /**
     * Http method
     * */
    private String method;

    /**
     * Path variable value configuration
     * <p>
     * Key: the name of path variable<br/>
     * Value: expression to extract value from sources
     * </p>
     * */
    private Map<String, String> pathVariables;

    /**
     * Query param value configuration
     * <p>
     * Key: the name of query param<br/>
     * Value: expression to extract value from sources
     * </p>
     * */
    private Map<String, String> queryParams;

    /**
     * Header value configuration
     * <p>
     * Key: the name of header variable<br/>
     * Value: expression to extract value from sources
     * </p>
     * */
    private Map<String, String> headers;

    /**
     * Body value configuration
     * */
    private Object body;

    @Override
    @JsonIgnore
    public String getName(ValueSource source) {
        return apiName;
    }
}
