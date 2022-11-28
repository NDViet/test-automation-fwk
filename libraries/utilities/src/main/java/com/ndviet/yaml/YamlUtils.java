package com.ndviet.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

import org.yaml.snakeyaml.Yaml;

public class YamlUtils {
    public static LinkedHashMap readYaml(String filePath) throws Exception {
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(new File(filePath));
        LinkedHashMap map = yaml.load(input);
        return map;
    }

    private static List<String> getSegments(String yamlPath) {
        return Arrays.asList(yamlPath.split("\\."));
    }

    public static String getValueAsString(LinkedHashMap yamlMap, String yamlPath) {
        List<String> segments = getSegments(yamlPath);
        Object value = getValueAsObject(yamlMap, segments.listIterator());
        if(value != null) {
            return value.toString();
        }
        return null;
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
