package com.t3coode.togg.activities.async;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;

public class AsyncLoaderTask<T> extends
        AsyncTask<LoaderTask<T>, Void, GenericLoaderResponse<T>> {

    private LoaderTask<T> mLoaderTask;
    private Map<String, Object> mParamsMap = new HashMap<String, Object>();

    public AsyncLoaderTask<T> setParamsMap(Map<String, Object> paramsMap) {
        if (paramsMap != null) {
            this.mParamsMap = paramsMap;
        }
        return this;
    }

    @Override
    protected GenericLoaderResponse<T> doInBackground(LoaderTask<T>... params) {
        mLoaderTask = params[0];

        GenericLoaderResponse<T> response = new GenericLoaderResponse<T>();

        try {
            T result = mLoaderTask.onExecute(mParamsMap);
            response.setData(result);

            if (!mParamsMap.containsKey(LoaderCallback.RESULT)) {
                mParamsMap.put(LoaderCallback.RESULT, result);
            }

        } catch (Exception e) {
            response.setError(e);
        }

        return response;
    }

    @Override
    protected void onPostExecute(GenericLoaderResponse<T> result) {
        if (mLoaderTask.awaitingTaskResult(mParamsMap)) {
            mLoaderTask.onPostExecute(result, mParamsMap);
        }
        super.onPostExecute(result);
    }
}
