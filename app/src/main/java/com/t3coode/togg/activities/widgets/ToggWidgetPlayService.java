package com.t3coode.togg.activities.widgets;

import java.util.Date;

import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.activities.services.TimeTrackingService.TimeTrackingBinder;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.tracking.TimeTrackerController;

public class ToggWidgetPlayService extends IntentService {

    public static final String PLAYED_TIME_ENTRY_ID_KEY = "com.t3coode.togg.activities.widgets.TogglWidgetProvider.currentTimeEntryIdKey";

    private ServiceConnection mTimeTrackingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimeEntryDTO timeEntry = new TimeEntryDTO();
            timeEntry.setStart(new Date());
            timeEntry.setDuration(TimeTrackerController.getRunningTimeStart(0));

            storePlayedTimeEntryId(timeEntry.getStart().getTime());
            timeEntry.setGuid(String.valueOf(timeEntry.getStart().getTime()));

            ((TimeTrackingBinder) service).getService().updateTimeEntry(
                    timeEntry, TimeTrackingService.ACTION_UPDATE);

            unbindService(mTimeTrackingServiceConnection);

        }
    };

    public ToggWidgetPlayService() {
        super("ToggWidgetPlayService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent i = new Intent(this, TimeTrackingService.class);
        bindService(i, mTimeTrackingServiceConnection, Service.BIND_AUTO_CREATE);
    }

    public static void storePlayedTimeEntryId(Long id) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(
                ToggApp.getApplication()).edit();
        if (id == null) {
            editor.remove(PLAYED_TIME_ENTRY_ID_KEY);
        } else {
            editor.putLong(PLAYED_TIME_ENTRY_ID_KEY, id);
        }
        editor.commit();
    }

}
