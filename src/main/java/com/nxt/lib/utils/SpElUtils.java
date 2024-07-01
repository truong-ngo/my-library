package com.nxt.lib.utils;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Spring Expression Language utility
 * @author Truong Ngo
 * */
public class SpElUtils {

    /**
     * Prevent instantiate
     * */
    private SpElUtils() {}

    /**
     * Get value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: extraction's context
     * @return value extracted as {@link Object} type
     * */
    public static Object getValue(String expression, Object context) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression);
        return exp.getValue(context);
    }

    /**
     * Get value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: extraction's context
     * @param clazz: return type
     * @return value extracted as {@link T} type
     * */
    public static <T> T getValue(String expression, Object context, Class<T> clazz) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression);
        return exp.getValue(context, clazz);
    }
}
