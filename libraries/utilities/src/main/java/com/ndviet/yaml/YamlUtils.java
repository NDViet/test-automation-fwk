package com.ndviet.yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;

import org.yaml.snakeyaml.Yaml;

public class YamlUtils {
    public static LinkedHashMap readYaml(String filePath) throws Exception {
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(filePath);
        LinkedHashMap map = yaml.load(input);
        return map;
    }

    public static LinkedHashMap readAllYaml(String filePath, String keyName, String valueName) throws Exception {
        Yaml yaml = new Yaml();
        LinkedHashMap<String, String> multiYaml = new LinkedHashMap<>();
        InputStream input = new FileInputStream(filePath);
        for (Object o : yaml.loadAll(input)) {
            LinkedHashMap map = (LinkedHashMap) o;
            multiYaml.put(map.get(keyName).toString(), map.get(valueName).toString());
        }
        return multiYaml;
    }

}
