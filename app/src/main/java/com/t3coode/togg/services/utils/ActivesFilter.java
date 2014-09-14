package com.t3coode.togg.services.utils;

public enum ActivesFilter {
    TRUE("true"),
    FALSE("false"),
    BOTH("both");

    public static final String KEY = "active";
    public static final ActivesFilter DEFAULT = TRUE;
    public final String value;

    ActivesFilter(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
