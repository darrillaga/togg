package com.t3coode.togg.activities.fragments;

import android.support.v4.app.Fragment;

import com.t3coode.ui.AnalyticsUtils;

public class BaseFragment extends Fragment {

    @Override
    public void onStart() {
        AnalyticsUtils.fragmentStart(this);
        super.onStart();
    }

}
