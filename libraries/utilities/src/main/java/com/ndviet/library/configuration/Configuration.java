package com.ndviet.library.configuration;

import java.util.LinkedHashMap;

public abstract class Configuration implements ConfigurationInterface {
    protected LinkedHashMap m_data;

    public Configuration() {
    }

    public LinkedHashMap getData() {
        return this.m_data;
    }

    public abstract void readConfigurationFrom(String filePath) throws Exception;

    @Override
    public abstract String getValue(String key);
}
