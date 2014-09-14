package com.t3coode.togg.activities.async;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;

public class GenericLoader<T> extends AsyncTaskLoader<GenericLoaderResponse<T>> {

    private DataLoader<T> mLoader;
    private GenericLoaderResponse<T> mData;
    private int mOriginFlag;
    private Bundle mArgs;

    public GenericLoader(Context context, int originFlag, DataLoader<T> loader) {
        super(context);
        this.mLoader = loader;
        this.mOriginFlag = originFlag;
    }

    public GenericLoader(Context context, int originFlag, DataLoader<T> loader,
            Bundle args) {
        super(context);
        this.mLoader = loader;
        this.mOriginFlag = originFlag;
        this.mArgs = args;
    }

    @Override
    public GenericLoaderResponse<T> loadInBackground() {

        GenericLoaderResponse<T> response = new GenericLoaderResponse<T>();
        try {
            response.setData(mLoader.loadInBackground(mOriginFlag, mArgs));
        } catch (Exception ex) {
            response.setError(ex);
        }
        return response;
    }

    /**
     * Called when there is new data to deliver to the client. The super class
     * will take care of delivering it; the implementation here just adds a
     * little more logic.
     */
    @Override
    public void deliverResult(GenericLoaderResponse<T> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (data != null && data.success()) {
                onReleaseResources(data);
            }
        }
        GenericLoaderResponse<T> oldApps = data;
        mData = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null && oldApps.success()) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mData != null && mData.success()) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null || mData.hasError()) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(GenericLoaderResponse<T> data) {
        super.onCanceled(data);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null && mData.success()) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated with an
     * actively loaded data set.
     */
    protected void onReleaseResources(GenericLoaderResponse<T> data) {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }

    // Extends loader callback to add the possibility to use the
    // GenericLoaderResponse
    public static interface LoaderCallbacks<T>
            extends
            android.support.v4.app.LoaderManager.LoaderCallbacks<GenericLoaderResponse<T>>,
            LoaderCallbacksBase<T> {

        @Override
        public Loader<GenericLoaderResponse<T>> onCreateLoader(int id,
                Bundle loader);

        @Override
        public void onLoaderReset(Loader<GenericLoaderResponse<T>> loader);

        @Override
        public void onLoadFinished(Loader<GenericLoaderResponse<T>> loader,
                GenericLoaderResponse<T> data);
    }

    public static interface LoaderCallbacksResetListener<T> {

        public void onLoaderReset(Loader<GenericLoaderResponse<T>> loader);
    }

    public static interface LoaderCallbacksFinishedListener<T> {

        public void onLoadFinished(Loader<GenericLoaderResponse<T>> loader,
                GenericLoaderResponse<T> data);
    }

    // Base implementation for a loader callback that uses a Generic Loader (the
    // onCreateLoader method implementation can be generalized)
    public static interface LoaderCallbacksBase<T> extends
            LoaderCallbacksResetListener<T>, LoaderCallbacksFinishedListener<T> {
    }

    // This interface makes a class a loaderCallbackBase and a DataLoader
    public static interface LoaderCallbacksHolder<T> extends
            LoaderCallbacksBase<T>, DataLoader<T> {
    }

    public static class GenericLoaderCallbacksImpl<T> implements
            LoaderCallbacks<T> {

        private DataLoader<T> mDataLoader;
        private LoaderCallbacksFinishedListener<T> mFinishedListener;
        private LoaderCallbacksResetListener<T> mResetListener;

        public GenericLoaderCallbacksImpl(DataLoader<T> dataLoader) {
            this.mDataLoader = dataLoader;
        }

        public GenericLoaderCallbacksImpl(DataLoader<T> dataLoader,
                LoaderCallbacksResetListener<T> resetListener,
                LoaderCallbacksFinishedListener<T> finishedListener) {

            this(dataLoader);
            this.mResetListener = resetListener;
            this.mFinishedListener = finishedListener;
        }

        @Override
        public Loader<GenericLoaderResponse<T>> onCreateLoader(int id,
                Bundle loader) {

            return new GenericLoader<T>(ToggApp.getApplication(), id,
                    (DataLoader<T>) mDataLoader, loader);
        }

        @Override
        public void onLoaderReset(Loader<GenericLoaderResponse<T>> loader) {
            if (mResetListener != null) {
                mResetListener.onLoaderReset(loader);
            }
        }

        @Override
        public void onLoadFinished(Loader<GenericLoaderResponse<T>> loader,
                GenericLoaderResponse<T> data) {
            if (mFinishedListener != null) {
                mFinishedListener.onLoadFinished(loader, data);
            }
        }
    }

    public static class LoaderCallbacksWrapper<T> implements LoaderCallbacks<T> {

        private LoaderCallbacksHolder<T> mLoaderCallbacks;

        public LoaderCallbacksWrapper(LoaderCallbacksHolder<T> loaderCallbacks) {
            this.mLoaderCallbacks = loaderCallbacks;
        }

        @Override
        public Loader<GenericLoaderResponse<T>> onCreateLoader(int id,
                Bundle loader) {

            return new GenericLoader<T>(ToggApp.getApplication(), id,
                    (DataLoader<T>) mLoaderCallbacks, loader);
        }

        @Override
        public void onLoaderReset(Loader<GenericLoaderResponse<T>> loader) {
            mLoaderCallbacks.onLoaderReset(loader);
        }

        @Override
        public void onLoadFinished(Loader<GenericLoaderResponse<T>> loader,
                GenericLoaderResponse<T> data) {
            mLoaderCallbacks.onLoadFinished(loader, data);
        }
    }

    public static interface DataLoader<T> {
        T loadInBackground(int flag, Bundle args) throws Exception;
    }

    public static final class GenericLoaderResponse<T> {
        private T data;
        private Exception error;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Exception getError() {
            return error;
        }

        public void setError(Exception error) {
            this.error = error;
        }

        public boolean hasError() {
            return error != null;
        }

        public boolean success() {
            return !hasError();
        }
    }
}
