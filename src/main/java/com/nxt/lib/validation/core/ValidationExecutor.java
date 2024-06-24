package com.nxt.lib.validation.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Execute validation base on defined rule
 * */
public class ValidationExecutor {

    /**
     * Validation rule configuration
     * */
    private final RuleConfiguration configuration;

    /**
     * Object need to be validate
     * */
    private final Object context;

    /**
     * Constructor base on classpath of validation json file and object need to be validate
     * */
    public ValidationExecutor(RuleConfiguration ruleConfiguration, Object context) {
        this.configuration = ruleConfiguration;
        this.context = context;
    }

    /**
     * Get configuration from class path
     * @param path: path to configuration file
     * @return {@link RuleConfiguration}
     * */
    public static RuleConfiguration getRuleConfiguration(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream in = resource.getInputStream()) {
            return new ObjectMapper().readValue(in.readAllBytes(), RuleConfiguration.class);
        } catch (Exception e) {
            throw new ValidationException(Map.of("message", "error occur while reading rule configuration"));
        }
    }

    /**
     * Get value from expression with desire type
     * @param expressionString: SpEl expression string
     * @param clazz: desire return type
     * @param context: context for expression evaluation
     * @return an instance of type {@link T}
     * */
    public <T> T getValueFromExpression(String expressionString, Class<T> clazz, Object context) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expressionString);
        return expression.getValue(context, clazz);
    }

    /**
     * Get value from expression with desire type
     * @param expressionString: SpEl expression string
     * @param context: context for expression evaluation
     * @return evaluation {@link Object} result
     * */
    public Object getValueFromExpression(String expressionString, Object context) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expressionString);
        return expression.getValue(context);
    }

    /**
     * Validation entry point
     * @return {@link ValidationResult}: result of validation process
     * */
    public ValidationResult validate() {
        if (configuration.isArrayConfiguration()) {
            return arrayValidate(configuration.getFieldName() + "[%d].");
        } else {
            if (configuration.isBasicConfiguration()) {
                return basicValidate();
            } else {
                return compositeValidate();
            }
        }
    }

    /**
     * Basic case validation
     * */
    public ValidationResult basicValidate() {
        Map<String, Object> message = new LinkedHashMap<>();
        try {
            boolean result = Boolean.TRUE.equals(getValueFromExpression(configuration.getRuleExpression(), Boolean.class, context));
            if (!result) {
                message.put(configuration.getFieldName(), configuration.getMessage());
            } else {
                message = null;
            }
            return new ValidationResult(result, message);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            message = new LinkedHashMap<>();
            message.put(configuration.getFieldName(), "invalid validation's expression");
            return new ValidationResult(false, message);
        }
    }

    /**
     * Composite case validation
     * */
    public ValidationResult compositeValidate() {
        boolean result;
        List<ValidationResult> results = configuration.getSubRules().stream()
                .filter(c -> (c.getCondition() == null || getValueFromExpression(c.getCondition(), Boolean.class, context)))
                .map(r -> new ValidationExecutor(r, context).validate()).toList();
        if (configuration.getCombineType().isAnd() || configuration.getCombineType().isCondition()) {
            result = results.stream().allMatch(ValidationResult::isValid);
            return getValidationResult(results, result);
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
            message.put(configuration.getFieldName(), messages);
            return new ValidationResult(result, message);
        }
    }

    /**
     * Array case validation
     * */
    @SuppressWarnings("unchecked")
    public ValidationResult arrayValidate(String rootPath) {
        RuleConfiguration arrayElementConfiguration = getRuleConfiguration(configuration.getArrayElementConfigPath());
        List<Object> objects = (List<Object>) getValueFromExpression("#this." + configuration.getFieldName(), context);
        List<ValidationResult> results = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            ValidationExecutor executor = new ValidationExecutor(arrayElementConfiguration, objects.get(i));
            ValidationResult r = executor.validate();
            if (!r.isValid()) {
                int finalI = i;
                Map<String, Object> message = r.getMessages()
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> String.format(rootPath, finalI) + entry.getKey(),
                                Map.Entry::getValue,
                                (exist, replace) -> exist,
                                LinkedHashMap::new
                        ));
                results.add(new ValidationResult(r.isValid(), message));
            }
        }
        boolean isValid = results.stream().allMatch(ValidationResult::isValid);
        return getValidationResult(results, isValid);
    }

    private ValidationResult getValidationResult(List<ValidationResult> results, boolean isValid) {
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
