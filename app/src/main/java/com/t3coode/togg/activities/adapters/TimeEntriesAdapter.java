package com.t3coode.togg.activities.adapters;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeComparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.t3coode.togg.R;
import com.t3coode.togg.activities.adapters.TimeEntriesAdapter.TimeHeader;
import com.t3coode.togg.activities.services.ProjectManager;
import com.t3coode.togg.activities.utils.DateTimeFormatter;
import com.t3coode.togg.activities.utils.ProjectColor;
import com.t3coode.togg.activities.utils.Resources;
import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.ui.StickyHeaderListViewArrayAdapterBase;

public class TimeEntriesAdapter extends
        StickyHeaderListViewArrayAdapterBase<TimeEntryDTO, TimeHeader> {

    private int[] mColors;
    private DateTimeComparator mComparator;
    private OnResumeTimeEntryListener mOnResumeTimeEntryListener;

    public TimeEntriesAdapter(Context context, int resource,
            List<TimeEntryDTO> objects) {
        super(context, resource, objects);

        this.mColors = Resources.loadResrourceColorTypedArray(context,
                R.array.project_colors, R.color.soft_grey);
    }

    @Override
    protected MetadataDTO generateListMetadata() {
        synchronized (getObjects()) {
            MetadataDTO metadata = super.generateListMetadata();

            TimeHeader currentTimeHeader = null;
            int currentTimeHeaderPosition = 0;

            for (int index = 0; index < metadata.getTotalItems().size(); index++) {
                if (currentTimeHeader == null) {
                    index = metadata.getNextHeadersPositionByIndex().get(index,
                            -1);
                    currentTimeHeaderPosition = index;
                    currentTimeHeader = (TimeHeader) metadata.getTotalItems()
                            .get(currentTimeHeaderPosition);
                }

                if (index != currentTimeHeaderPosition
                        && index != metadata.getNextHeadersPositionByIndex()
                                .get(index, -1)) {
                    currentTimeHeader.duration += ((TimeEntryDTO) metadata
                            .getTotalItems().get(index)).getDuration();
                } else {
                    currentTimeHeaderPosition = index;
                    currentTimeHeader = (TimeHeader) metadata.getTotalItems()
                            .get(currentTimeHeaderPosition);
                }
            }

            return metadata;
        }
    }

    private DateTimeComparator getComparator() {
        if (mComparator == null) {
            this.mComparator = DateTimeComparator.getDateOnlyInstance();
        }

        return mComparator;
    }

    @Override
    public int compareItems(TimeEntryDTO lhs, TimeEntryDTO rhs) {
        return getComparator().compare(rhs.getStart(), lhs.getStart());
    }

    @Override
    public int compareHeadersData(TimeHeader lhs, TimeHeader rhs) {
        return getComparator().compare(lhs.date, rhs.date);
    }

    @Override
    public TimeHeader getHeadersData(TimeEntryDTO item) {
        TimeHeader timeHeader = new TimeHeader();
        timeHeader.date = item.getStart();
        return timeHeader;
    }

    private static class Holder {
        public TextView descriptionTV;
        public TextView durationTV;
        public TextView projectNameTV;
        public TextView projectLetterTV;
        public TextView timesTV;
        public View projectColor;
        public ImageButton playButton;
        public View playButtonWrapper;
    }

    private static class HeaderHolder {
        public TextView dateTV;
        public TextView timeTV;
    }

    @Override
    public View getItemView(final int position, View convertView,
            final ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_time_entries_list_item, parent, false);

            holder = new Holder();

            holder.descriptionTV = (TextView) convertView
                    .findViewById(R.id.description);
            holder.durationTV = (TextView) convertView.findViewById(R.id.time);

            holder.projectNameTV = (TextView) convertView
                    .findViewById(R.id.project_name);

            holder.projectLetterTV = (TextView) convertView
                    .findViewById(R.id.project_letter);

            holder.timesTV = (TextView) convertView.findViewById(R.id.times);

            holder.projectColor = convertView.findViewById(R.id.project_color);

            holder.playButton = (ImageButton) convertView
                    .findViewById(R.id.play_button);

            holder.playButtonWrapper = convertView
                    .findViewById(R.id.play_button_wrapper);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        TimeEntryDTO timeEntry = getItem(position);

        holder.descriptionTV.setText(timeEntry.getDescription());

        holder.durationTV.setText(DateTimeFormatter.periodHourBased(timeEntry
                .getDuration()));

        holder.timesTV.setText(DateTimeFormatter.hhMMAMPM(timeEntry.getStart())
                + " - " + DateTimeFormatter.hhMMAMPM(timeEntry.getStop()));

        setProjectNameAndColor(holder, timeEntry.getProjectId());

        holder.playButton.setFocusable(false);
        holder.playButton.setFocusableInTouchMode(false);

        OnClickListener playButtonClickListener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                mOnResumeTimeEntryListener.onResumeTimeEntry((ListView) parent,
                        view, position, getItemId(position));
            }
        };

        if (mOnResumeTimeEntryListener != null) {
            holder.playButtonWrapper
                    .setOnClickListener(playButtonClickListener);
            holder.playButton.setOnClickListener(playButtonClickListener);
        }

        return convertView;
    }

    public void setOnResumeTimeEntryListener(
            OnResumeTimeEntryListener mOnResumeTimeEntryListener) {
        this.mOnResumeTimeEntryListener = mOnResumeTimeEntryListener;
    }

    private void setProjectNameAndColor(Holder holder, Long projectId) {
        int color = getContext().getResources().getColor(R.color.soft_grey);

        String noProjectName = "("
                + getContext().getResources().getString(R.string.no_project)
                + ")";

        String projectName = noProjectName;

        if (projectId != null) {
            ProjectDTO project = ProjectManager.getInstance().get(projectId);

            if (project != null) {
                color = ProjectColor.getColor(project, mColors);

                projectName = project.getName();
            }
        }

        if (projectName.length() > 0 && projectName != noProjectName) {
            holder.projectLetterTV.setText(projectName.substring(0, 1)
                    .toUpperCase());
        } else {
            holder.projectLetterTV.setText(null);
        }

        holder.projectNameTV.setText(projectName);

        holder.projectColor.setBackgroundColor(color);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        HeaderHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_time_entries_list_header, parent, false);

            holder = new HeaderHolder();

            holder.dateTV = (TextView) convertView.findViewById(R.id.date);
            holder.timeTV = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (HeaderHolder) convertView.getTag();
        }

        TimeHeader timeHeader = (TimeHeader) getRawItem(position);

        holder.dateTV.setText(StringUtils.capitalizeFully(DateTimeFormatter
                .dayOrDate(timeHeader.date)));

        holder.timeTV.setText(DateTimeFormatter
                .periodHourMinBased(timeHeader.duration));

        return convertView;
    }

    public static class TimeHeader {
        public Date date;
        public long duration;
    }

    public static interface OnResumeTimeEntryListener {
        void onResumeTimeEntry(ListView l, View v, int position, long id);
    }
}
