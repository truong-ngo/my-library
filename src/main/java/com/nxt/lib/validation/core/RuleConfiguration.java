package com.nxt.lib.validation.core;

import com.nxt.lib.utils.StringUtils;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Hold the validation logic of a parameter
 * <p>
 * Java class model for rule configuration file define in {@code @Valid}.
 * <p>
 * Describe the validation logic of entire object as below:
 * <ul>
 *     <li>Basic case: Single field validation</li>
 *     <li>Composite case: Group of field validation with combine operator</li>
 *     <li>Array case: Contain the path of array element validation rule</li>
 * </ul>
 * @see Valid
 * @author Truong Ngo
 * */
@Data
public class RuleConfiguration {

    /**
     * Indicate the target that need to be validated. Can be:
     * <ul>
     *     <li>A rule of single field</li>
     *     <li>A group of rule</li>
     *     <li>Conditional rule</li>
     * </ul>
     * of object that need to be validated
     * */
    private String target;

    /**
     * Display name of target;
     * */
    private String targetName;

    /**
     * Rule message, indicate how the target should be
     * */
    private String message;

    /**
     * A SpEl expression indicate that the validation is applied or not<br/>
     * If the value is null or true then the validation will be applied
     * */
    private String condition;

    /**
     * A SpEl expression that evaluate the validation of object's field<br/>
     * This is the basic form of rule
     * */
    private String ruleExpression;

    /**
     * Indicate the configuration of array element in case field is array<br/>
     * This is rule for array member
     * */
    private String arrayElementConfig;

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
        return (!StringUtils.isTrimEmpty(ruleExpression)) &&
               (Objects.isNull(arrayElementConfig)) &&
               (Objects.isNull(combineType) && Objects.isNull(subRules));
    }

    /**
     * Determine if rule is in array form
     * */
    public boolean isArrayConfiguration() {
        return (Objects.isNull(ruleExpression)) &&
               (Objects.nonNull(arrayElementConfig)) &&
               (Objects.isNull(combineType) && Objects.isNull(subRules));
    }

    /**
     * Determine if rule is in composite form
     * */
    public boolean isCompositeConfiguration() {
        return (Objects.isNull(ruleExpression)) &&
               (Objects.isNull(arrayElementConfig)) &&
               (Objects.nonNull(combineType) && Objects.nonNull(subRules));
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
