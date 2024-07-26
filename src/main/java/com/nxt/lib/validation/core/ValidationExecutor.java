package com.nxt.lib.validation.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxt.lib.utils.IOUtils;
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
            return arrayValidate(configuration.getTarget() + "[%d].");
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
                message.put(configuration.getTarget(), configuration.getMessage());
            } else {
                message = null;
            }
            return new ValidationResult(result, message);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            message = new LinkedHashMap<>();
            message.put(configuration.getTarget(), "invalid validation's expression");
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
     * */
    @SuppressWarnings("unchecked")
    public ValidationResult arrayValidate(String rootPath) {
        RuleConfiguration arrayElementConfiguration = IOUtils
                .getResource(configuration.getArrayElementConfig(), RuleConfiguration.class)
                .orElseThrow(() -> new ValidationException(Map.of(configuration.getTarget(), "invalid array element config path or path not found!")));
        List<Object> objects = (List<Object>) getValueFromExpression("#this." + configuration.getTarget(), context);
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
        return getConjunctionValidationResult(results, isValid);
    }

    /**
     * Conjunction validation result<br/>
     * Use for array & AND case
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
