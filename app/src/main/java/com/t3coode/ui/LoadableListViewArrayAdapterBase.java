package com.t3coode.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.t3coode.togg.R;

public class LoadableListViewArrayAdapterBase<T> extends BaseAdapter implements
        LoadableListViewAdapter<ArrayAdapter<T>> {

    private static final int VIEW_TYPE_COUNT = 1;
    private ArrayAdapter<T> mAdapter;
    private boolean mLoading;

    public LoadableListViewArrayAdapterBase(Context context, int resource,
            ArrayAdapter<T> adapter) {

        this.mAdapter = adapter;
    }

    @Override
    public int getViewTypeLoader() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT + mAdapter.getViewTypeCount();
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (isInitLoaderEnabled()
                || (position == mAdapter.getCount() && mLoading)) {
            return getLoaderView(position, convertView, parent);
        } else {
            return mAdapter.getView(position, convertView, parent);
        }
    }

    private boolean isInitLoaderEnabled() {
        return (mAdapter.getCount() == 0 && mLoading);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return isInitLoaderEnabled() ? false : mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int i) {
        return isInitLoaderEnabled() ? false : mAdapter.isEnabled(i);
    }

    @Override
    public int getCount() {
        if (isInitLoaderEnabled()) {
            return 1;
        } else if (mLoading) {
            return mAdapter.getCount() + 1;
        } else {
            return mAdapter.getCount();
        }
    }

    @Override
    public Object getItem(int i) {
        return isInitLoaderEnabled() ? null : mAdapter.getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return isInitLoaderEnabled() ? -1 : mAdapter.getItemId(i);
    }

    @Override
    public int getItemViewType(int i) {
        return (isInitLoaderEnabled() || mAdapter.getCount() <= i) ? getViewTypeLoader()
                : mAdapter.getItemViewType(i);
    }

    @Override
    public boolean hasStableIds() {
        return isInitLoaderEnabled() ? true : mAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return !isInitLoaderEnabled() && mAdapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver datasetobserver) {
        super.registerDataSetObserver(datasetobserver);
        mAdapter.registerDataSetObserver(datasetobserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver datasetobserver) {
        super.unregisterDataSetObserver(datasetobserver);
        mAdapter.unregisterDataSetObserver(datasetobserver);
    }

    @Override
    public View getLoaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            return LayoutInflater.from(mAdapter.getContext()).inflate(
                    R.layout.layout_list_loader_view, parent, false);
        } else {
            return convertView;
        }
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    @Override
    public void setLoading(boolean loading) {
        this.mLoading = loading;

        if (!loading) {
            mAdapter.notifyDataSetChanged();
        }

        notifyDataSetChanged();

    }

    @Override
    public boolean isLoading() {
        return mLoading;
    }
}
