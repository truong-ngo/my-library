package com.nxt.lib.validation.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * A rule for validating object (context)
 * @author Truong Ngo
 * */
@Data
public class RuleConfiguration {

    /**
     * Rule name
     * */
    private String ruleName;

    /**
     * Indicate object's field that need to be validated
     * */
    private String fieldName;

    /**
     * Message indicate that how the field should be valid
     * */
    private String message;

    /**
     * Indicate that the validation is applied or not<br/>
     * If the value is null then the validation will be applied
     * */
    private String condition;

    /**
     * A SpEl expression that evaluate the validation of object's field<br/>
     * This is the basic form of rule
     * */
    private String ruleExpression;

    /**
     * Indicate that the validation field is array, list or not
     * */
    private Boolean isArray;

    /**
     * Indicate the configuration of array element in case field is array
     * */
    private String arrayElementConfigPath;

    /**
     * Combine type for rule in the composite form
     * */
    private RuleCombineType combineType;

    /**
     * Child rule, this is the composite form of rule
     * */
    private List<RuleConfiguration> subRules;

    /**
     * Determine if rule is in basic form
     * */
    public boolean isBasicConfiguration() {
        return (ruleExpression != null && !ruleExpression.trim().isEmpty()) &&
               ((isArray == null || !isArray) && arrayElementConfigPath == null) &&
               (combineType == null && subRules == null);
    }

    /**
     * Determine if rule is in array form
     * */
    public boolean isArrayConfiguration() {
        return (ruleExpression == null) &&
               (isArray != null && isArray && arrayElementConfigPath != null) &&
               (combineType == null && subRules == null);
    }

    /**
     * Determine if rule is in composite form
     * */
    public boolean isCompositeConfiguration() {
        return (ruleExpression == null) &&
               ((isArray == null || !isArray) && arrayElementConfigPath == null) &&
               (combineType != null && subRules != null);
    }

    /**
     * Rule is either be basic, composite or array form
     * @throws ValidationException if above condition is not match
     * */
    public void checkFormat() {
        if (!isBasicConfiguration() && !isArrayConfiguration() && !isCompositeConfiguration()) {
            throw new ValidationException(Map.of("message", "invalid rule configuration format"));
        }
    }

    /**
     * Combine type enum
     * */
    public enum RuleCombineType {

        AND,
        OR,
        CONDITION;

        public boolean isAnd() {
            return this.equals(AND);
        }
        public boolean isCondition() {
            return this.equals(CONDITION);
        }
    }
}
