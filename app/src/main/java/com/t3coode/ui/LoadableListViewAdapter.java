package com.t3coode.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public interface LoadableListViewAdapter<T extends ListAdapter> extends
        ListAdapter, WrapperListAdapter {

    int getViewTypeLoader();

    View getLoaderView(int position, View convertView, ViewGroup parent);

    ListAdapter getWrappedAdapter();

    void setLoading(boolean loading);

    boolean isLoading();
}
