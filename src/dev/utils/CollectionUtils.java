package dev.utils;

import java.util.List;

public final class CollectionUtils {

    public static int getNextTaskId(List<Integer> list) {
        int key = 0;
        while (list.contains(key)) {
            key++;
        }
        return key;
    }
}
