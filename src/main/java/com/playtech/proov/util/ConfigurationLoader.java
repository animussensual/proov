package com.playtech.proov.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads properties and caches these for later requests
 */
public class ConfigurationLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);
    private static Map<String, Properties> configurations = new ConcurrentHashMap<>();

    public static Properties getProperties(String name) {
        if (configurations.containsKey(name)) {
            return configurations.get(name);
        } else {
            synchronized (configurations) {
                if (!configurations.containsKey(name)) {
                    Properties properties = new Properties();
                    URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
                    try (InputStream inputStream = resource.openStream()) {
                        properties.load(inputStream);
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                    configurations.put(name, properties);
                }
            }
            return configurations.get(name);
        }
    }
}
