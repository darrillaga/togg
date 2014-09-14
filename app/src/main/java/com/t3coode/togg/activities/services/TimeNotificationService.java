package com.t3coode.togg.activities.services;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.SplashScreenActivity;
import com.t3coode.togg.activities.async.AsyncLoaderTask;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;
import com.t3coode.togg.activities.async.LoaderTask;
import com.t3coode.togg.activities.services.TimeEntryUpdateBroadcastReceiver.ReceiverListener;
import com.t3coode.togg.activities.services.TimeTrackingService.TimeTrackingBinder;
import com.t3coode.togg.activities.utils.DateTimeFormatter;
import com.t3coode.togg.activities.utils.ProjectColor;
import com.t3coode.togg.activities.utils.Resources;
import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.services.utils.NullAwareBeanUtils;
import com.t3coode.togg.tracking.TimeTrackerController;
import com.t3coode.togg.tracking.TimeTrackerController.OnTimeUpdateListener;

public class TimeNotificationService extends Service implements
        OnTimeUpdateListener, ReceiverListener,
        TimeEntryCreateBroadcastReceiver.ReceiverListener,
        LoaderTask<TimeEntryDTO> {

    public static final String CURRENT_TIME_ENTRY_KEY = "com.t3coode.togg.activities.fragments.currentTimeEntryKey";
    public static final String NOTIFICATION_STOP_ACTION_KEY = "NotificationStopActionKey";

    private static final String TAG = TimeNotificationService.class.getName();
    private static final int NOTIFICATION_ID = 234123;

    private TimeEntryDTO mCurrentTimeEntry;
    private ProjectDTO mCurrentProject;
    private Bitmap mCurrentProjectIcon;
    private TimeTrackerController mTimeTrackerController;

    private TimeEntryUpdateBroadcastReceiver mTimeEntryUpdateReceiver;
    private TimeEntryCreateBroadcastReceiver mTimeEntryCreateReceiver;

    private TimeTrackingService mTimeTrackingService;
    private boolean mTimeTrackingServiceBound;
    private NotificationManager mNotificationManager;
    private boolean mIsNotificationShown;

    private ServiceConnection mTimeTrackingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            TimeNotificationService.this.mTimeTrackingService = null;
            TimeNotificationService.this.mTimeTrackingServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimeNotificationService.this.mTimeTrackingService = ((TimeTrackingBinder) service)
                    .getService();
            TimeNotificationService.this.mTimeTrackingServiceBound = true;
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mCurrentTimeEntry == null) {
            initializeTimeEntry();
        } else {
            if (intent.hasExtra(NOTIFICATION_STOP_ACTION_KEY)) {
                stopTracking();
            }
        }
        return START_STICKY;
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initialize();
        initializeTimeEntry();
        onResume();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TimeNotificationBinder(this);
    }

    public static class TimeNotificationBinder extends Binder {

        private final TimeNotificationService service;

        public TimeNotificationBinder(TimeNotificationService service) {
            this.service = service;
        }

        public TimeNotificationService getService() {
            return service;
        }
    }

    @Override
    public void onDestroy() {
        onPause();
        mNotificationManager.cancel(NOTIFICATION_ID);
        if (mTimeTrackingServiceBound) {
            unbindService(mTimeTrackingServiceConnection);
        }

        super.onDestroy();
    }

    public void initialize() {

        this.mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        this.mTimeEntryUpdateReceiver = new TimeEntryUpdateBroadcastReceiver(
                this);
        this.mTimeEntryCreateReceiver = new TimeEntryCreateBroadcastReceiver(
                this);

        Intent i = new Intent(this, TimeTrackingService.class);
        if (!mTimeTrackingServiceBound) {
            bindService(i, mTimeTrackingServiceConnection,
                    Service.BIND_AUTO_CREATE);
        }
    }

    private void initializeTimeTrackerController() {
        if (mTimeTrackerController == null) {
            this.mTimeTrackerController = new TimeTrackerController(this);
        } else {
            mTimeTrackerController.stop();
            mTimeTrackerController.recycle();
            this.mTimeTrackerController = new TimeTrackerController(this);
        }
    }

    public void initializeTimeEntry() {
        initializeTimeTrackerController();
        loadCurerntTimeEntry();
    }

    public void onResume() {
        if (mCurrentTimeEntry != null && mCurrentTimeEntry.getDuration() < 0) {
            startTrackingController();
        }

        IntentFilter intentFilter = new IntentFilter(
                TimeTrackingService.BROADCAST_TIME_ENTRY_UPDATE);
        registerReceiver(mTimeEntryUpdateReceiver, intentFilter);

        intentFilter = new IntentFilter(
                TimeTrackingService.BROADCAST_TIME_ENTRY_CREATE);
        registerReceiver(mTimeEntryCreateReceiver, intentFilter);
    }

    public void onPause() {
        cleanTimeTrackerController();

        unregisterReceiver(mTimeEntryUpdateReceiver);
        unregisterReceiver(mTimeEntryCreateReceiver);

    }

    private Bitmap createProjectIcon() {

        Bitmap b;

        if (mCurrentProject != null) {

            View projectIconWrapper = LayoutInflater.from(this).inflate(
                    R.layout.layout_project_icon, null, false);

            View projectIconView = projectIconWrapper
                    .findViewById(R.id.project_color);

            TextView projectLetterTV = (TextView) projectIconView
                    .findViewById(R.id.project_letter);

            projectLetterTV.setText(mCurrentProject.getName().substring(0, 1)
                    .toUpperCase());

            projectIconView.setBackgroundColor(ProjectColor.getColor(
                    mCurrentProject, Resources.loadResrourceColorTypedArray(
                            this, R.array.project_colors, R.color.soft_grey)));

            if (projectIconWrapper.getMeasuredHeight() <= 0) {
                projectIconWrapper.measure(MeasureSpec.UNSPECIFIED,
                        MeasureSpec.UNSPECIFIED);
                projectIconWrapper.layout(0, 0,
                        projectIconWrapper.getMeasuredWidth(),
                        projectIconWrapper.getMeasuredHeight());
                b = Bitmap.createBitmap(projectIconWrapper.getMeasuredWidth(),
                        projectIconWrapper.getMeasuredHeight(),
                        Config.ARGB_8888);
            } else {
                projectIconWrapper.layout(projectIconWrapper.getLeft(),
                        projectIconWrapper.getTop(),
                        projectIconWrapper.getRight(),
                        projectIconWrapper.getBottom());

                b = Bitmap.createBitmap(projectIconWrapper.getWidth(),
                        projectIconWrapper.getHeight(), Config.ARGB_8888);
            }

            Canvas c = new Canvas(b);
            projectIconWrapper.draw(c);
        } else {
            b = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.app_icon)).getBitmap();
        }

        return b;
    }

    private void setNotificationData() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(mCurrentProjectIcon)
                .setContentTitle(
                        DateTimeFormatter.periodHourBased(TimeTrackerController
                                .convertIfRunningTime(mCurrentTimeEntry
                                        .getDuration())))
                .setWhen(mCurrentTimeEntry.getStart().getTime())
                .setContentText(
                        StringUtils.isNotEmpty(mCurrentTimeEntry
                                .getDescription()) ? mCurrentTimeEntry
                                .getDescription()
                                : getString(R.string.no_description))
                .setOngoing(true).setAutoCancel(false);

        Intent notificationIntent = new Intent(this,
                NotificationsStopBroadcastReceiver.class);
        final PendingIntent stopIntent = PendingIntent.getBroadcast(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.addAction(R.drawable.ic_action_stop,
                    getString(R.string.stop), stopIntent);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashScreenActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        Notification notification = mBuilder.build();

        if (mIsNotificationShown) {
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        this.mIsNotificationShown = true;
    }

    private synchronized void startTrackingController() {
        if (mCurrentTimeEntry != null) {

            setCurrentProject(ProjectManager.getInstance().get(
                    mCurrentTimeEntry.getProjectId()));

            setNotificationData();

            initializeTimeTrackerController();
            mTimeTrackerController.start(mCurrentTimeEntry);
        }

    }

    private void setCurrentProject(ProjectDTO project) {
        this.mCurrentProject = project;
        this.mCurrentProjectIcon = createProjectIcon();
    }

    private synchronized Long stopTrackingController() {
        return cleanTimeTrackerController();
    }

    private Long cleanTimeTrackerController() {
        if (mTimeTrackerController != null) {
            Long time = mTimeTrackerController.stop();
            mTimeTrackerController.recycle();
            this.mTimeTrackerController = null;
            return time;
        }

        return null;
    }

    @Override
    public void onTimeUpdated(TimeEntryDTO timeEntry, final long runningTime) {
        setNotificationData();
    }

    @Override
    public boolean isListenToTimeEntry(long id) {
        boolean listen = this.mCurrentTimeEntry == null
                || (mCurrentTimeEntry.isPersisted() && mCurrentTimeEntry
                        .getId() == id);
        return listen;
    }

    @Override
    public void onUpdateTimeEntry(TimeEntryDTO timeEntry) {
        try {
            if (mCurrentTimeEntry == null) {
                this.mCurrentTimeEntry = new TimeEntryDTO();
            }

            NullAwareBeanUtils.getInstance().copyProperties(mCurrentTimeEntry,
                    timeEntry);

            if (mCurrentTimeEntry.getDuration() >= 0) {
                removeNotification();
            } else {
                startTrackingController();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateTimeEntry(TimeEntryDTO timeEntry) {
        if (timeEntry.getDuration() < 0) {
            stopTrackingController();

            setCurrentTimeEntry(timeEntry);
            startTrackingController();
        } else {
            removeNotification();
        }
    }

    private void setCurrentTimeEntry(TimeEntryDTO timeEntry) {
        this.mCurrentTimeEntry = timeEntry;
        if (timeEntry != null) {
            setCurrentProject(ProjectManager.getInstance().get(
                    timeEntry.getProjectId()));
        }
    }

    private void stopTracking() {
        removeNotification();

        mCurrentTimeEntry.setStop(new Date());

        mCurrentTimeEntry
                .setDuration(TimeTrackerController
                        .convertIfRunningTime(-(mCurrentTimeEntry.getStart()
                                .getTime() / 1000)));

        if (mTimeTrackingServiceBound) {

            updateCurrentTimeEntry();
        }

    }

    private void removeNotification() {
        stopTrackingController();
        stopForeground(true);
        this.mIsNotificationShown = false;
    }

    @SuppressWarnings("unchecked")
    private void loadCurerntTimeEntry() {
        new AsyncLoaderTask<TimeEntryDTO>().execute(this);
    }

    private void updateCurrentTimeEntry() {
        if (mTimeTrackingServiceBound) {
            mTimeTrackingService.updateTimeEntry(mCurrentTimeEntry,
                    TimeTrackingService.ACTION_UPDATE);
        }
    }

    @Override
    public void onPostExecute(GenericLoaderResponse<TimeEntryDTO> result,
            Map<String, Object> params) {
        TimeEntryDTO timer = result.getData();

        if (result.success() && timer != null) {
            if (mCurrentTimeEntry == null
                    || !timer.getId().equals(mCurrentTimeEntry.getId())
                    || !timer.getAt().equals(mCurrentTimeEntry.getAt())
                    && timer.getDuration() < 0) {
                stopTrackingController();
                setCurrentTimeEntry(timer);
                startTrackingController();

            }
        } else {
            removeNotification();
        }
    }

    @Override
    public TimeEntryDTO onExecute(Map<String, Object> params) throws Exception {
        TimeEntryDTO timeEntry = ToggApp.getApplication().getTogglServices()
                .manageTimeEntries().current();
        return timeEntry;
    }

    @Override
    public boolean awaitingTaskResult(Map<String, Object> params) {
        return true;
    }

}
