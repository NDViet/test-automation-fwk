package com.ndviet.configuration;

import com.ndviet.map.MapUtils;
import com.ndviet.yaml.YamlUtils;

public class YamlConfiguration extends Configuration implements ConfigurationInterface {
    public YamlConfiguration() {
        super();
    }

    @Override
    public void readConfigurationFrom(String filePath) throws Exception {
        this.m_data = YamlUtils.readYaml(filePath);
    }

    @Override
    public String getValue(String key) {
        return MapUtils.getValueAsString(this.m_data, key);
    }
}
