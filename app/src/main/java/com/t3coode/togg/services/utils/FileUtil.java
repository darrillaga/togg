package com.t3coode.togg.services.utils;

public class FileUtil {

    public static String formattedSize(Long size) {
        String resultSize = size.toString() + " B";

        if (size >= 1000 && size < 1000000) {
            resultSize = ((Long) (size / 1000)).toString() + " KB";
        } else if (size > 1000000000) {
            resultSize = ((Long) (size / 1000000)).toString() + " MB";
        }

        return resultSize;
    }
}
