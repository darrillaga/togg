package com.t3coode.togg.activities.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RemoteTimeEntryBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                TimeTrackingService.BROADCAST_TIME_ENTRY_CREATE)
                || intent.getAction().equals(
                        TimeTrackingService.BROADCAST_TIME_ENTRY_UPDATE)) {

            Intent i = new Intent(context, TimeNotificationService.class);
            if (context.startService(i) == null) {
                i.putExtras(intent);
                context.startService(i);
            }
        }
    }
}
