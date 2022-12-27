package com.ndviet.libary.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public static Object getValueAsObject(Object obj, String segmentsPath) {
        List<String> segments = getSegments(segmentsPath);
        return getValueAsObject(obj, segments.listIterator());
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

    public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map, boolean reverse) {
        List<Map.Entry<K, V>> entries = new LinkedList<>(map.entrySet());
        Collections.sort(entries, (o1, o2) -> {
            if (!reverse) {
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
