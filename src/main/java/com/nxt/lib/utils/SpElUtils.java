package com.nxt.lib.utils;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Optional;

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
     * @return value extracted as {@code Optional} of {@link Object} type
     * */
    public static Optional<Object> getValue(String expression, Object context) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return Optional.ofNullable(exp.getValue(context));
        } catch (ParseException | EvaluationException | IllegalAccessError e) {
            return Optional.empty();
        }
    }

    /**
     * Get value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: extraction's context
     * @param clazz: return type
     * @return optional of {@link T} type
     * */
    public static <T> Optional<T> getValue(String expression, Object context, Class<T> clazz) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return Optional.ofNullable(exp.getValue(context, clazz));
        } catch (ParseException | EvaluationException | IllegalAccessError e) {
            return Optional.empty();
        }
    }
}
