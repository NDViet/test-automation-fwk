package org.ndviet.library.configuration;

import org.ndviet.library.map.MapUtils;
import org.ndviet.library.yaml.YamlUtils;

import java.util.LinkedHashMap;
import java.util.List;

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

    @Override
    public List getListValues(String key) {
        Object values = MapUtils.getValueAsObject(this.m_data, key);
        if (values instanceof List) {
            return (List) values;
        } else {
            throw new RuntimeException("Return object is not a List");
        }
    }

    @Override
    public LinkedHashMap getMapValues(String key) {
        Object values = MapUtils.getValueAsObject(this.m_data, key);
        if (values instanceof LinkedHashMap) {
            return (LinkedHashMap) values;
        } else {
            throw new RuntimeException("Return object is not a HashMap");
        }
    }
}
