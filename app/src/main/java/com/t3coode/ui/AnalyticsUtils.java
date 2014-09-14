package com.t3coode.ui;

import android.support.v4.app.Fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.t3coode.togg.ToggApp;

public class AnalyticsUtils {

    public static final String ANALYTICS_ACTIVITY_NAME_PARAM = "&cd";

    public static void fragmentStart(Fragment fragment) {
        EasyTracker
                .getInstance(ToggApp.getApplication())
                .send(MapBuilder
                        .createAppView()
                        .set(ANALYTICS_ACTIVITY_NAME_PARAM,
                                fragment.getClass().getCanonicalName()).build());
    }
}
