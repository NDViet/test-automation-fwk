package com.ndviet.libary.configuration;

import com.ndviet.libary.map.MapUtils;
import com.ndviet.libary.yaml.YamlUtils;

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
