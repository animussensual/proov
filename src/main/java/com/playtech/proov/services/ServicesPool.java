package com.playtech.proov.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Container for initialized services
 */
public class ServicesPool {

    private Map<Class, Object> services = new ConcurrentHashMap<>();


    public <T> T getService(Class<T> serviceInterface) {
        return (T) services.get(serviceInterface);
    }

    public synchronized void addService(Class serviceInterface, Object service) {
        if (services.containsKey(serviceInterface)) {
            throw new IllegalArgumentException("Service already exists " + serviceInterface);
        }
        services.put(serviceInterface, service);
    }
}
