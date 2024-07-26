package com.nxt.lib.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

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
    public static <T> Optional<T> getResource(String path, Class<T> clazz) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStream in = resource.getInputStream();
            T result = new ObjectMapper().readValue(in.readAllBytes(), clazz);
            in.close();
            return Optional.of(result);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Get resource from class path<br/>
     * Use when the data is generic type like {@link java.util.List}, {@link java.util.Map} etc...
     * @param type : desire {@link TypeReference<T>}
     * @param path: path to resource file
     * @return desire resource
     * */
    public static <T> Optional<T> getResource(String path, TypeReference<T> type) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStream in = resource.getInputStream();
            T result = new ObjectMapper().readValue(in.readAllBytes(), type);
            in.close();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
