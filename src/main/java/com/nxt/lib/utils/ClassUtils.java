package com.nxt.lib.utils;

import java.util.List;

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

}
