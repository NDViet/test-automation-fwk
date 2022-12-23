package com.ndviet.libary.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigurationOrdering implements ConfigurationInterface {
    private List<Configuration> m_configurations = new ArrayList<>();

    public ConfigurationOrdering(String baseConfigFile, List<String> orderingConfigFiles) throws Exception {
        setConfig(initializeConfiguration(null));
        for (int i = orderingConfigFiles.size() - 1; i >= 0; i--) {
            setConfig(initializeConfiguration(orderingConfigFiles.get(i)));
        }
        setConfig(initializeConfiguration(baseConfigFile));
    }

    private void setConfig(Configuration configuration) {
        if (configuration != null) {
            m_configurations.add(configuration);
        }
    }

    private Configuration initializeConfiguration(String configFilePath) throws Exception {
        Configuration configuration;
        if (configFilePath == null) {
            configuration = new PropertiesConfiguration();
            configuration.readConfigurationFrom(configFilePath);
        } else {
            String fileExtension = configFilePath.substring(configFilePath.lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase("yml") || fileExtension.equalsIgnoreCase("yaml")) {
                configuration = new YamlConfiguration();
            } else if (fileExtension.equalsIgnoreCase("json")) {
                configuration = new JsonConfiguration();
            } else if (fileExtension.equalsIgnoreCase("properties")) {
                configuration = new PropertiesConfiguration();
            } else {
                throw new RuntimeException("Configuration file type is not support!");
            }
            configuration.readConfigurationFrom(configFilePath);
        }
        return configuration;
    }

    @Override
    public String getValue(String key) {
        String value = null;
        Iterator<Configuration> iterator = m_configurations.iterator();
        do {
            Configuration configuration = iterator.next();
            value = configuration.getValue(key);
        } while (value == null && iterator.hasNext());
        return value;
    }
}
