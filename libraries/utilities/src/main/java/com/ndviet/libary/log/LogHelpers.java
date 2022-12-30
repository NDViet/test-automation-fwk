package com.ndviet.libary.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class LogHelpers {
    private static final Logger LOGGER = LogManager.getLogger(LogHelpers.class);

    public static void prettyPrintObjectDetails(Object object) {
        if (object instanceof Map) {
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                LOGGER.info(key.toString() + ": " + map.get(key).toString() + "\n---");
            }
        } else if (object instanceof List) {
            List list = (List) object;
            for (Object item : list) {
                LOGGER.info(item.toString());
            }
        } else {
            LOGGER.info(object.toString());
        }
    }

}
