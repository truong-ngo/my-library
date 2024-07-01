package com.nxt.lib.integration.api;

import lombok.Data;

import java.util.Map;

/**
 * Configuration for api
 * @author Truong Ngo
 * */
@Data
public class ApiConfiguration {

    /**
     * Api call condition
     * */
    private String callCondition;

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
}
