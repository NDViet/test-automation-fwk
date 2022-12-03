package com.ndviet.configuration;

import com.ndviet.json.JsonUtils;
import com.ndviet.map.MapUtils;

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
}
