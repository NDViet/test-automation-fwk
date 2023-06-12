package org.ndviet.library.configuration;

import java.util.LinkedHashMap;
import java.util.List;

public interface ConfigurationInterface {
    String getValue(String key);

    List getListValues(String key);

    LinkedHashMap getMapValues(String key);
}
