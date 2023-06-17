package org.ndviet.library.configuration;

import java.util.LinkedHashMap;
import java.util.List;

public interface IConfiguration {
    String getValue(String key);

    List getListValues(String key);

    LinkedHashMap getMapValues(String key);
}
