package com.nxt.lib.validation.core;

import com.nxt.lib.utils.StringUtils;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Hold the validation logic of a validation object, and it's field
 * <p>
 * Java class model for rule define in {@code @Valid}.
 * <p>
 * Describe the validation rule of entire object with three case:
 * <ul>
 *     <li>Basic case: Single field validation</li>
 *     <li>Composite case: Group of field validation with combine operator</li>
 *     <li>Array case: Indicate the rule configuration for member is array</li>
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
     *     <li>A group of rule (and & or)</li>
     *     <li>Conditional rule</li>
     * </ul>
     * of object that need to be validated
     * <p>
     * In most case, target is field name of validated object but in or case the
     * field name is represent more than one field. E.g: if the rule is group of rule
     * (especially in or case) the target may be like 'fieldA&fieldB'
     * */
    private String target;

    /**
     * Display name of target;
     * */
    private String targetName;

    /**
     * Rule message, describe how the target value should match<br>
     * Use as response message in case validation failed
     * */
    private String message;

    /**
     * A SpEl expression indicate that the validation is applied or not. If the value is null or
     * true then the validation will be applied
     * <p>
     * Note that {@code condition} is different with {@link #isConditional}.
     * {@code condition} indicate that the rule is applied or not while {@code isConditional}
     * only place in group rule of and indicate if the one of rule in group has match condition
     * then applied that rule (like switch - case)
     * @see #isConditional
     * */
    private String condition;

    /**
     * A SpEl expression that evaluate the validation of object's field<br/>
     * This is the basic form of rule
     * */
    private String ruleExpression;

    /**
     * Indicate the configuration of array element in case field type is array<br/>
     * This is rule for array member, all member must match the validation define
     * in this field
     * */
    private String arrayElementConfig;

    /**
     * Combine type for rule in the composite form
     * <p>
     * For the rule group format, check the {@link RuleGroupType} to see detail
     * */
    private RuleGroupType groupType;

    /**
     * A child case of group rule of and
     * <p>
     * Indicate that only one rule in group with right {@code condition} is validated,
     * also help to define the error message correctly for that group
     * @see #condition
     * */
    public Boolean isConditional;

    /**
     * Child rule, this is the composite form of rule
     * */
    private List<RuleConfiguration> subRules;

    /**
     * Determine if rule is in basic form
     * @return true if {@link #ruleExpression} is not null and other case configuration is null
     * */
    public boolean isBasicConfiguration() {
        return (!StringUtils.isTrimEmpty(ruleExpression)) &&
               (Objects.isNull(arrayElementConfig)) &&
               (Objects.isNull(groupType) && Objects.isNull(subRules));
    }

    /**
     * Determine if rule is in array form
     * @return true if {@link #arrayElementConfig} is not null and other case configuration is null
     * */
    public boolean isArrayConfiguration() {
        return (Objects.isNull(ruleExpression)) &&
               (Objects.nonNull(arrayElementConfig)) &&
               (Objects.isNull(groupType) && Objects.isNull(subRules));
    }

    /**
     * Determine if rule is in composite form
     * @return true if {@link #groupType} and {@link #subRules} is not null and other
     * case configuration is null
     * */
    public boolean isCompositeConfiguration() {
        return (Objects.isNull(ruleExpression)) &&
               (Objects.isNull(arrayElementConfig)) &&
               (Objects.nonNull(groupType) && Objects.nonNull(subRules));
    }

    /**
     * Rule is either be basic, composite or array form
     * @throws ValidationException if above condition is not match
     * */
    public void checkFormat() {
        if (!isBasicConfiguration() && !isArrayConfiguration() && !isCompositeConfiguration()) {
            throw new ValidationException(Map.of(
                    ValidationUtils.RULE_FORMAT_KEY,
                    ValidationUtils.INVALID_RULE_FORMAT_MESSAGE));
        }
    }

    /**
     * Combine type enum
     * <p>
     * Only use in group rule
     * */
    public enum RuleGroupType {

        /**
         * And group. Use for and & condition case
         * <p>
         * And case indicate that target must match all the rules define like:
         * <blockquote><pre>
         * {
         *      "groupType": AND
         *      "subRules": [
         *          {
         *              "ruleExpression": expression a
         *          },
         *          {
         *              "ruleExpression": expression b
         *          }
         *          ...
         *          other rule
         *      ]
         * }
         * </pre></blockquote>
         * <p>
         * Conditional case describe for conditional validation for specific target. E.g. like:
         * <blockquote><pre>
         * {
         *      "groupType": AND
         *      "subRules": [
         *          {
         *              "condition": a // if condition a
         *              "ruleExpression": expression a
         *          },
         *          {
         *              "condition": b // else if condition b
         *              "ruleExpression": expression b
         *          }
         *          ...
         *          other case
         *      ]
         * }
         * </pre></blockquote>
         * The message return is contain only message of rule that not valid among those rule
         * */
        AND,

        /**
         * Or group. Indicate that the target of rule is only need to match one among the rules define like:
         * <blockquote><pre>
         * {
         *      "groupType": OR
         *      "subRules": [
         *          {
         *              "ruleExpression": expression a
         *          },
         *          {
         *              "ruleExpression": expression b
         *          }
         *          ...
         *          other rule
         *      ]
         * }
         * </pre></blockquote>
         * The message return must contain all the message of those rules
         * */
        OR;

        /**
         * Check if group type is and or not
         * @return true if type is AND
         * */
        public boolean isAnd() {
            return this.equals(AND);
        }
    }
}
