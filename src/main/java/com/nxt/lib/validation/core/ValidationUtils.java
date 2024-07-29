package com.nxt.lib.validation.core;

import com.nxt.lib.utils.IOUtils;
import com.nxt.lib.utils.SpElUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility for validation
 * @author Truong Ngo
 * */
public class ValidationUtils {

    /**
     * Rule key constant
     * */
    public static final String RULE_KEY = "rule";

    /**
     * Rule format constant
     * */
    public static final String RULE_FORMAT_KEY = "ruleFormat";

    /**
     * Invalid rule message
     * */
    public static final String INVALID_RULE_MESSAGE = "Invalid rule or rule not found!";

    /**
     * Invalid rule format
     * */
    public static final String INVALID_RULE_FORMAT_MESSAGE = "Invalid rule format!";

    /**
     * Invalid expression syntax
     * */
    public static final String INVALID_EXPRESSION_SYNTAX_PATTERN = "Invalid rule expression syntax: %s!";

    /**
     * Invalid condition syntax
     * */
    public static final String INVALID_CONDITION_SYNTAX_PATTERN = "Invalid rule condition syntax: %s!";

    /**
     * Resolve condition of rule
     * @param config the rule config
     * @param context object to extract value
     * @return true if condition is null or resolve result is true, otherwise false
     * @throws ValidationException if the condition's string expression is invalid
     * */
    public static Boolean getCondition(RuleConfiguration config, Object context) {
        return  Objects.isNull(config.getCondition()) ||
                SpElUtils
                        .getValue(config.getCondition(), context, Boolean.class)
                        .orElseThrow(() -> new ValidationException(Map.of(
                                config.getTargetName(),
                                String.format(INVALID_CONDITION_SYNTAX_PATTERN, config.getCondition())
                        )));
    }

    /**
     * Resolve rule expression
     * @param config the rule config
     * @param context object to extract value
     * @return true if resolve result is true, otherwise false
     * @throws ValidationException if the rule's string expression is invalid
     * */
    public static Boolean getExpressionResult(RuleConfiguration config, Object context) {
        return SpElUtils
                .getValue(config.getRuleExpression(), context, Boolean.class)
                .orElseThrow(() -> new ValidationException(Map.of(
                        config.getTarget(),
                        String.format(ValidationUtils.INVALID_EXPRESSION_SYNTAX_PATTERN, config.getRuleExpression())
                )));
    }

    /**
     * Get the rule configuration
     * @param path the path lead to rule file
     * @return {@code RuleConfiguration} - the configuration as Java object
     * @throws ValidationException if path is invalid or not found or the rule file structure is invalid
     * */
    public static RuleConfiguration getRuleConfiguration(String path) {
        return IOUtils
                .getResource(path, RuleConfiguration.class)
                .orElseThrow(() -> new ValidationException(Map.of(
                        ValidationUtils.RULE_KEY,
                        ValidationUtils.INVALID_RULE_MESSAGE)));
    }

    /**
     * Get the value of element that is array in configuration
     * @return {@code List<Object>} as value of array element
     * @throws ValidationException if the value of {@code RuleConfiguration.arrayElementConfig} or
     * {@code RuleConfiguration.target} is invalid
     * */
    public static List<?> getArrayElement(RuleConfiguration config, Object context) {
        return (List<?>) SpElUtils
                .getValue("#this." + config.getTarget(), context, List.class)
                .orElseThrow(() -> new ValidationException(Map.of(config.getTarget(), INVALID_RULE_MESSAGE)));
    }
}
