package com.t3coode.togg.activities.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.t3coode.togg.services.dtos.TimeEntryDTO;

public class TimeEntryUpdateBroadcastReceiver extends BroadcastReceiver {

    private ReceiverListener mListener;

    public TimeEntryUpdateBroadcastReceiver(ReceiverListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                TimeTrackingService.BROADCAST_TIME_ENTRY_UPDATE)) {

            TimeEntryDTO timeEntry = new TimeEntryDTO();
            timeEntry
                    .loadFromJSON(intent
                            .getStringExtra(TimeTrackingService.BROADCAST_TIME_ENTRY_JSON));

            if (mListener != null && timeEntry.getId() != null
                    && mListener.isListenToTimeEntry(timeEntry.getId())) {
                mListener.onUpdateTimeEntry(timeEntry);
            }
        }
    }

    public static interface ReceiverListener {
        boolean isListenToTimeEntry(long id);

        void onUpdateTimeEntry(TimeEntryDTO timeEntry);
    }
}
