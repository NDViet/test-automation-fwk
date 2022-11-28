package com.ndviet.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ndviet.configuration.Constants.PROP_CONFIGURATION_BASE;
import static com.ndviet.configuration.Constants.PROP_CONFIGURATION_ORDERING;

public class ConfigurationFactory implements ConfigurationInterface {

    static ConfigurationFactory m_instance = null;
    private static ConfigurationOrdering m_configurations;

    public static ConfigurationFactory getInstance() throws Exception {
        if (m_instance == null)
            return new ConfigurationFactory();
        else
            return m_instance;
    }

    public ConfigurationFactory() throws Exception {
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.readConfigurationFrom(null);
        List<String> configurationOrdering = extractOrderingConfigurations(propertiesConfiguration);
        m_configurations = new ConfigurationOrdering(propertiesConfiguration.getValue(PROP_CONFIGURATION_BASE), configurationOrdering);
    }

    private List<String> extractOrderingConfigurations(Configuration configuration) {
        List<String> listKeys = new ArrayList<>();
        for(String key : (Set<String>) configuration.getData().keySet()) {
            if(key.contains(PROP_CONFIGURATION_ORDERING)) {
                listKeys.add(key);
            }
        }
        listKeys.stream().sorted().collect(Collectors.toList());
        List<String> listFiles = new ArrayList<>();
        for(String key : listKeys) {
            listFiles.add(configuration.getValue(key));
        }
        return listFiles;
    }

    @Override
    public String getValue(String key) {
        return m_configurations.getValue(key);
    }

}
