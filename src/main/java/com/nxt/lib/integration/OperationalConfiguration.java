package com.nxt.lib.integration;

/**
 * Marker interface for all operation in integration process:
 * <ul>
 *     <li>Method</li>  
 *     <li>Api</li>
 *     <li>Process</li>
 * </ul>
 * @author Truong Ngo
 * */
public interface OperationalConfiguration {
    
    /**
     * <p>
     *     Invoke condition, decide whether element can be invoked or not <br/>
     *     Use as router in integration process, decide what is the next element<br/>
     *     that be invoked. 
     * </p>
     * 
     * */
    String getInvokeCondition();

    /**
     * Name of operation
     * */
    String getName(ValueSource source);
}
