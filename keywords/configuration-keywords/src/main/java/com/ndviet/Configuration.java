package com.ndviet;

import com.ndviet.configuration.ConfigurationFactory;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.library.AnnotationLibrary;

import java.util.Arrays;

@RobotKeywords
public class Configuration extends AnnotationLibrary {
    public Configuration() {
        super(Arrays.asList("com/ndviet/Configuration.class"));
    }

    @RobotKeyword
    public String getValue(String key) throws Exception {
        return ConfigurationFactory.getInstance().getValue(key);
    }
}
