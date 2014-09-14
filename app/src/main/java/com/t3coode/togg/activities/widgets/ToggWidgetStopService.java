package com.t3coode.togg.activities.widgets;

import java.util.Date;

import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.activities.services.TimeTrackingService.TimeTrackingBinder;
import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.tracking.TimeTrackerController;

public class ToggWidgetStopService extends IntentService {

    private TimeTrackingService mService;
    private Object token = new Object();

    private ServiceConnection mTimeTrackingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ToggWidgetStopService.this.mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            ToggWidgetStopService.this.mService = ((TimeTrackingBinder) service)
                    .getService();
            synchronized (token) {
                token.notify();
            }
        }
    };

    public ToggWidgetStopService() {
        super("ToggWidgetStopService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent i = new Intent(this, TimeTrackingService.class);
        bindService(i, mTimeTrackingServiceConnection, Service.BIND_AUTO_CREATE);
        try {
            synchronized (token) {
                if (mService == null) {
                    token.wait();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mService != null) {
            TimeEntryDTO mTimeEntry;
            try {
                mTimeEntry = ToggApp.getApplication().getTogglServices()
                        .manageTimeEntries().current();

                if (mTimeEntry != null) {
                    mTimeEntry.setStop(new Date());

                    mTimeEntry.setDuration(TimeTrackerController
                            .convertIfRunningTime(-(mTimeEntry.getStart()
                                    .getTime() / 1000)));

                    mService.updateTimeEntry(mTimeEntry,
                            TimeTrackingService.ACTION_UPDATE);
                } else {
                    sendBroadcast(new Intent(
                            ToggWidgetProvider.NO_RUNNING_ACTION));
                }

            } catch (TogglApiResponseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            unbindService(mTimeTrackingServiceConnection);
        }

    }

}
