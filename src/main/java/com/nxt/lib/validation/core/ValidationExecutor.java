package com.nxt.lib.validation.core;

import com.nxt.lib.integration.utils.IntegrationUtils;
import com.nxt.lib.utils.IOUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Execute validation
 * @author Truong Ngo
 * */
public class ValidationExecutor {

    /**
     * Rule configuration
     * */
    private final RuleConfiguration configuration;

    /**
     * Object need to be validated
     * */
    private final Object context;

    /**
     * Constructor base on configuration and validate object
     * */
    public ValidationExecutor(RuleConfiguration ruleConfiguration, Object context) {
        this.configuration = ruleConfiguration;
        this.context = context;
    }

    /**
     * Validation processor<br>
     * @return {@code ValidationResult} - result of validation process
     * @see ValidationResult
     * */
    public ValidationResult validate() {
        return configuration.isArrayConfiguration() ?
                arrayValidate(configuration.getTarget() + "[%d].") :
                configuration.isBasicConfiguration() ? basicValidate() : compositeValidate();
    }

    /**
     * Basic case validation
     * @return {@code ValidationResult} - result
     * */
    public ValidationResult basicValidate() {
        Boolean expressionResult = ValidationUtils.getExpressionResult(configuration, context);
        Map<String, Object> message = expressionResult ? null : Map.of(configuration.getTarget(), configuration.getMessage());
        return new ValidationResult(expressionResult, message);
    }

    /**
     * Composite case validation
     * @return {@code ValidationResult} - result
     * */
    public ValidationResult compositeValidate() {
        boolean result;
        List<ValidationResult> results = configuration.getSubRules().stream()
                .filter(c -> ValidationUtils.getCondition(configuration, context))
                .map(r -> new ValidationExecutor(r, context).validate()).toList();
        if (configuration.getGroupType().isAnd()) {
            result = results.stream().allMatch(ValidationResult::isValid);
            return getConjunctionValidationResult(results, result);
        } else {
            result = results.stream().anyMatch(ValidationResult::isValid);
            Map<String, Object> subMessage = result ? null : results.stream()
                    .flatMap(vr -> vr.getMessages().entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (exist, replace) -> exist,
                            LinkedHashMap::new
                    ));
            Map<String, Object> messages = new LinkedHashMap<>();
            messages.put("condition", "At least one of these condition must match");
            messages.put("messages", subMessage);
            Map<String, Object> message = new LinkedHashMap<>();
            message.put(configuration.getTarget(), messages);
            return new ValidationResult(result, message);
        }
    }

    /**
     * Array case validation
     * @return {@code ValidationResult} - result
     * */
    public ValidationResult arrayValidate(String rootPath) {
        RuleConfiguration arrayElementConfiguration = ValidationUtils.getRuleConfiguration(configuration.getArrayElementConfig());
        List<?> objects = ValidationUtils.getArrayElement(configuration, context);
        List<ValidationResult> results = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            ValidationExecutor executor = new ValidationExecutor(arrayElementConfiguration, objects.get(i));
            ValidationResult r = executor.validate();
            if (!r.isValid()) {
                int j = i;
                Map<String, Object> message = r.getMessages()
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> String.format(rootPath, j) + entry.getKey(),
                                Map.Entry::getValue,
                                (exist, replace) -> exist,
                                LinkedHashMap::new
                        ));
                results.add(new ValidationResult(r.isValid(), message));
            }
        }
        boolean isValid = results.stream().allMatch(ValidationResult::isValid);
        return getConjunctionValidationResult(results, isValid);
    }

    /**
     * Utility method for {@link #arrayValidate(String)} and {@link #compositeValidate()}
     * */
    private ValidationResult getConjunctionValidationResult(List<ValidationResult> results, boolean isValid) {
        Map<String, Object> message = isValid ? null : results.stream()
                .filter(vr -> !vr.isValid())
                .flatMap(vr -> vr.getMessages().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (exist, replace) -> exist,
                        LinkedHashMap::new
                ));
        return new ValidationResult(isValid, message);
    }
}
