package com.nxt.lib.validation.core;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Intercept method invocation to perform validation
 * <p>
 * The method that annotated with {@code @Validated} will be intercepted and validated in here
 * @see Validated
 * @see Valid
 * @author Truong Ngo
 * */
@Aspect
@Component
public class ValidationAspect {

    /**
     * Entry point that intercept method annotated with {@code Validated} annotation
     * and perform validation
     * <p>
     * Find the method that annotated with {@code Validated} in the invocation chain
     * (invoke only through dependency injection), if exist a method then find the
     * parameter annotated with {@code @Valid} and pass the parameter and rule
     * configuration to the {@code ValidationExecutor} to perform validation
     * <p>
     * If the validation process success then continue to invoke the method otherwise
     * throw {@code ValidationException} along with all the invalid field and message
     * @see ValidationExecutor
     * @throws ValidationException if method parameter validation is failed
     * */
    @Before("@annotation(com.nxt.lib.validation.core.Validated)")
    public void validate(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Valid validAnnotation) {
                    String path = validAnnotation.rule();
                    RuleConfiguration rule = ValidationUtils.getRuleConfiguration(path);
                    rule.checkFormat(); // Ensure that the configuration is valid
                    ValidationResult result = new ValidationExecutor(rule, args[i]).validate();
                    if (!result.isValid()) throw new ValidationException(result.getMessages());
                }
            }
        }
    }
}
