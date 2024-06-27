package com.nxt.lib.integration.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ServiceUtils.applicationContext = applicationContext;
    }

    /**
     * Get service instance
     * @param clazz: service type
     * @return service of {@link T} type
     * */
    public static <T> T getService(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * Get service instance as object
     * @return Object service
     * */
    public static Object getServiceAsObject(Class<?> clazz) {
        return getService(clazz);
    }
}
