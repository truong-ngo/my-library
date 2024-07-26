package com.nxt.lib.validation.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marker annotation indicate that that annotated method has parameter need to be validated
 * <p>
 * Notice that this annotation effect will only triggered when the annotated method is called
 * via dependency injection so that spring aop will create a join point at the beginning of
 * the method to perform validation. If the annotated method is call directly from other
 * method in same class then the effect won't be trigger.
 * <p>
 * The validation will be triggered at {@code ValidationAspect} and performed by {@code ValidationExecutor}
 * <p>
 * The parameter need to be validated must be annotated with {@code @Valid}
 * @see Valid
 * @see ValidationAspect
 * @see ValidationExecutor
 * @author Truong Ngo
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validated {}
