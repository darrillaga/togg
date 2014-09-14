package com.t3coode.togg.activities.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.t3coode.togg.activities.widgets.ToggWidgetProvider;

public class NotificationsStopBroadcastReceiver extends BroadcastReceiver {

    public static final String STOP_BROADCAST_RECEIVER_KEY = "com.t3coode.activities.services.NotificationsStopBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                TimeNotificationService.class.getName());

        intent.putExtra(TimeNotificationService.NOTIFICATION_STOP_ACTION_KEY,
                true);

        Intent actionIntent = new Intent(context, ToggWidgetProvider.class);
        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect
        // of
        // broadcasting TOAST_ACTION.
        actionIntent.setAction(STOP_BROADCAST_RECEIVER_KEY);
        actionIntent.setData(Uri.parse(actionIntent
                .toUri(Intent.URI_INTENT_SCHEME)));

        context.sendBroadcast(actionIntent);

        context.startService(intent.setComponent(comp));
        // setResultCode(Activity.RESULT_OK);

    }
}
