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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
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
import com.t3coode.togg.activities.services.TimeEntryUpdateBroadcastReceiver;
import com.t3coode.togg.activities.services.TimeEntryUpdateBroadcastReceiver.ReceiverListener;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.activities.services.TimeTrackingService.TimeTrackingBinder;
import com.t3coode.togg.activities.utils.DateTimeFormatter;
import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.services.utils.NullAwareBeanUtils;
import com.t3coode.togg.tracking.TimeTrackerController;

public class TimerEditFragment extends BaseFragment implements
        ReceiverListener, LoaderCallbacksFinishedListener<TimeEntryDTO>,
        DataLoader<TimeEntryDTO> {

    public static final String CURRENT_TIME_ENTRY_KEY = "com.t3coode.togg.activities.fragments.currentTimeEntryKey";
    public static final String CURRENT_TIME_ENTRY_ID_KEY = "com.t3coode.togg.activities.fragments.currentTimeEntryIdKey";

    private TimeEntryDTO mCurrentTimeEntry;
    private Long mCurrentTimeEntryId;
    private TimerContainer mTimerContainer;

    private View mProjectColorView;
    private TextView mTimeTV;
    private EditText mDescriptionET;
    private Spinner mProjectSpinner;
    private TextView mDateTV;
    private TextView mStartTimeTV;
    private TextView mEndTimeTV;
    private ProgressBar mTimerProgressBar;
    private View mResumeButton;
    private View mDeleteButton;

    private TimeEntryUpdateBroadcastReceiver mTimeEntryUpdateReceiver;

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
            TimerEditFragment.this.mTimeTrackingService = null;
            TimerEditFragment.this.mTimeTrackingServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerEditFragment.this.mTimeTrackingService = ((TimeTrackingBinder) service)
                    .getService();
            TimerEditFragment.this.mTimeTrackingServiceBound = true;
        }
    };

    @Override
    public void onAttach(Activity activity) {
        this.mTimerContainer = (TimerContainer) activity;
        super.onAttach(activity);
    }

    private void loadCurrentTimeEntryId(Bundle savedInstanceState) {
        if (mCurrentTimeEntryId == null) {
            long id = savedInstanceState.getLong(CURRENT_TIME_ENTRY_ID_KEY, -1);
            this.mCurrentTimeEntryId = (id != -1) ? id : null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        loadCurrentTimeEntryId(savedInstanceState);

        this.mTimeEntryUpdateReceiver = new TimeEntryUpdateBroadcastReceiver(
                this);

        Intent i = new Intent(getActivity(), TimeTrackingService.class);
        getActivity().bindService(i, mTimeTrackingServiceConnection,
                Service.BIND_AUTO_CREATE);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_timer_edit, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        this.mTimerProgressBar = (ProgressBar) getView().findViewById(
                R.id.timer_progress_bar);

        this.mDescriptionET = (EditText) getView().findViewById(
                R.id.description);

        mDescriptionET.addTextChangedListener(mDescriptionTextWatcher);

        this.mTimeTV = (TextView) getView().findViewById(R.id.time);

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
                    projectId = project.getId();
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

        this.mResumeButton = getView().findViewById(R.id.resume_time_entry);

        this.mResumeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                resumeCurrentTimeEntry();
            }
        });

        this.mDeleteButton = getView().findViewById(R.id.delete_time_entry);

        this.mDeleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteCurrentTimeEntry();

            }
        });

        loadCurerntTimeEntry();

        super.onViewCreated(v, savedInstanceState);
    }

    @Override
    public void onResume() {

        IntentFilter intentFilter = new IntentFilter(
                TimeTrackingService.BROADCAST_TIME_ENTRY_UPDATE);
        getActivity().registerReceiver(mTimeEntryUpdateReceiver, intentFilter);
        setViewData();

        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(CURRENT_TIME_ENTRY_ID_KEY, mCurrentTimeEntryId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        getActivity().unregisterReceiver(mTimeEntryUpdateReceiver);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(mTimeTrackingServiceConnection);
        super.onDestroy();
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

    private void setViewData() {
        mDescriptionET.setText(mCurrentTimeEntry.getDescription());
        mDescriptionET
                .setSelection(mCurrentTimeEntry.getDescription().length());

        ProjectDTO project = getProjectById(mCurrentTimeEntry.getProjectId());

        int selectedProjectIndex = 0;

        if (project != null) {

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

    private synchronized void updateTimeTv(Long seconds) {
        if (seconds != null) {
            mTimeTV.setText(DateTimeFormatter.periodHourBased(seconds));
        } else {
            mTimeTV.setText(DateTimeFormatter.periodHourBased(0L));
        }
    }

    @Override
    public boolean isListenToTimeEntry(long id) {
        boolean listen = mCurrentTimeEntry.isPersisted()
                && mCurrentTimeEntryId == id;
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

            setViewData();
        } catch (Exception e) {
            e.printStackTrace();
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

            mCurrentTimeEntry.setDescription(s.toString());

            if (mTimeTrackingServiceBound && mCurrentTimeEntry.isPersisted()
                    && !oldDescription.equals(s.toString())) {

                if (checkWaitForNextUpdate()) {
                    mDescriptionTimeoutTask = new TimerTask() {

                        @Override
                        public void run() {
                            checkForUpdateDescription();
                        }
                    };
                    mDescriptionTimeoutTimer = new Timer();
                    mDescriptionTimeoutTimer.schedule(mDescriptionTimeoutTask,
                            DESCRIPTION_TIMEOUT_DELAY);
                } else {
                    updateCurrentTimeEntry();
                }
            }
        }

    };

    private void loadCurerntTimeEntry() {
        showTimerProgressBar();
        getLoaderManager().restartLoader(0, null,
                new GenericLoaderCallbacksImpl<TimeEntryDTO>(this, null, this));
    }

    @Override
    public TimeEntryDTO loadInBackground(int flag, Bundle args)
            throws Exception {
        return ToggApp.getApplication().getTogglServices().manageTimeEntries()
                .get(mCurrentTimeEntryId);
    }

    @Override
    public void onLoadFinished(
            Loader<GenericLoaderResponse<TimeEntryDTO>> loader,
            GenericLoaderResponse<TimeEntryDTO> data) {

        hideTimerProgressBar();

        TimeEntryDTO timer = data.getData();

        if (data.success() && timer != null) {
            if (mCurrentTimeEntry == null
                    || !timer.getId().equals(mCurrentTimeEntry.getId())
                    || !timer.getAt().equals(mCurrentTimeEntry.getAt())
                    && timer.getDuration() < 0) {
                setCurrentTimeEntry(timer);

            }
            mTimerContainer.notifyTimerChanged(this);
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

    private void resumeCurrentTimeEntry() {
        TimeEntryDTO timeEntry = mCurrentTimeEntry;

        if (ToggApp.getApplication().getCurrentUser().isStoreStartAndStopTime()) {
            timeEntry = timeEntry.resumeTimer(false);
            showTimerProgressBar();
        } else {
            timeEntry = timeEntry.resumeTimer(true);
            getFragmentManager().popBackStack();
        }

        mTimeTrackingService.updateTimeEntry(timeEntry,
                TimeTrackingService.ACTION_UPDATE);
    }

    private void deleteCurrentTimeEntry() {
        mTimeTrackingService.deleteTimeEntry(mCurrentTimeEntry);
        getFragmentManager().popBackStack();
    }

    public void setCurrentTimeEntry(TimeEntryDTO mCurrentTimeEntry) {
        this.mCurrentTimeEntry = mCurrentTimeEntry;
        this.mCurrentTimeEntryId = mCurrentTimeEntry.getId();
        if (getView() != null) {
            setViewData();
        }
    }
}
