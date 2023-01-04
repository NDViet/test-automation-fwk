package com.ndviet.library.list;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.ndviet.library.math.MathHelpers.compareNumber;
import static com.ndviet.library.math.MathHelpers.isCreatable;

public class ListHelpers {
    private static final Logger LOGGER = LogManager.getLogger(ListHelpers.class);

    public static Object getLastElement(List list, boolean reverse) {
        LOGGER.info("List contains these element: " + list.toString());
        if (reverse) {
            Collections.reverse(list);
        }
        return list.get(list.size() - 1);
    }

    public static <V extends Comparable> boolean isSorted(List<V> list, boolean reverse) {
        LOGGER.info("List contains these element: " + list.toString());
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        Iterator<V> iterator;
        if (reverse) {
            Collections.reverse(list);
        }
        iterator = list.iterator();
        V current, previous = iterator.next();
        while (iterator.hasNext()) {
            current = iterator.next();
            if (isCreatable(previous.toString()) && isCreatable(current.toString())) {
                if (compareNumber(previous.toString(), current.toString()) > 0) {
                    LOGGER.error("Unsorted pair: " + previous + " - " + current);
                    return false;
                }
            } else {
                if (previous.compareTo(current) > 0)
                    return false;
            }
            previous = current;
        }
        return true;
    }
}
