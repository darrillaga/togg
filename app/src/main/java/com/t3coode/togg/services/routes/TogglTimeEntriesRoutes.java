package com.t3coode.togg.services.routes;

public class TogglTimeEntriesRoutes {
    public static final String PATH = "/time_entries";
    public static final String TIME_ENTRY = PATH + "/%s";
    public static final String CURRENT_TIME_ENTRY = PATH + "/current";
    public static final String START = PATH + "/start";
    public static final String STOP = TIME_ENTRY + "/stop";
}
