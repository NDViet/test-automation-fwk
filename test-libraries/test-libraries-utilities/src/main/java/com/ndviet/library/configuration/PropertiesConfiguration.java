package com.ndviet.library.configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

public class PropertiesConfiguration extends Configuration implements ConfigurationInterface {
    public PropertiesConfiguration() {
        super();
        this.m_data = new LinkedHashMap<String, String>();
    }

    @Override
    public void readConfigurationFrom(String filePath) throws Exception {
        this.m_data = new LinkedHashMap<>();
        Properties properties = new Properties();
        if (filePath == null) {
            properties = System.getProperties();
            this.m_data.putAll(System.getenv());
        } else {
            InputStream inputStream = new FileInputStream(filePath);
            properties.load(inputStream);
        }
        for (Object key : properties.keySet()) {
            this.m_data.put(key.toString(), properties.get(key.toString()).toString());
            //Set property to system if it isn't present
            if (!System.getProperties().containsKey(key)) {
                System.setProperty(key.toString(), properties.get(key.toString()).toString());
            }
        }
    }

    @Override
    public String getValue(String key) {
        return (String) this.m_data.get(key);
    }

    @Override
    public List<String> getListValues(String key) {
        return null;
    }
}
