package com.t3coode.togg;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private static CacheManager instance;

    private Map<String, Object> resources;

    private CacheManager() {
        resources = new HashMap<String, Object>();
    };

    public Map<String, Object> getResources() {
        return resources;
    }

    public void setResources(Map<String, Object> resources) {
        this.resources = resources;
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

}
