package com.nxt.lib.validation.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that annotated parameter need to be validated. Usage:
 * <blockquote><pre>
 * {@code @Validated}
 * public void someMethod({@code @Valid}(rule = "...rule.json")) {
 *     method content
 * }
 * </pre></blockquote>
 * @see Validated
 * @author Truong Ngo
 * */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {

    /**
     * Rule configuration location
     * */
    String rule();
}
