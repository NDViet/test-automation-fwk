package org.ndviet.library.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.LinkedHashMap;

public class JsonUtils {
    public static LinkedHashMap readJson(String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap map = objectMapper.readValue(new File(filePath), LinkedHashMap.class);
        return map;
    }
}
