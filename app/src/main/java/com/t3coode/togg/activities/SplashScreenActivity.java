package com.t3coode.togg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.t3coode.togg.ToggApp;
import com.t3coode.ui.AnalyticsActivity;

public class SplashScreenActivity extends AnalyticsActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle arg0) {
        Crashlytics.start(this);
        super.onCreate(arg0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAndGoToHome();
            }
        }, SPLASH_DISPLAY_LENGTH);

        super.onPostCreate(savedInstanceState);
    }

    public void finishAndGoToHome() {
        Intent intent = null;

        if (ToggApp.getApplication().userIsLoggedIn()) {
            intent = new Intent(this, TimeEntriesActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
