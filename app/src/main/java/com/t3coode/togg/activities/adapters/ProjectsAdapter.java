package com.t3coode.togg.activities.adapters;

import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.t3coode.togg.R;
import com.t3coode.togg.activities.utils.ProjectColor;
import com.t3coode.togg.activities.utils.Resources;
import com.t3coode.togg.services.dtos.ProjectDTO;

public class ProjectsAdapter extends ArrayAdapter<ProjectDTO> {

    private int[] mColors;

    public ProjectsAdapter(Context context, List<ProjectDTO> objects) {
        super(context, View.NO_ID, objects);

        this.mColors = Resources.loadResrourceColorTypedArray(context,
                R.array.project_colors, R.color.soft_grey);
    }

    private static class Holder {
        public View colorView;
        public TextView projectNameTV;
    }

    public View fillWithProject(int position, View convertView,
            ViewGroup parent, int layoutId) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutId,
                    parent, false);

            holder = new Holder();

            holder.colorView = convertView.findViewById(R.id.project_color);
            holder.projectNameTV = (TextView) convertView
                    .findViewById(R.id.project_name);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        ProjectDTO project = getItem(position);

        holder.colorView.setBackgroundColor(ProjectColor.getColor(project,
                mColors));
        holder.projectNameTV.setText(project.getName());

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return fillWithProject(position, convertView, parent,
                R.layout.layout_projects_list_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        convertView = fillWithProject(position, convertView, parent,
                R.layout.layout_projects_spiner_list_item);

        int paddingBottom = convertView.getPaddingBottom();

        if (position == getCount() - 1) {
            paddingBottom += TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources()
                            .getDisplayMetrics());
        }

        convertView.setPadding(convertView.getPaddingLeft(),
                convertView.getPaddingTop(), convertView.getPaddingRight(),
                paddingBottom);

        return convertView;
    }
}
