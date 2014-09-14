package com.t3coode.togg.activities.async;

import java.util.Map;

import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;

public interface LoaderCallback<T> {

    public static final String TASK_FLAG = "taskFlag";
    public static String CALLBACK = "callbackParam";
    public static final String RESULT = "paramsResult";

    void onPostExecute(GenericLoaderResponse<T> result,
            Map<String, Object> params);
}
