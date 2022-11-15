package com.ndviet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

public class YamlUtils {
    public static LinkedHashMap readYaml(String filePath) throws Exception {
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(new File(filePath));
        LinkedHashMap map = yaml.load(input);
        return map;
    }

    public static String getValueAsString(Map yamlMap, String yamlPath) {
        List<String> segments = Arrays.asList(yamlPath.split("\\."));
        Object value = getValueAsObject(yamlMap, segments.listIterator());
        return value.toString();
    }

    public static Object getValueAsObject(Object obj, Iterator<String> segments) {
        if(segments.hasNext()) {
            String segment = segments.next();
            if(obj instanceof LinkedHashMap) {
                Object currentObj = ((LinkedHashMap) obj).get(segment);
                if(currentObj != null) {
                    if(segments.hasNext()) {
                        return getValueAsObject(currentObj, segments);
                    } else {
                        return currentObj;
                    }
                }
            }
        } else {
            return obj.toString();
        }
        return null;
    }
}
