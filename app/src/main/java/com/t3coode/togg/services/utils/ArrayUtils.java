package com.t3coode.togg.services.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayUtils {

    public static String join(List<String> strings, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iterator = strings.iterator();

        while (iterator.hasNext()) {
            buffer.append(iterator.next());

            if (iterator.hasNext()) {
                buffer.append(delimiter);
            }
        }

        return buffer.toString();
    }

    public static <T> List<T> reverse(List<T> list) {
        if (list != null) {
            List<T> rList = new ArrayList<T>();

            for (int i = list.size() - 1; i >= 0; i--) {
                rList.add(list.get(i));
            }

            return rList;
        } else {
            return null;
        }
    }

}
