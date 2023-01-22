package org.ndviet.library.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.ndviet.library.configuration.Constants.PROP_CONFIGURATION_BASE;
import static org.ndviet.library.configuration.Constants.PROP_CONFIGURATION_ORDERING;

public class ConfigurationFactory implements ConfigurationInterface {

    private static ConfigurationFactory m_instance = null;
    private static ConfigurationOrdering m_configurations;

    public ConfigurationFactory() throws Exception {
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.readConfigurationFrom(null);
        List<String> configurationOrdering = extractOrderingConfigurations(propertiesConfiguration);
        m_configurations = new ConfigurationOrdering(propertiesConfiguration.getValue(PROP_CONFIGURATION_BASE), configurationOrdering);
    }

    public static ConfigurationFactory getInstance() {
        if (m_instance == null)
            try {
                m_instance = new ConfigurationFactory();
            } catch (Exception ex) {

            }
        return m_instance;
    }

    private List<String> extractOrderingConfigurations(Configuration configuration) {
        List<String> listKeys = new ArrayList<>();
        for (String key : (Set<String>) configuration.getData().keySet()) {
            if (key.contains(PROP_CONFIGURATION_ORDERING)) {
                listKeys.add(key);
            }
        }
        listKeys.stream().sorted().collect(Collectors.toList());
        List<String> listFiles = new ArrayList<>();
        for (String key : listKeys) {
            if (configuration.getValue(key) != null && !configuration.getValue(key).isEmpty())
                listFiles.add(configuration.getValue(key));
        }
        return listFiles;
    }

    @Override
    public String getValue(String key) {
        return m_configurations.getValue(key);
    }

    @Override
    public List<String> getListValues(String key) {
        return m_configurations.getListValues(key);
    }
}
