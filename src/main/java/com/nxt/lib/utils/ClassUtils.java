package com.nxt.lib.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Class Utility
 * @author Truong Ngo
 * */
public class ClassUtils {

    /**
     * Prevent instantiate
     * */
    private ClassUtils() {
    }

    /**
     * Wrapper type collections
     * */
    private static final List<Class<?>> WRAPPER_TYPES = List.of(
            Boolean.class, Character.class, Byte.class,
            Short.class, Integer.class, Long.class,
            Float.class, Double.class, Void.class
    );

    /**
     * Check if object is Wrapper type
     * @param o: checked object
     * */
    public static boolean isWrapper(Object o) {
        return WRAPPER_TYPES.contains(o.getClass());
    }

    /**
     * Check if object is primitive type
     * @param o: checked object
     * */
    public static boolean isPrimitive(Object o) {
        return o.getClass().isPrimitive();
    }

    /**
     * Get all field include field in super class
     * @param clazz the class that need to get all field
     * @return All field present
     * */
    public static List<Field> getAllFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> result = new ArrayList<>(Arrays.asList(fields));
        Class<?> parentClass = clazz.getSuperclass();
        if (parentClass != null) {
            result.addAll(getAllFields(parentClass));
        }
        return result;
    }

    /**
     * Get field by field name
     * @param clazz class that need to get field
     * @param fieldName field name
     * @return match field otherwise null
     * */
    public static Field getField(Class<?> clazz, String fieldName) {
        List<Field> fields = getAllFields(clazz);
        return fields
                .stream()
                .peek(f -> f.setAccessible(true))
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get field by field name
     * @param clazz class that need to get field
     * @param fieldName field name
     * @return match field
     * @throws X if field is not found
     * */
    public static <X extends Throwable> Field getFieldOrElseThrow(Class<?> clazz, String fieldName, Supplier<X> exSupplier) throws X {
        List<Field> fields = getAllFields(clazz);
        return fields
                .stream()
                .peek(f -> f.setAccessible(true))
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElseThrow(exSupplier);
    }
}
