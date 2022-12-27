package com.ndviet.libary.configuration;

import com.ndviet.libary.json.JsonUtils;
import com.ndviet.libary.map.MapUtils;

import java.util.List;

public class JsonConfiguration extends Configuration implements ConfigurationInterface {
    public JsonConfiguration() {
        super();
    }

    @Override
    public void readConfigurationFrom(String filePath) throws Exception {
        this.m_data = JsonUtils.readJson(filePath);
    }

    @Override
    public String getValue(String key) {
        return MapUtils.getValueAsString(this.m_data, key);
    }

    @Override
    public List<String> getListValues(String key) {
        return (List) MapUtils.getValueAsObject(this.m_data, key);
    }
}
