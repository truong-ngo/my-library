package com.nxt.lib.integration.method;

/**
 * Result of invoking method
 *
 * @param result    Method call result
 * @param exception Exception occur during method invocation
 */
public record MethodInvocationResult(String methodSignature, Object result, MethodInvocationException exception) {

}
