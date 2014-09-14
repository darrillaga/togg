package com.t3coode.togg.activities;

import android.view.View;

public interface TimerContainer {
    void onEditMode(View v);

    // void setCurrentTimer(Object trigger, TimeEntryDTO timeEntry);

    void registerTimerChanged(TimerObserver observer);

    void unregisterTimerChanged(TimerObserver observer);

    public static interface TimerObserver {
        void onTimerChanged(Object trigger);
    }

    void notifyTimerChanged(Object trigger);
}