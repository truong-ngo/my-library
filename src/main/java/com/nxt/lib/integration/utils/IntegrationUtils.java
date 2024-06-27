package com.nxt.lib.integration.utils;

import com.nxt.lib.integration.IntegrationException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Integration utility
 * */
public class IntegrationUtils {

    /**
     * Prevent instantiate
     * */
    private IntegrationUtils() {}

    /**
     * Extract value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: context for extraction
     * @return value extracted as {@link Object} type
     * */
    public static Object extractValue(String expression, Object context) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(context);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new IntegrationException("Invalid expression string: " + expression, exception);
        }
    }

    /**
     * Extract value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: context for extraction
     * @param clazz: return type
     * @return value extracted as {@link T} type
     * */
    public static <T> T extractValue(String expression, Object context, Class<T> clazz) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(context, clazz);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new IntegrationException("Invalid expression string: " + expression, exception);
        }
    }
}
