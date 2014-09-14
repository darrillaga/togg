package com.t3coode.ui;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class StickyHeaderListViewArrayAdapterBase<T, D> extends
        ArrayAdapter<T> implements StickyHeaderListViewAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<T> mObjectList;
    private List<Object> mTotalItems = new ArrayList<Object>();
    private SparseIntArray mPrevHeadersPositionByIndex = new SparseIntArray();
    private SparseIntArray mNextHeadersPositionByIndex = new SparseIntArray();
    private Class<T> mGenericClass;
    private boolean mNotifiyng;
    private AsyncTask<Void, Void, Void> mCurrentMetadataTask;

    // private SparseIntArray mHeadersPositions;

    private SparseIntArray mViewTypesByPosition = new SparseIntArray();

    public StickyHeaderListViewArrayAdapterBase(Context context, int resource,
            List<T> objects) {
        super(context, resource, objects);
        this.mObjectList = objects;

        generateAsyncListMetadata();

        registerDataSetObserver(new DataSetObserver() {

            @Override
            public void onChanged() {
                if (!mNotifiyng) {
                    generateAsyncListMetadata();
                }
                mNotifiyng = false;
                super.onChanged();
            }

            @Override
            public void onInvalidated() {
                if (!mNotifiyng) {
                    generateAsyncListMetadata();
                }
                mNotifiyng = false;
                super.onInvalidated();

            }
        });
    }

    public void sortList(List<T> objectList) {
        Collections.sort(objectList, new Comparator<T>() {

            @Override
            public int compare(T lhs, T rhs) {
                return compareItems(lhs, rhs);
            }
        });
    }

    public abstract int compareItems(T lhs, T rhs);

    public abstract int compareHeadersData(D lhs, D rhs);

    public abstract D getHeadersData(T item);

    protected void generateAsyncListMetadata() {
        synchronized (mObjectList) {
            if (mCurrentMetadataTask != null) {
                mCurrentMetadataTask.cancel(true);
            }

            final StickyHeaderListViewArrayAdapterBase<T, D> that = this;

            this.mCurrentMetadataTask = new AsyncTask<Void, Void, Void>() {
                private MetadataDTO metadata;

                @Override
                protected Void doInBackground(Void... params) {
                    this.metadata = generateListMetadata();
                    return null;
                }

                protected void onPostExecute(Void result) {
                    mNotifiyng = true;
                    that.mPrevHeadersPositionByIndex = metadata
                            .getPrevHeadersPositionByIndex();
                    that.mNextHeadersPositionByIndex = metadata
                            .getNextHeadersPositionByIndex();
                    that.mViewTypesByPosition = metadata
                            .getViewTypesByPosition();
                    that.mTotalItems = metadata.getTotalItems();

                    StickyHeaderListViewArrayAdapterBase.super
                            .notifyDataSetChanged();
                };
            };
            mCurrentMetadataTask.execute();
        }
    }

    protected List<T> getObjects() {
        return mObjectList;
    }

    protected MetadataDTO generateListMetadata() {
        synchronized (mObjectList) {
            MetadataDTO metadata = new MetadataDTO();

            D currentHeaderData = null;
            int currentHeaderDataPosition = 0;
            SparseIntArray headerPositions = new SparseIntArray();
            // this.mHeadersPositions = new SparseIntArray();

            sortList(mObjectList);

            for (T item : mObjectList) {

                if (currentHeaderData == null
                        || compareHeadersData(currentHeaderData,
                                getHeadersData(item)) != 0) {

                    currentHeaderData = getHeadersData(item);

                    metadata.getTotalItems().add(currentHeaderData);

                    int lastCurrentHeaderDataPosition = currentHeaderDataPosition;

                    currentHeaderDataPosition = metadata.getTotalItems().size() - 1;

                    metadata.getViewTypesByPosition().append(
                            currentHeaderDataPosition, VIEW_TYPE_HEADER);

                    headerPositions.append(lastCurrentHeaderDataPosition,
                            currentHeaderDataPosition);

                    // mHeadersPositions.append(lastCurrentDatePosition,
                    // currentDatePosition);

                    metadata.getPrevHeadersPositionByIndex().append(
                            currentHeaderDataPosition,
                            currentHeaderDataPosition);
                }

                metadata.getTotalItems().add(item);

                int position = metadata.getTotalItems().size() - 1;

                metadata.getViewTypesByPosition().append(position,
                        VIEW_TYPE_ITEM);

                metadata.getPrevHeadersPositionByIndex().append(position,
                        currentHeaderDataPosition);
            }

            for (int index = 0; index < metadata
                    .getPrevHeadersPositionByIndex().size(); index++) {
                int key = metadata.getPrevHeadersPositionByIndex().keyAt(index);
                int value = metadata.getPrevHeadersPositionByIndex().valueAt(
                        index);

                if (key != value) {
                    value = headerPositions.get(value, -1);
                }

                metadata.getNextHeadersPositionByIndex().append(key, value);

            }

            return metadata;
        }
    }

    @Override
    public int getCount() {
        return mTotalItems.size();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getItem(int position) {

        T item = null;

        Object object = mTotalItems.get(position);

        if (mGenericClass == null) {
            this.mGenericClass = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        }

        if (mGenericClass.isInstance(object)) {
            item = (T) object;
        }

        return item;
    }

    public Object getRawItem(int position) {

        return mTotalItems.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mViewTypesByPosition.get(position);
        // if (getItem(position) != null) {
        // return VIEW_TYPE_PURCAHSE;
        // } else {
        // return VIEW_TYPE_HEADER;
        // }
    }

    @Override
    public int getNextHeaderViewPosition(int position) {
        return mNextHeadersPositionByIndex.get(position, -1);

        // int nextPosition = mPrevHeadersPositionByIndex.get(position, -1);
        // if (position != nextPosition) {
        // nextPosition = mHeadersPositions.get(nextPosition, -1);
        // }
        // return nextPosition;
    }

    @Override
    public int getPrevHeaderViewPosition(int position) {
        return mPrevHeadersPositionByIndex.get(position, -1);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            return getHeaderView(position, convertView, parent);
        } else {
            return getItemView(position, convertView, parent);
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == VIEW_TYPE_HEADER) ? false : super
                .isEnabled(position);
    }

    public abstract View getItemView(int position, View convertView,
            ViewGroup parent);

    public abstract View getHeaderView(int position, View convertView,
            ViewGroup parent);

    protected static final class MetadataDTO {
        private List<Object> totalItems = new ArrayList<Object>();
        private SparseIntArray prevHeadersPositionByIndex = new SparseIntArray();
        private SparseIntArray nextHeadersPositionByIndex = new SparseIntArray();
        private SparseIntArray viewTypesByPosition = new SparseIntArray();

        public List<Object> getTotalItems() {
            return totalItems;
        }

        public SparseIntArray getPrevHeadersPositionByIndex() {
            return prevHeadersPositionByIndex;
        }

        public SparseIntArray getNextHeadersPositionByIndex() {
            return nextHeadersPositionByIndex;
        }

        public SparseIntArray getViewTypesByPosition() {
            return viewTypesByPosition;
        }

    }
}
