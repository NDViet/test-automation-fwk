package com.ndviet.library.configuration;

import java.util.List;

public interface ConfigurationInterface {
    String getValue(String key);

    List<String> getListValues(String key);
}
