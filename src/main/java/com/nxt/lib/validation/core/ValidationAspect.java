package com.nxt.lib.validation.core;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Intercept method to perform validation
 * */
@Aspect
@Component
public class ValidationAspect {

    /**
     * Entry point that read annotated method and parameter need to be validated
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
                    RuleConfiguration rule = ValidationExecutor.getRuleConfiguration(path);
                    ValidationResult result = new ValidationExecutor(rule, args[i]).validate();
                    if (!result.isValid()) {
                        throw new ValidationException(result.getMessages());
                    }
                }
            }
        }
    }
}
