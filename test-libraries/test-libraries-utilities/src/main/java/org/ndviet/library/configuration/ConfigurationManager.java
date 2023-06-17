package org.ndviet.library.configuration;

public class ConfigurationManager {
    private static final ThreadLocal<ConfigurationOrdering> configurations = new ThreadLocal<>();

    private ConfigurationManager() {
    }

    public static ConfigurationOrdering getInstance() {
        if (configurations.get() == null) {
            setInstance(ConfigurationFactory.createInstance());
        }
        return configurations.get();
    }

    public static void setInstance(ConfigurationOrdering configurationOrdering) {
        ConfigurationManager.configurations.set(configurationOrdering);
    }

    public static void release() {
        configurations.remove();
    }
}
