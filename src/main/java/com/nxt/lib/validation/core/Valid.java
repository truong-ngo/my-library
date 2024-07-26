package com.nxt.lib.validation.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that annotated parameter need to be validated.
 * <p>
 * Parameter that need to be validated must be annotated with {@code @Valid}
 * and the method must be annotated with {@code @Validated} like this:
 * <blockquote><pre>
 * {@code @Validated}
 * public void someMethod({@code @Valid}(rule = "rule.json") AnyType param) {
 *     ...
 * }
 * </pre></blockquote>
 * Rule is the path config json file that hold the logic to validate the parameter
 * that define in {@code RuleConfiguration}
 * @see Validated
 * @see RuleConfiguration
 * @author Truong Ngo
 * */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {

    /**
     * Rule configuration path
     * */
    String rule();
}
