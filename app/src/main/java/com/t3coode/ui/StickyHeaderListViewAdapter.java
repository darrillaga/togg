package com.t3coode.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public interface StickyHeaderListViewAdapter extends ListAdapter {

    static final int VIEW_TYPE_HEADER = 0;

    int getNextHeaderViewPosition(int position);

    int getPrevHeaderViewPosition(int position);

    View getHeaderView(int position, View convertView, ViewGroup parent);
}
