package com.t3coode.ui;

import android.support.v7.app.ActionBarActivity;

import com.google.analytics.tracking.android.EasyTracker;

public class AnalyticsActivity extends ActionBarActivity {

    @Override
    protected void onStart() {
        EasyTracker.getInstance(this).activityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }

}
