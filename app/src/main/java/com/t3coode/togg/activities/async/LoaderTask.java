package com.t3coode.togg.activities.async;

import java.util.Map;

public interface LoaderTask<T extends Object> extends LoaderCallback<T> {

    T onExecute(Map<String, Object> params) throws Exception;

    boolean awaitingTaskResult(Map<String, Object> params);
}
