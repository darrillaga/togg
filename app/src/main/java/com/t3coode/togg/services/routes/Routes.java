package com.t3coode.togg.services.routes;

import java.net.URI;

import org.apache.commons.lang.StringUtils;

import android.net.Uri;

import com.sun.jersey.api.client.ClientResponse;

public class Routes {
    private static Routes instance;
    private String basePath;

    private Routes() {
        super();
    }

    public String getRoute(String resourcePath) {
        return basePath + resourcePath;
    }

    public String getRoute(String resourcePath, Object... args) {
        String route = getWithArguments(resourcePath, args);

        return basePath + route;
    }

    public static String getWithArguments(String resourcePath, Object... args) {
        Object params[] = new Object[args.length];
        for (int index = 0; index < args.length; index++) {
            params[index] = Uri.encode(args[index].toString());
        }

        return String.format(resourcePath, params);
    }

    public static String getPathAndQuery(URI url) {
        String strUrl = url.getPath();
        if (StringUtils.isNotBlank(url.getQuery())) {
            strUrl += "?" + url.getQuery();
        }
        return strUrl;
    }

    public static String getMethod(ClientResponse response) {
        return response.toString().split(" ")[0];
    }

    public static String getHostWithProtocol(URI url) {
        return url.getScheme() + "://" + url.getHost();
    }

    public static void build(String basePath) {
        if (instance == null) {
            synchronized (Routes.class) {
                if (instance == null) {
                    instance = new Routes();
                    instance.basePath = basePath;
                }
            }
        }
    }

    public static Routes getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Could not instantiate class. Execute build operation first.");
        }
        return instance;
    }

}