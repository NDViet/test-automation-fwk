package com.ndviet.library.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import static com.ndviet.library.configuration.Constants.LOCALE_LANGUAGE_TAG;

public class ConfigurationHelpers {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationHelpers.class);

    public static Locale getSystemLocale() {
        String localeLanguageTag = ConfigurationFactory.getInstance().getValue(LOCALE_LANGUAGE_TAG);
        localeLanguageTag = (localeLanguageTag == null) ? "en-US" : localeLanguageTag;
        String[] components = localeLanguageTag.split("-");
        Locale locale;
        try {
            locale = new Locale(components[0], components[1]);
        } catch (Exception e) {
            LOGGER.error("Could not create Locale instance. Kindly check the Language Tag");
            throw e;
        }
        return locale;
    }
}
