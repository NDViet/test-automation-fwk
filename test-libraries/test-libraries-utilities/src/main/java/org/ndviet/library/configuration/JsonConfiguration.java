package org.ndviet.library.configuration;

import org.ndviet.library.json.JsonUtils;
import org.ndviet.library.map.MapUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class JsonConfiguration extends AbstractConfiguration {
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
    public List getListValues(String key) {
        Object values = MapUtils.getValueAsObject(this.m_data, key);
        if (values instanceof List || values == null) {
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
