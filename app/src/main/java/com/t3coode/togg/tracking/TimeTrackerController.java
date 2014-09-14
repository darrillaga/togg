package com.t3coode.togg.tracking;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.t3coode.togg.services.dtos.TimeEntryDTO;

public class TimeTrackerController {

    private OnTimeUpdateListener mListener;
    private TimeEntryDTO mCurrentTimeEntry;
    private TimerTask mCurrentTimerTask;
    private long mCurrentRunningTime;
    private Timer mTimer;

    public TimeTrackerController(OnTimeUpdateListener listener) {
        this.mListener = listener;
        initTimer();
    }

    public void initTimer() {
        if (this.mTimer == null) {
            this.mTimer = new Timer();
        }
    }

    public long start(final TimeEntryDTO timeEntry) {
        stop();
        this.mCurrentTimeEntry = timeEntry;
        this.mCurrentRunningTime = convertIfRunningTime(timeEntry.getDuration());
        this.mCurrentTimerTask = new TimerTask() {
            @Override
            public void run() {
                timeUpdate(timeEntry, ++mCurrentRunningTime);
            }
        };

        mTimer.schedule(mCurrentTimerTask, new Date(
                System.currentTimeMillis() + 1000), 1000);

        return mCurrentRunningTime;
    }

    public Long stop() {
        Long timeElapsed = null;

        if (this.mCurrentTimeEntry != null) {
            this.mCurrentTimerTask.cancel();
            mTimer.purge();
            timeElapsed = mCurrentRunningTime;
            mCurrentRunningTime = 0;
            this.mCurrentTimerTask = null;
            this.mCurrentTimeEntry = null;
        }

        return timeElapsed;
    }

    protected void timeUpdate(TimeEntryDTO timeEntry, long runningTime) {
        mListener.onTimeUpdated(timeEntry, runningTime);
    }

    public static interface OnTimeUpdateListener {
        void onTimeUpdated(TimeEntryDTO timeEntry, long runningTime);
    }

    public void recycle() {
        if (mCurrentTimerTask != null) {
            this.mCurrentTimerTask.cancel();
        }
        this.mTimer.cancel();
    }

    /*
     * neg. value provided means "running time's start" in seconds since epoch *
     * -1 this function gives back seconds since "running time's start"
     */
    public static long convertIfRunningTime(long time) {
        if (time < 0) {
            long currentTimeSeconds = System.currentTimeMillis() / 1000;
            time = currentTimeSeconds + time;
        }

        if (time < 0) {
            time = 0;
        }
        return time;
    }

    /*
     * Calculates the start of "running time" in seconds since epoch. Makes it a
     * neg. value to distinguish its meaning.
     */
    public static long getRunningTimeStart(long time) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        long trackingStartSinceEpoch = currentTimeSeconds - time;
        return -(trackingStartSinceEpoch);
    }

}
