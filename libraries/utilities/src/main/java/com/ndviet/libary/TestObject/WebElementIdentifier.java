package com.ndviet.libary.TestObject;

import com.ndviet.libary.configuration.ConfigurationFactory;
import com.ndviet.libary.map.MapUtils;
import com.ndviet.libary.yaml.YamlUtils;

import java.io.File;
import java.util.LinkedHashMap;

import static com.ndviet.libary.configuration.Constants.OBJECT_REPOSITORY_DIRECTORY;
import static com.ndviet.libary.configuration.Constants.WEB_IDENTIFIERS_DIRECTORY;

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
        String directory = ConfigurationFactory.getInstance().getValue(OBJECT_REPOSITORY_DIRECTORY)
                + File.separator + ConfigurationFactory.getInstance().getValue(WEB_IDENTIFIERS_DIRECTORY);
        setElementFiles(directory);
    }

    public void setElementFiles(String directory) throws Exception {
        m_data.putAll(YamlUtils.readAllYamlInDirectory(directory));
    }

    public String getIdentifier(String key) {
        return MapUtils.getValueAsString(m_data, key);
    }
}
