package com.nxt.lib.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * String utility
 * @author Truong Ngo
 * */
public class StringUtils {

    /**
     * Check if given string is empty or not (ignored white space of start and end of string)
     * @param s string to check
     * @return true if string is null or trim() of string is empty
     * */
    public static boolean isTrimEmpty(String s) {
        return Objects.isNull(s) || s.trim().isEmpty();
    }

    /**
     * Check if given string is empty or not
     * @param s string to check
     * @return true if string is null or string is empty
     * */
    public static boolean isEmpty(String s) {
        return Objects.isNull(s) || s.isEmpty();
    }


    /**
     * Utility method for get a non-null value of string
     * @param s given string
     * @return if string is null or blank then return empty string otherwise return original string
     * */
    public static String nvl(String s) {
        return isTrimEmpty(s) ? "" : s;
    }


    /**
     * Split given string to list of object type {@code T}
     * @param <T> result type
     * @param s a given string that need to be converted
     * @param converter converter
     * @param delimiter delimiter
     * @return {@code List<T>} collection
     * */
    public static <T> List<T> split(String s, Function<String, T> converter, String delimiter) {
        if (isTrimEmpty(s)) return Collections.emptyList();
        return Stream.of(s.split(delimiter)).map(converter).toList();
    }


    /**
     * Join a given list of type {@code T} to string
     * @param <T> list type
     * @param list a given list
     * @param converter converter
     * @param delimiter delimiter
     * @return result string
     * */
    public static <T> String join(List<T> list, Function<T, String> converter, String delimiter) {
        if (Objects.isNull(list) || list.isEmpty()) return null;
        return list.stream().map(converter).collect(Collectors.joining(delimiter));
    }
}
