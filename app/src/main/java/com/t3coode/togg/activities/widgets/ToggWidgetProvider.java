package com.t3coode.togg.activities.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.services.NotificationsStopBroadcastReceiver;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.services.dtos.TimeEntryDTO;

public class ToggWidgetProvider extends AppWidgetProvider {

    public static final String PLAY_ACTION = "com.t3coode.togg.activities.widgets.TogglWidgetProvider.playAction";
    public static final String STOP_ACTION = "com.t3coode.togg.activities.widgets.TogglWidgetProvider.stopAction";
    public static final String NO_RUNNING_ACTION = "com.t3coode.togg.activities.widgets.TogglWidgetProvider.noRunningAction";
    public static final String STATUS_ACTION = "com.t3coode.togg.activities.widgets.TogglWidgetProvider.statusAction";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateWidget(context, appWidgetManager, appWidgetId,
                    R.layout.stopped_widget);
        }

        context.startService(new Intent(context, ToggWidgetStatusService.class));
    }

    private void updateWidget(Context context,
            AppWidgetManager appWidgetManager, int appWidgetId, int viewId) {

        RemoteViews rv = null;

        switch (viewId) {
        case R.layout.stopping_widget:
            rv = createStoppingWidget(context, appWidgetId);
            break;
        case R.layout.starting_widget:
            rv = createStartingWidget(context, appWidgetId);
            break;
        case R.layout.running_widget:
            rv = createRunningWidget(context, appWidgetId);
            break;
        default:
            rv = createSoppedWidget(context, appWidgetId);
            break;
        }

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private RemoteViews createSoppedWidget(Context context, int appWidgetId) {
        // // Sets up the intent that points to the StackViewService that
        // will
        // // provide the views for this collection.
        // Intent intent = new Intent(context, ToggWidgetProvider.class);
        // intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
        // appWidgetIds[i]);
        // // When intents are compared, the extras are ignored, so we need
        // to embed the extras
        // // into the data so that the extras will not be ignored.
        // intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.stopped_widget);
        // rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
        //
        // // The empty view is displayed when the collection has no items.
        // It should be a sibling
        // // of the collection view.
        // rv.setEmptyView(R.id.stack_view, R.id.empty_view);

        // This section makes it possible for items to have individualized
        // behavior.
        // It does this by setting up a pending intent template. Individuals
        // items of a collection
        // cannot set up their own pending intents. Instead, the collection
        // as a whole sets
        // up a pending intent template, and the individual items set a
        // fillInIntent
        // to create unique behavior on an item-by-item basis.
        Intent actionIntent = new Intent(context, ToggWidgetProvider.class);
        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect
        // of
        // broadcasting TOAST_ACTION.
        actionIntent.setAction(ToggWidgetProvider.PLAY_ACTION);
        actionIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        actionIntent.setData(Uri.parse(actionIntent
                .toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,
                0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.action, actionPendingIntent);

        return rv;
    }

    private RemoteViews createStartingWidget(Context context, int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.starting_widget);

        return rv;
    }

    private RemoteViews createRunningWidget(Context context, int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.running_widget);

        Intent actionIntent = new Intent(context, ToggWidgetProvider.class);
        actionIntent.setAction(ToggWidgetProvider.STOP_ACTION);
        actionIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        actionIntent.setData(Uri.parse(actionIntent
                .toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,
                0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.action, actionPendingIntent);

        return rv;
    }

    private RemoteViews createStoppingWidget(Context context, int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.stopping_widget);

        return rv;
    }

    private void updateWidgetView(Context context, TimeEntryDTO timeEntry) {
        int[] ids = getWidgetIds(context);

        for (int id : ids) {
            if (timeEntry.getDuration() < 0) {
                updateWidget(context, AppWidgetManager.getInstance(context),
                        id, R.layout.running_widget);
            } else {
                updateWidget(context, AppWidgetManager.getInstance(context),
                        id, R.layout.stopped_widget);
            }
        }
    }

    private void onWidgetPlayClick(Context context, int appWidgetId) {
        updateWidget(context, AppWidgetManager.getInstance(context),
                appWidgetId, R.layout.starting_widget);
        context.startService(new Intent(context, ToggWidgetPlayService.class));
    }

    private void onWidgetStopClick(Context context, int appWidgetId) {
        updateWidget(context, AppWidgetManager.getInstance(context),
                appWidgetId, R.layout.stopping_widget);
        context.startService(new Intent(context, ToggWidgetStopService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if ((intent.getAction()
                .equals(TimeTrackingService.BROADCAST_TIME_ENTRY_UPDATE))
                || intent.getAction().equals(
                        TimeTrackingService.BROADCAST_TIME_ENTRY_CREATE)) {
            TimeEntryDTO timeEntry = new TimeEntryDTO();
            timeEntry
                    .loadFromJSON(intent
                            .getStringExtra(TimeTrackingService.BROADCAST_TIME_ENTRY_JSON));
            updateWidgetView(context, timeEntry);
            ToggWidgetPlayService.storePlayedTimeEntryId(null);
            ToggWidgetStatusService.storeStatusTimeEntryId(null);

        } else if (intent.getAction().equals(PLAY_ACTION)) {
            onWidgetPlayClick(context,
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0));
        } else if (intent.getAction().equals(STOP_ACTION)) {
            onWidgetStopClick(context,
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0));
        } else if (intent.getAction().equals(STATUS_ACTION)
                && getStatusTimeEntryId() != null) {

            TimeEntryDTO timeEntry = new TimeEntryDTO();
            timeEntry
                    .loadFromJSON(intent
                            .getStringExtra(TimeTrackingService.BROADCAST_TIME_ENTRY_JSON));
            updateWidgetView(context, timeEntry);

        } else if (intent.getAction().equals(
                NotificationsStopBroadcastReceiver.STOP_BROADCAST_RECEIVER_KEY)) {
            for (int id : getWidgetIds(context)) {
                updateWidget(context, AppWidgetManager.getInstance(context),
                        id, R.layout.stopped_widget);
            }

        } else if (intent.getAction().equals(NO_RUNNING_ACTION)) {
            for (int id : getWidgetIds(context)) {
                updateWidget(context, AppWidgetManager.getInstance(context),
                        id, R.layout.stopped_widget);
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    public int[] getWidgetIds(Context context) {
        ComponentName name = new ComponentName(context, getClass());
        return AppWidgetManager.getInstance(context).getAppWidgetIds(name);
    }

    public Long getStatusTimeEntryId() {
        long id = PreferenceManager.getDefaultSharedPreferences(
                ToggApp.getApplication()).getLong(
                ToggWidgetStatusService.STATUS_TIME_ENTRY_ID_KEY, -1);
        return id != -1 ? id : null;
    }

}
