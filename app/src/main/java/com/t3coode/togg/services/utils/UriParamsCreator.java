package com.t3coode.togg.services.utils;

import java.util.Iterator;
import java.util.Map;

import android.net.Uri;

public class UriParamsCreator {

    public static String BEGINNING_TOKEN = "?";
    public static String ASSIGNMENT_TOKEN = "=";
    public static String CONCAT_TOKEN = "&";

    public static String create(Map<String, String> params) {
        return create(params, true);
    }

    public static String create(Map<String, String> params,
            boolean fromTheBeginning) {
        StringBuilder uriParams = new StringBuilder();

        Iterator<String> it = params.keySet().iterator();

        if (fromTheBeginning) {
            uriParams.append(BEGINNING_TOKEN);
        } else {
            uriParams.append(CONCAT_TOKEN);
        }

        while (it.hasNext()) {
            String key = it.next();
            uriParams.append(key).append(ASSIGNMENT_TOKEN)
                    .append(Uri.encode(params.get(key))).append(CONCAT_TOKEN);
        }

        uriParams.deleteCharAt(uriParams.length() - 1);

        return uriParams.toString();
    }
}
