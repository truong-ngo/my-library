package com.nxt.lib.utils;

import java.util.Objects;

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
}
