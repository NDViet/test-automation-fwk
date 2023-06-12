package org.ndviet.library.configuration;

import java.util.LinkedHashMap;
import java.util.List;

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

    public abstract List getListValues(String key);

    public abstract LinkedHashMap getMapValues(String key);
}
