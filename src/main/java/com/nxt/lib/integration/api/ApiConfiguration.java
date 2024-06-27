package com.nxt.lib.integration.api;

import lombok.Data;

import java.util.Map;

/**
 * Configuration for api calling
 * */
@Data
public class ApiConfiguration {

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
     * Path variable config<br/>
     * Key: the name of path variable<br/>
     * Value: expression to extract value from sources
     * */
    private Map<String, String> pathVariables;

    /**
     * Query param config<br/>
     * Key: the name of query param<br/>
     * Value: expression to extract value from sources
     * */
    private Map<String, String> queryParams;

    /**
     * Header config<br/>
     * Key: the name of header variable<br/>
     * Value: expression to extract value from sources
     * */
    private Map<String, String> headers;

    /**
     * Body config<br/>
     * Key: attribute of body<br/>
     * Value: expression to extract value from sources
     * */
    private Map<String, String> body;
}
