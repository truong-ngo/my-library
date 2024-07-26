package com.nxt.lib.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * IO Utility
 * @author Truong Ngo
 * */
public class IOUtils {

    /**
     * Prevent instantiate
     * */
    private IOUtils() {}

    /**
     * Get resource from class path
     * @param clazz : desire {@link Class<T>}
     * @param path: path to resource file
     * @return desire resource
     * */
    public static <T> T getResource(String path, Class<T> clazz) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        InputStream in = resource.getInputStream();
        T result = new ObjectMapper().readValue(in.readAllBytes(), clazz);
        in.close();
        return result;
    }

    /**
     * Get resource from class path<br/>
     * Use when the data is generic type like {@link java.util.List}, {@link java.util.Map} etc...
     * @param type : desire {@link TypeReference<T>}
     * @param path: path to resource file
     * @return desire resource
     * */
    public static <T> T getResource(String path, TypeReference<T> type) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        InputStream in = resource.getInputStream();
        T result = new ObjectMapper().readValue(in.readAllBytes(), type);
        in.close();
        return result;
    }
}
