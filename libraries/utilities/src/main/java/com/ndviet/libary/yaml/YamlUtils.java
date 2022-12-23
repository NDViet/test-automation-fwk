package com.ndviet.libary.yaml;

import com.ndviet.libary.file.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class YamlUtils {
    public static LinkedHashMap readYaml(String filePath) throws Exception {
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(filePath);
        LinkedHashMap map = yaml.load(input);
        return map;
    }

    public static LinkedHashMap readAllYaml(String filePath) throws Exception {
        Yaml yaml = new Yaml();
        LinkedHashMap multiYaml = new LinkedHashMap<>();
        InputStream input = new FileInputStream(filePath);
        for (Object o : yaml.loadAll(input)) {
            multiYaml.putAll((LinkedHashMap) o);
        }
        return multiYaml;
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

    public static LinkedHashMap readAllYamlInDirectory(String directory) throws Exception {
        LinkedHashMap<String, String> multiYaml = new LinkedHashMap<>();
        List<String> files = new ArrayList<>();
        files = FileUtils.recursiveGetListFiles(directory, files, "y.*ml");
        for (String filePath : files) {
            multiYaml.putAll(readAllYaml(filePath));
        }
        return multiYaml;
    }

}
