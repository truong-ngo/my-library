package com.nxt.lib.validation.core;

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
        if (ValidationUtils.getCondition(configuration, context)) {
            Boolean result = ValidationUtils.getExpressionResult(configuration, context);
            Map<String, Object> message = result ? null : Map.of(configuration.getTarget(), configuration.getMessage());
            return new ValidationResult(result, message);
        } {
            return ValidationResult.VALID_RESULT;
        }

    }

    /**
     * Composite case validation
     * @return {@code ValidationResult} - result
     * */
    public ValidationResult compositeValidate() {

        if (!ValidationUtils.getCondition(configuration, context)) {
            return ValidationResult.VALID_RESULT;
        }

        Map<RuleConfiguration, ValidationResult> resultMap = configuration.getSubRules().stream()
                .filter(c -> ValidationUtils.getCondition(configuration, context))
                .map(r -> new AbstractMap.SimpleEntry<>(r, new ValidationExecutor(r, context).validate()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return configuration.getGroupType().isAnd() ?
                andValidation(resultMap, configuration) :
                orValidation(resultMap, configuration);
    }

    /**
     * Array case validation
     * @param rootPath the previous path
     * @return {@code ValidationResult} - result
     * */
    public ValidationResult arrayValidate(String rootPath) {
        if (!ValidationUtils.getCondition(configuration, context)) {
            return ValidationResult.VALID_RESULT;
        }
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
                                entry -> String.format(rootPath, j) + entry.getKey(), // append the root path in key
                                Map.Entry::getValue
                        ));
                results.add(new ValidationResult(r.isValid(), message));
            }
        }
        boolean isValid = results.stream().allMatch(ValidationResult::isValid);

        Map<String, Object> message = isValid ? null : results.stream()
                .filter(vr -> !vr.isValid())
                .flatMap(vr -> vr.getMessages().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (exist, replace) -> combineMessage(exist, replace, arrayElementConfiguration.getGroupType()),
                        LinkedHashMap::new
                ));

        return new ValidationResult(isValid, message);
    }

    /**
     * Utility method of {@link #compositeValidate()}
     * <p>
     * This method perform validation for and case
     * @return {@code ValidationResult}
     * */
    private ValidationResult andValidation(Map<RuleConfiguration, ValidationResult> resultMap, RuleConfiguration configuration) {
        boolean result = resultMap.values().stream().allMatch(ValidationResult::isValid);
        if (result) {
            return ValidationResult.VALID_RESULT;
        } else {
            Map<String, Object> subMessage = resultMap.values().stream()
                    .filter(vr -> !vr.isValid())
                    .flatMap(vr -> vr.getMessages().entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (exist, replace) -> combineMessage(exist, replace, configuration.getGroupType()),
                            LinkedHashMap::new
                    ));
            return new ValidationResult(false, subMessage);
        }
    }

    /**
     * Utility method of {@link #compositeValidate()}
     * <p>
     * This method perform validation for and or
     * @return {@code ValidationResult}
     * */
    private ValidationResult orValidation(Map<RuleConfiguration, ValidationResult> resultMap, RuleConfiguration configuration) {
        boolean result = resultMap.values().stream().anyMatch(ValidationResult::isValid);
        if (result) {
            return ValidationResult.VALID_RESULT;
        } else {
            Map<String, Object> subMessage = resultMap.values().stream()
                    .flatMap(vr -> vr.getMessages().entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (exist, replace) -> combineMessage(exist, replace, configuration.getGroupType()),
                            LinkedHashMap::new
                    ));
            Map<String, Object> message = new LinkedHashMap<>();
            message.put(ValidationUtils.MESSAGE_KEY, String.format(ValidationUtils.HEADER_MESSAGE_FOR_OR_CASE_PATTERN, configuration.getTarget()));
            message.putAll(subMessage);
            return new ValidationResult(false, Map.of(configuration.getTarget(), message));
        }
    }

    /**
     * Utility method for {@link #arrayValidate(String)}
     * <p>
     * Use to merge message ({@code Map<String, Object>}) of every member in group.
     * If the key is duplicate then this method will merge the values of key into
     * single value
     * <p>
     * Value of key in message is one of two type: {@code String} and {@code List}.
     * The merge logic is
     * <ul>
     *     <li>If two value is string then concatenate two string with the operator (and, or).
     *     E.g: value a or value b</li>
     *     <li>If one is String and other is List then add the String to List</li>
     *     <li>If two value List then merge two list</li>
     * </ul>
     * @param exist exist value in map
     * @param replace the duplicate key value
     * @return merge result object
     * */
    @SuppressWarnings("all")
    private Object combineMessage(Object exist, Object replace, RuleConfiguration.RuleGroupType groupType) {
        if (exist instanceof String ex && replace instanceof String rp) {
            String operator = groupType.isAnd() ? "and" : "or";
            return String.format("%s %s %s", ex, operator, rp);
        } else if (exist instanceof String ex && replace instanceof List rp) {
            rp.add(ex);
            return rp;
        } else if (exist instanceof List ex && replace instanceof String rp) {
            ex.add(rp);
            return ex;
        } else if (exist instanceof List ex && replace instanceof List rp) {
            ex.addAll(rp);
            return ex;
        }
        return ""; // Never happen as the value only one of String or List;
    }
}
