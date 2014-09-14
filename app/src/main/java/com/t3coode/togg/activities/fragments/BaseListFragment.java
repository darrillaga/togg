package com.t3coode.togg.activities.fragments;

import android.support.v4.app.ListFragment;

import com.t3coode.ui.AnalyticsUtils;

public class BaseListFragment extends ListFragment {

    @Override
    public void onStart() {
        AnalyticsUtils.fragmentStart(this);
        super.onStart();
    }

}
