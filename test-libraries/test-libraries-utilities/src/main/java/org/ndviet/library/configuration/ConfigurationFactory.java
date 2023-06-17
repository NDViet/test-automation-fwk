package org.ndviet.library.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.ndviet.library.configuration.Constants.PROP_CONFIGURATION_BASE;
import static org.ndviet.library.configuration.Constants.PROP_CONFIGURATION_ORDERING;

public class ConfigurationFactory {

    public static ConfigurationOrdering createInstance() {
        ConfigurationOrdering configurations;
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
            propertiesConfiguration.readConfigurationFrom(null);
            List<String> configurationOrdering = extractOrderingConfigurations(propertiesConfiguration);
            configurations = new ConfigurationOrdering(propertiesConfiguration.getValue(PROP_CONFIGURATION_BASE), configurationOrdering);
        } catch (Exception e) {
            throw new RuntimeException("Could not create Configuration instance");
        }
        return configurations;
    }

    private static List<String> extractOrderingConfigurations(AbstractConfiguration configuration) {
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
}
