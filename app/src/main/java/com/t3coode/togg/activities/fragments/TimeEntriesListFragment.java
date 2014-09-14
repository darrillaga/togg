package com.t3coode.togg.activities.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.ErrorHandler;
import com.t3coode.togg.activities.TimerContainer;
import com.t3coode.togg.activities.TimerContainer.TimerObserver;
import com.t3coode.togg.activities.adapters.TimeEntriesAdapter;
import com.t3coode.togg.activities.adapters.TimeEntriesAdapter.OnResumeTimeEntryListener;
import com.t3coode.togg.activities.async.GenericLoader.DataLoader;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderCallbacksImpl;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;
import com.t3coode.togg.activities.async.GenericLoader.LoaderCallbacksFinishedListener;
import com.t3coode.togg.activities.services.ProjectManager;
import com.t3coode.togg.activities.services.TimeEntryDeleteBroadcastReceiver;
import com.t3coode.togg.activities.services.TimeEntryUpdateBroadcastReceiver;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.activities.services.TimeTrackingService.TimeTrackingBinder;
import com.t3coode.togg.services.TogglServicesFactory;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.services.utils.DateUtil;
import com.t3coode.ui.StickyHeaderListViewAdapter;
import com.t3coode.ui.StickyHeaderListViewController;

public class TimeEntriesListFragment extends BaseListFragment implements
        TimerObserver, DataLoader<List<TimeEntryDTO>>,
        LoaderCallbacksFinishedListener<List<TimeEntryDTO>>,
        TimeEntryUpdateBroadcastReceiver.ReceiverListener,
        TimeEntryDeleteBroadcastReceiver.ReceiverListener,
        OnResumeTimeEntryListener {

    private List<TimeEntryDTO> mTimeEntries = new ArrayList<TimeEntryDTO>();
    private StickyHeaderListViewController mStickyController;
    private ViewGroup mStickyHolder;
    private TimerContainer mTimerContainer;
    private ImageButton mRefreshButton;
    private ProgressBar mRefreshButtonProgressBar;
    // private PullToRefreshListView mListView;
    private ListView mListView;
    private ArrayAdapter<TimeEntryDTO> mAdapter;

    private TimeTrackingService mTimeTrackingService;
    private boolean mTimeTrackingServiceBound;
    private TimeEntryUpdateBroadcastReceiver mTimeEntryUpdateReceiver;
    private TimeEntryDeleteBroadcastReceiver mTimeEntryDeleteReceiver;

    private ServiceConnection mTimeTrackingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            TimeEntriesListFragment.this.mTimeTrackingService = null;
            TimeEntriesListFragment.this.mTimeTrackingServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimeEntriesListFragment.this.mTimeTrackingService = ((TimeTrackingBinder) service)
                    .getService();
            TimeEntriesListFragment.this.mTimeTrackingServiceBound = true;
        }
    };

    @Override
    public void onAttach(Activity activity) {
        this.mTimerContainer = (TimerContainer) activity;
        mTimerContainer.registerTimerChanged(this);
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.mTimeEntryUpdateReceiver = new TimeEntryUpdateBroadcastReceiver(
                this);

        this.mTimeEntryDeleteReceiver = new TimeEntryDeleteBroadcastReceiver(
                this);

        Intent i = new Intent(getActivity(), TimeTrackingService.class);
        getActivity().bindService(i, mTimeTrackingServiceConnection,
                Service.BIND_AUTO_CREATE);

        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_entries_list, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mTimeEntries.addAll(ToggApp.getApplication().getCurrentUser()
                .getTimeEntries());

        checkTimeEntries(mTimeEntries);

        this.mListView = getListView();

        this.mRefreshButton = (ImageButton) view
                .findViewById(R.id.action_refresh);

        this.mRefreshButtonProgressBar = (ProgressBar) view
                .findViewById(R.id.refresh_progress_bar);

        this.mAdapter = new TimeEntriesAdapter(getActivity(),
                android.R.layout.simple_list_item_1, mTimeEntries);

        mListView.setAdapter(mAdapter);

        ProjectManager.getInstance().registerObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mAdapter.notifyDataSetChanged();
                super.onChanged();
            }
        });

        this.mStickyHolder = (ViewGroup) view.findViewById(R.id.sticky_layer);

        ViewGroup stickyHolderContent = (ViewGroup) mStickyHolder.getChildAt(0);

        View emptyStickyHolderView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_time_entries_empty_list_header,
                        stickyHolderContent, false);

        this.mStickyController = new StickyHeaderListViewController(
                getListView(), stickyHolderContent, emptyStickyHolderView,
                (StickyHeaderListViewAdapter) mAdapter);

        setListAdapter(mAdapter);

        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (mAdapter.getCount() > 0) {
                    mStickyController.initStickyController();
                }
                super.onChanged();
            }

            @Override
            public void onInvalidated() {
                if (mAdapter.getCount() > 0) {
                    mStickyController.initStickyController();
                }
                super.onInvalidated();
            }
        });

        ((TimeEntriesAdapter) mAdapter).setOnResumeTimeEntryListener(this);

        mRefreshButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadTimeEntries();

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void loadTimeEntries() {

        if (mListView == null) {
            return;
        }

        showRefreshButtonPreloader();

        getLoaderManager().restartLoader(
                this.hashCode(),
                null,
                new GenericLoaderCallbacksImpl<List<TimeEntryDTO>>(this, null,
                        this));
    }

    private void checkTimeEntries(List<TimeEntryDTO> list) {

        Comparator<TimeEntryDTO> comparator = new Comparator<TimeEntryDTO>() {

            @Override
            public int compare(TimeEntryDTO lhs, TimeEntryDTO rhs) {
                return DateUtil.compare(rhs.getStart(), lhs.getStart());
            }
        };

        Collections.sort(list, comparator);

        deleteCurrentEntry(list);
    }

    private void deleteCurrentEntry(List<TimeEntryDTO> list) {
        Iterator<TimeEntryDTO> it = list.iterator();

        while (it.hasNext()) {
            TimeEntryDTO entry = it.next();
            if (entry.getDuration() < 0) {
                it.remove();
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TimeEntryDTO selectedTimeEntry = (TimeEntryDTO) l
                .getItemAtPosition(position);

        if (selectedTimeEntry != null) {
            TimerEditFragment timerEditFragment = (TimerEditFragment) getFragmentManager()
                    .findFragmentByTag(TimerEditFragment.class.getName());

            if (timerEditFragment == null) {
                timerEditFragment = new TimerEditFragment();
                timerEditFragment.setCurrentTimeEntry(selectedTimeEntry);
            }

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragments_container, timerEditFragment,
                            TimerEditFragment.class.getName())
                    .addToBackStack(null).commit();
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onTimerChanged(Object trigger) {
        if (trigger != this) {
            loadTimeEntries();
        }
    }

    @Override
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter(
                TimeTrackingService.BROADCAST_TIME_ENTRY_UPDATE);
        getActivity().registerReceiver(mTimeEntryUpdateReceiver, intentFilter);
        intentFilter = new IntentFilter(
                TimeTrackingService.BROADCAST_TIME_ENTRY_DELETE);
        getActivity().registerReceiver(mTimeEntryDeleteReceiver, intentFilter);
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mTimeEntryUpdateReceiver);
        getActivity().unregisterReceiver(mTimeEntryDeleteReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mTimerContainer.unregisterTimerChanged(this);
        if (mTimeTrackingServiceBound) {
            getActivity().unbindService(mTimeTrackingServiceConnection);
        }
        super.onDestroy();
    }

    @Override
    public List<TimeEntryDTO> loadInBackground(int flag, Bundle args)
            throws Exception {
        return TogglServicesFactory.getToggleServices().manageTimeEntries()
                .list(null, null);
    }

    private void hideRefreshButtonPreloader() {
        mRefreshButton.setVisibility(View.VISIBLE);
        mRefreshButtonProgressBar.setVisibility(View.GONE);
    }

    private void showRefreshButtonPreloader() {
        mRefreshButton.setVisibility(View.GONE);
        mRefreshButtonProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(
            Loader<GenericLoaderResponse<List<TimeEntryDTO>>> loader,
            GenericLoaderResponse<List<TimeEntryDTO>> data) {

        hideRefreshButtonPreloader();

        if (data.success()) {
            ToggApp.getApplication().getCurrentUser().getTimeEntries().clear();
            ToggApp.getApplication().getCurrentUser().getTimeEntries()
                    .addAll(data.getData());
            ToggApp.getApplication().onUserChanged();

            checkTimeEntries(data.getData());

            synchronized (mTimeEntries) {
                mTimeEntries.clear();
                mTimeEntries.addAll(data.getData());

                mAdapter.notifyDataSetChanged();

                // mListView.onRefreshComplete();
                mStickyController.restartStickyController();
            }

        } else {
            ErrorHandler.handle(getActivity(), data.getError());
        }

    }

    @Override
    public boolean isListenToTimeEntry(long id) {
        return true;
    }

    @Override
    public void onUpdateTimeEntry(TimeEntryDTO timeEntry) {
        if (timeEntry.getDuration() >= 0) {
            onTimerChanged(null);
        }
    }

    @Override
    public void onDeleteTimeEntry(TimeEntryDTO timeEntry) {
        loadTimeEntries();
    }

    @Override
    public void onResumeTimeEntry(ListView l, View v, int position, long id) {
        TimeEntryDTO timeEntry = (TimeEntryDTO) l.getItemAtPosition(position);

        if (ToggApp.getApplication().getCurrentUser().isStoreStartAndStopTime()) {
            timeEntry = timeEntry.resumeTimer(false);
        } else {
            timeEntry = timeEntry.resumeTimer(true);
            mTimeEntries.remove(position);
            mAdapter.notifyDataSetChanged();
        }

        showRefreshButtonPreloader();

        mTimeTrackingService.updateTimeEntry(timeEntry,
                TimeTrackingService.ACTION_UPDATE);
    }
}
