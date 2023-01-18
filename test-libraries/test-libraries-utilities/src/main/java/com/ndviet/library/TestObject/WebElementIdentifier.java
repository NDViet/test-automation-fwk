package com.ndviet.library.TestObject;

import com.ndviet.library.configuration.ConfigurationFactory;
import com.ndviet.library.map.MapUtils;
import com.ndviet.library.yaml.YamlUtils;

import java.util.LinkedHashMap;

import static com.ndviet.library.configuration.Constants.WEB_ELEMENT_IDENTIFIERS_DIRECTORY;

public class WebElementIdentifier {
    private static LinkedHashMap m_data = new LinkedHashMap<>();
    private static WebElementIdentifier m_instance;

    public WebElementIdentifier() throws Exception {
        setElementFiles();
    }

    public static WebElementIdentifier getInstance() throws Exception {
        if (m_instance == null) {
            m_instance = new WebElementIdentifier();
        }
        return m_instance;
    }

    public void setElementFiles() throws Exception {
        String directory = ConfigurationFactory.getInstance().getValue(WEB_ELEMENT_IDENTIFIERS_DIRECTORY);
        setElementFiles(directory);
    }

    public void setElementFiles(String directory) throws Exception {
        m_data.putAll(YamlUtils.readAllYamlInDirectory(directory));
    }

    public String getIdentifier(String key) {
        return MapUtils.getValueAsString(m_data, key);
    }
}
