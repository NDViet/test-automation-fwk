package com.ndviet.libary;

import com.ndviet.libary.file.DownloadHelpers;

import static com.ndviet.libary.configuration.Constants.PROP_CONFIGURATION_BASE;

public class Main {
    public static void main(String[] args) throws Exception {
//        LinkedHashMap locators = YamlUtils.readAllYamlInDirectory("D:/Code/test-automation-fwk/libraries/utilities/src/main/resources/WebIdentifiers");
//        System.out.println("" + locators);
        System.setProperty(PROP_CONFIGURATION_BASE, "D:/Code/test-automation-fwk/libraries/utilities/src/main/resources/sample.yaml");
//        String outputPath = TemplateUtils.processFileTemplate("D:/Code/test-automation-fwk/libraries/utilities/src/main/resources/template/template_testing.yaml",
//                Collections.singletonMap("env", ConfigurationFactory.getInstance()), null);
//        System.out.println(outputPath);
//        Map values = new HashMap<>();
//        values.put("firstName", "Viet");
//        values.put("lastName", "Nguyen");
//        String content = TemplateUtils.processTemplate("Hello ${firstName} ${lastName}!", values);
//        System.out.println(content);
        DownloadHelpers.downloadFile("https://chromedriver.storage.googleapis.com/108.0.5359.71/chromedriver_win32.zip", "D:/Code/test-automation-fwk/libraries/utilities/src/main/drivers");
    }
}