package com.t3coode.togg.activities.utils;

import java.util.List;

import android.widget.TextView;

public class TextViewUtils {

    public static boolean isTruncatedDescription(TextView containerTv) {
        return truncateDescriptionLength(containerTv) > 0;
    }

    private static int truncateDescriptionLength(TextView containerTv) {
        if (containerTv.getLayout() != null) {
            return containerTv.getLayout().getEllipsisCount(1);
        }
        return 0;
    }

    public static String joinWithCommas(List<Object> objects) {
        StringBuilder str = new StringBuilder();
        for (Object object : objects) {
            str.append(object.toString());
            str.append(", ");
        }
        if (str.length() > 0) {
            str.delete(str.length() - 2, str.length());
        }
        return str.toString();
    }
}
