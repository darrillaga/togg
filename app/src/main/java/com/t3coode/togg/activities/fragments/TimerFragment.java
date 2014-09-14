package com.t3coode.togg.activities.fragments;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.TimerContainer;
import com.t3coode.togg.activities.adapters.ProjectsAdapter;
import com.t3coode.togg.activities.adapters.SpinnerWithPromptAdapter;
import com.t3coode.togg.activities.async.GenericLoader.DataLoader;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderCallbacksImpl;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;
import com.t3coode.togg.activities.async.GenericLoader.LoaderCallbacksFinishedListener;
import com.t3coode.togg.activities.services.TimeEntryCreateBroadcastReceiver;
import com.t3coode.togg.activities.services.TimeEntryUpdateBroadcastReceiver;
import com.t3coode.togg.activities.services.TimeEntryUpdateBroadcastReceiver.ReceiverListener;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.activities.services.TimeTrackingService.TimeTrackingBinder;
import com.t3coode.togg.activities.utils.DateTimeFormatter;
import com.t3coode.togg.activities.utils.ProjectColor;
import com.t3coode.togg.activities.utils.Resources;
import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.services.utils.NullAwareBeanUtils;
import com.t3coode.togg.tracking.TimeTrackerController;
import com.t3coode.togg.tracking.TimeTrackerController.OnTimeUpdateListener;

public class TimerFragment extends BaseFragment implements
        OnTimeUpdateListener, ReceiverListener,
        TimeEntryCreateBroadcastReceiver.ReceiverListener,
        LoaderCallbacksFinishedListener<TimeEntryDTO>, DataLoader<TimeEntryDTO> {

    public static final String CURRENT_TIME_ENTRY_KEY = "com.t3coode.togg.activities.fragments.currentTimeEntryKey";

    private TimeEntryDTO mCurrentTimeEntry = new TimeEntryDTO();
    private TimeTrackerController mTimeTrackerController;
    private TimerContainer mTimerContainer;

    private View mTogglTimerBtn;
    private View mProjectColorView;
    private TextView mTimeTV;
    private EditText mDescriptionET;
    private ViewGroup mDetailsWrapper;
    private Spinner mProjectSpinner;
    private TextView mDateTV;
    private TextView mStartTimeTV;
    private TextView mEndTimeTV;
    private ImageButton mMoreButton;
    private ProgressBar mTimerProgressBar;

    private TimeEntryUpdateBroadcastReceiver mTimeEntryUpdateReceiver;
    private TimeEntryCreateBroadcastReceiver mTimeEntryCreateReceiver;

    private boolean mIsTracking;

    private TimeTrackingService mTimeTrackingService;
    private boolean mTimeTrackingServiceBound;
    private List<ProjectDTO> mProjects;

    private long mLastTimerTimestamp = -1;
    private static final int DESCRIPTION_TIMEOUT_DELAY = 1500;

    private Timer mDescriptionTimeoutTimer = new Timer();
    private TimerTask mDescriptionTimeoutTask = null;

    private ServiceConnection mTimeTrackingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            TimerFragment.this.mTimeTrackingService = null;
            TimerFragment.this.mTimeTrackingServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerFragment.this.mTimeTrackingService = ((TimeTrackingBinder) service)
                    .getService();
            TimerFragment.this.mTimeTrackingServiceBound = true;
        }
    };

    @Override
    public void onAttach(Activity activity) {
        this.mTimerContainer = (TimerContainer) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        this.mTimeEntryUpdateReceiver = new TimeEntryUpdateBroadcastReceiver(
                this);
        this.mTimeEntryCreateReceiver = new TimeEntryCreateBroadcastReceiver(
                this);

        Intent i = new Intent(getActivity(), TimeTrackingService.class);
        getActivity().bindService(i, mTimeTrackingServiceConnection,
                Service.BIND_AUTO_CREATE);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        if (mCurrentTimeEntry == null) {
            this.mCurrentTimeEntry = new TimeEntryDTO();
        }

        if (mTimeTrackerController == null) {
            this.mTimeTrackerController = new TimeTrackerController(this);
        }

        this.mTimerProgressBar = (ProgressBar) getView().findViewById(
                R.id.timer_progress_bar);

        this.mTogglTimerBtn = getView().findViewById(R.id.toggl_timer);
        mTogglTimerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onTimerButtonClick(v);
            }

        });

        this.mDescriptionET = (EditText) getView().findViewById(
                R.id.description);

        mDescriptionET.addTextChangedListener(mDescriptionTextWatcher);

        mDescriptionET.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !mDetailsWrapper.isShown()) {
                    toggleTimerDetails();
                }
            }
        });

        this.mTimeTV = (TextView) getView().findViewById(R.id.time);

        this.mMoreButton = (ImageButton) getView().findViewById(
                R.id.more_button);

        mMoreButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleTimerDetails();
            }
        });

        this.mDetailsWrapper = (ViewGroup) getView().findViewById(
                R.id.details_wrapper);

        this.mProjectSpinner = (Spinner) getView().findViewById(R.id.project);

        this.mProjects = ToggApp.getApplication().getCurrentUser()
                .getProjects();
        this.mProjects = mProjects.subList(0, mProjects.size());

        SpinnerWithPromptAdapter adapter = new SpinnerWithPromptAdapter(
                new ProjectsAdapter(getActivity(), mProjects),
                R.layout.layout_projects_list_zero_item,
                R.layout.layout_projects_spiner_list_zero_item, getActivity());

        adapter.setZeroStateAvailable(true);

        mProjectSpinner.setAdapter(adapter);

        mProjectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                    int position, long id) {
                ProjectDTO project = (ProjectDTO) adapter
                        .getItemAtPosition(position);

                Long projectId = null;

                if (project != null) {
                    setProjectColorView(project);
                    projectId = project.getId();
                } else {
                    setEmptyProjectColorView();
                }

                mCurrentTimeEntry.setProjectId(projectId);
                if (mCurrentTimeEntry.isPersisted()) {
                    updateCurrentTimeEntry();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                mProjectColorView.setBackgroundColor(getResources().getColor(
                        R.color.white));

            }
        });

        this.mDateTV = (TextView) getView().findViewById(R.id.date);
        this.mStartTimeTV = (TextView) getView().findViewById(R.id.start_time);
        this.mEndTimeTV = (TextView) getView().findViewById(R.id.end_time);

        this.mProjectColorView = getView().findViewById(R.id.project_color);

        super.onViewCreated(v, savedInstanceState);
    }

    private void toggleTimerDetails() {
        if (mDetailsWrapper.isShown()) {
            mDetailsWrapper.setVisibility(View.GONE);
            mMoreButton.setImageResource(R.drawable.ic_arow_down_min);
        } else {
            mMoreButton.setImageResource(R.drawable.ic_arow_up_min);
            mDetailsWrapper.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        loadCurerntTimeEntry();
        super.onStart();
    }

    @Override
    public void onResume() {
        if (mCurrentTimeEntry != null && mCurrentTimeEntry.getDuration() < 0) {
            startTrackingController();
        }

        IntentFilter intentFilter = new IntentFilter(
                TimeTrackingService.BROADCAST_TIME_ENTRY_UPDATE);
        getActivity().registerReceiver(mTimeEntryUpdateReceiver, intentFilter);

        intentFilter = new IntentFilter(
                TimeTrackingService.BROADCAST_TIME_ENTRY_CREATE);
        getActivity().registerReceiver(mTimeEntryCreateReceiver, intentFilter);

        super.onResume();
    }

    @Override
    public void onPause() {
        mTimeTrackerController.stop();

        getActivity().unregisterReceiver(mTimeEntryUpdateReceiver);
        getActivity().unregisterReceiver(mTimeEntryCreateReceiver);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mTimeTrackerController.recycle();
        // mTimerContainer.unregisterTimerChanged(this);

        getActivity().unbindService(mTimeTrackingServiceConnection);
        super.onDestroy();
    }

    private void resetViewData() {
        mTogglTimerBtn.setSelected(false);
        mDescriptionET.setText(null);
        mProjectSpinner.setSelection(0);
        mDateTV.setText(null);
        mStartTimeTV.setText(null);
        mEndTimeTV.setText(null);
        setEmptyProjectColorView();
        updateTimeTv(0L);
    }

    private ProjectDTO getProjectById(Long id) {

        if (id == null) {
            return null;
        }

        int index = 0;
        boolean found = false;
        ProjectDTO project = null;

        while (index < mProjects.size() && !found) {
            project = mProjects.get(index);
            if (project.getId().equals(id)) {
                found = true;
            } else {
                index++;
            }
        }

        return found ? project : null;
    }

    private void setProjectColorView(ProjectDTO project) {
        mProjectColorView.setBackgroundColor(ProjectColor.getColor(project,
                Resources.loadResrourceColorTypedArray(getActivity(),
                        R.array.project_colors, R.color.soft_grey)));
    }

    private void setEmptyProjectColorView() {
        mProjectColorView.setBackgroundColor(getResources().getColor(
                android.R.color.transparent));
    }

    private void setViewData() {
        mDescriptionET.setText(mCurrentTimeEntry.getDescription());
        mDescriptionET
                .setSelection(mCurrentTimeEntry.getDescription().length());

        ProjectDTO project = getProjectById(mCurrentTimeEntry.getProjectId());

        int selectedProjectIndex = 0;

        if (project != null) {
            setProjectColorView(project);
            selectedProjectIndex = SpinnerWithPromptAdapter
                    .normalizePosition(mProjects.indexOf(project));
        }

        mProjectSpinner.setSelection(selectedProjectIndex);

        mDateTV.setText(DateTimeFormatter.onlyDate(mCurrentTimeEntry.getStart()));
        mStartTimeTV.setText(DateTimeFormatter.hhMMAMPM(mCurrentTimeEntry
                .getStart()));

        if (mCurrentTimeEntry.getStop() != null) {
            mEndTimeTV.setText(DateTimeFormatter.hhMMAMPM(mCurrentTimeEntry
                    .getStop()));
        }

        updateTimeTv(TimeTrackerController
                .convertIfRunningTime(mCurrentTimeEntry.getDuration()));
    }

    private synchronized void startTrackingController() {
        if (mCurrentTimeEntry == null) {
            this.mCurrentTimeEntry = new TimeEntryDTO();
        }

        this.mIsTracking = true;
        mTogglTimerBtn.setSelected(true);

        setViewData();

        long time = mTimeTrackerController.start(mCurrentTimeEntry);

        updateTimeTv(time);

    }

    private synchronized Long stopTrackingController() {
        if (mTimeTrackerController != null) {
            this.mIsTracking = false;
            Long time = mTimeTrackerController.stop();
            resetViewData();
            return time;
        }

        return null;
    }

    private synchronized void updateTimeTv(Long seconds) {
        if (seconds != null && mIsTracking) {
            mTimeTV.setText(DateTimeFormatter.periodHourBased(seconds));
        } else {
            mTimeTV.setText(DateTimeFormatter.periodHourBased(0L));
        }
    }

    @Override
    public void onTimeUpdated(TimeEntryDTO timeEntry, final long runningTime) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                updateTimeTv(runningTime);
            }
        });
    }

    @Override
    public boolean isListenToTimeEntry(long id) {
        boolean listen = mCurrentTimeEntry.isPersisted()
                && mCurrentTimeEntry.getId() == id;
        if (!listen) {
            hideTimerProgressBar();
        }
        return listen;
    }

    @Override
    public void onUpdateTimeEntry(TimeEntryDTO timeEntry) {
        hideTimerProgressBar();
        try {
            NullAwareBeanUtils.getInstance().copyProperties(mCurrentTimeEntry,
                    timeEntry);

            if (mIsTracking && mCurrentTimeEntry.getDuration() >= 0) {
                stopTracking();
            } else if (!mIsTracking && mCurrentTimeEntry.getDuration() < 0) {
                startToTrack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateTimeEntry(TimeEntryDTO timeEntry) {
        hideTimerProgressBar();
        if (timeEntry.getDuration() < 0
                && !timeEntry.getId().equals(mCurrentTimeEntry.getId())) {
            if (mIsTracking) {
                stopTracking();
            }
            this.mCurrentTimeEntry = timeEntry;
            startTrackingController();
        }
    }

    private final TextWatcher mDescriptionTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (mDescriptionTimeoutTask != null) {
                mDescriptionTimeoutTask.cancel();
            }
            if (mDescriptionTimeoutTimer != null) {
                mDescriptionTimeoutTimer.cancel();
            }

            String oldDescription = mCurrentTimeEntry.getDescription();
            if (!mCurrentTimeEntry.isPersisted() || mIsTracking) {

                mCurrentTimeEntry.setDescription(s.toString());

                if (mTimeTrackingServiceBound
                        && mCurrentTimeEntry.isPersisted()
                        && !oldDescription.equals(s.toString())) {

                    if (checkWaitForNextUpdate()) {
                        mDescriptionTimeoutTask = new TimerTask() {

                            @Override
                            public void run() {
                                checkForUpdateDescription();
                            }
                        };
                        mDescriptionTimeoutTimer = new Timer();
                        mDescriptionTimeoutTimer.schedule(
                                mDescriptionTimeoutTask,
                                DESCRIPTION_TIMEOUT_DELAY);
                    } else {
                        updateCurrentTimeEntry();
                    }
                }
            }
        }
    };

    private void onTimerButtonClick(View v) {
        if (mIsTracking) {
            stopTracking();
        } else {
            startToTrack();
        }
    }

    private void stopTracking() {
        if (mIsTracking) {
            stopTrackingController();

            mCurrentTimeEntry.setStop(new Date());

            mCurrentTimeEntry.setDuration(TimeTrackerController
                    .convertIfRunningTime(-(mCurrentTimeEntry.getStart()
                            .getTime() / 1000)));

            if (mTimeTrackingServiceBound) {

                updateCurrentTimeEntry();
            }

            this.mCurrentTimeEntry = new TimeEntryDTO();
        }
    }

    private void startToTrack() {
        mCurrentTimeEntry.setStart(new Date());
        mCurrentTimeEntry.setDuration(TimeTrackerController
                .getRunningTimeStart(0));

        startTrackingController();
        if (mTimeTrackingServiceBound) {

            updateCurrentTimeEntry();

        }
    }

    private void loadCurerntTimeEntry() {
        updateCurrentTimeEntry();
        getLoaderManager().restartLoader(0, null,
                new GenericLoaderCallbacksImpl<TimeEntryDTO>(this, null, this));
    }

    @Override
    public TimeEntryDTO loadInBackground(int flag, Bundle args)
            throws Exception {
        return ToggApp.getApplication().getTogglServices().manageTimeEntries()
                .current();
    }

    @Override
    public void onLoadFinished(
            Loader<GenericLoaderResponse<TimeEntryDTO>> loader,
            GenericLoaderResponse<TimeEntryDTO> data) {

        hideTimerProgressBar();

        TimeEntryDTO timer = data.getData();

        if (data.success() && timer != null) {
            if (!timer.getId().equals(mCurrentTimeEntry.getId())
                    || !timer.getAt().equals(mCurrentTimeEntry.getAt())
                    && timer.getDuration() < 0) {
                if (mIsTracking) {
                    stopTrackingController();
                }
                this.mCurrentTimeEntry = timer;
                startTrackingController();

            }
            mTimerContainer.notifyTimerChanged(this);
        } else {
            if (mIsTracking) {
                stopTracking();
            }
        }
    }

    private boolean checkWaitForNextUpdate() {
        long now = new Date().getTime();

        if (mLastTimerTimestamp != -1
                && mLastTimerTimestamp + DESCRIPTION_TIMEOUT_DELAY < now) {
            this.mLastTimerTimestamp = -1;
            return false;
        } else {
            this.mLastTimerTimestamp = now;
            return true;
        }
    }

    private void checkForUpdateDescription() {
        if (!checkWaitForNextUpdate()) {
            updateCurrentTimeEntry();
        }
    }

    private void updateCurrentTimeEntry() {
        if (mTimeTrackingServiceBound) {
            showTimerProgressBar();
            mTimeTrackingService.updateTimeEntry(mCurrentTimeEntry,
                    TimeTrackingService.ACTION_UPDATE);
        }
    }

    private void showTimerProgressBar() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mTimerProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void hideTimerProgressBar() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mTimerProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

}
