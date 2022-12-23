package com.ndviet.libary.map;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MapUtils {

    private static List<String> getSegments(String segmentsPath) {
        return Arrays.asList(segmentsPath.split("\\."));
    }

    public static String getValueAsString(LinkedHashMap map, String segmentsPath) {
        List<String> segments = getSegments(segmentsPath);
        Object value = getValueAsObject(map, segments.listIterator());
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public static Object getValueAsObject(Object obj, Iterator<String> segments) {
        if (segments.hasNext()) {
            String segment = segments.next();
            if (obj instanceof LinkedHashMap) {
                Object currentObj = ((LinkedHashMap) obj).get(segment);
                if (currentObj != null) {
                    if (segments.hasNext()) {
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
