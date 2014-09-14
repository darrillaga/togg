package com.t3coode.togg.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.t3coode.togg.R;
import com.t3coode.togg.activities.fragments.LateralMenuFragment;
import com.t3coode.togg.activities.fragments.TimeEntriesListFragment;
import com.t3coode.togg.activities.fragments.TimerEditFragment;
import com.t3coode.togg.activities.fragments.TimerFragment;
import com.t3coode.ui.DrawerLayoutActivity;

public class TimeEntriesActivity extends DrawerLayoutActivity implements
        TimerContainer {

    private List<TimerObserver> mTimerOservers = new ArrayList<TimerObserver>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setCustomView(R.layout.layout_togg_action_bar);
        setContentView(R.layout.activity_time_entries);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        configUpActionBar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.timer_container, new TimerFragment()).commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.left_drawer, new LateralMenuFragment()).commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container,
                        new TimeEntriesListFragment()).commit();

        notifyTimerChanged(this);
    }

    private void configUpActionBar() {
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onEditMode(View v) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.timer_container, new TimerEditFragment())
                .commit();
    }

    @Override
    public void registerTimerChanged(TimerObserver observer) {
        mTimerOservers.add(observer);
    }

    @Override
    public void unregisterTimerChanged(TimerObserver observer) {
        mTimerOservers.remove(observer);
    }

    @Override
    public void notifyTimerChanged(Object trigger) {
        for (TimerObserver timerObserver : mTimerOservers) {
            timerObserver.onTimerChanged(trigger);
        }
    }
}
