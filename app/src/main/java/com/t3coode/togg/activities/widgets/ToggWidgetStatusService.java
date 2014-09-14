package com.t3coode.togg.activities.widgets;

import java.util.Date;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.dtos.TimeEntryDTO;

public class ToggWidgetStatusService extends IntentService {

    public static final String STATUS_TIME_ENTRY_ID_KEY = "com.t3coode.togg.activities.widgets.TogglWidgetProvider.statusTimeEntryIdKey";

    public ToggWidgetStatusService() {
        super("ToggWidgetStatusService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        storeStatusTimeEntryId(new Date().getTime());
        try {
            TimeEntryDTO timeEntry = ToggApp.getApplication()
                    .getTogglServices().manageTimeEntries().current();

            if (timeEntry != null) {
                Intent i = new Intent(ToggWidgetProvider.STATUS_ACTION);
                i.putExtra(TimeTrackingService.BROADCAST_TIME_ENTRY_JSON,
                        timeEntry.toJSON());
                sendBroadcast(i);
            }
        } catch (TogglApiResponseException e) {
            // TODO Auto-generated catch block
            storeStatusTimeEntryId(null);
            e.printStackTrace();
        }

    }

    public static void storeStatusTimeEntryId(Long id) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(
                ToggApp.getApplication()).edit();
        if (id == null) {
            editor.remove(STATUS_TIME_ENTRY_ID_KEY);
        } else {
            editor.putLong(STATUS_TIME_ENTRY_ID_KEY, id);
        }
        editor.commit();
    }

}
