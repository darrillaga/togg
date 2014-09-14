package com.t3coode.togg.activities.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.view.View;

import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.async.GenericLoader.DataLoader;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderCallbacksImpl;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;
import com.t3coode.togg.activities.async.GenericLoader.LoaderCallbacksFinishedListener;
import com.t3coode.togg.activities.utils.Resources.UrlDrawable;
import com.t3coode.togg.services.TogglApiResponseException;

public class Resources implements LoaderCallbacksFinishedListener<UrlDrawable>,
        DataLoader<UrlDrawable> {

    private static Resources mInstance;

    public static Resources getInstance() {
        if (mInstance == null) {
            mInstance = new Resources();
        }
        return mInstance;
    }

    private final Map<String, List<ResourceCallback>> binder = new HashMap<String, List<ResourceCallback>>();
    private FragmentActivity mCurrentContext;

    private Resources() {
    };

    public Drawable loadAsync(ResourceCallback callback, String url) {
        return loadAsync(callback, url, false);
    }

    public Drawable loadAsync(ResourceCallback callback, String url,
            boolean force) {
        Map<String, Object> resources = ToggApp.getApplication()
                .getCachedResources();
        if (resources.containsKey(url) && !force) {
            removeBindIfExists(url, callback);
            return (Drawable) resources.get(url);
        } else {
            mCurrentContext = callback.getActivity();
            Bundle b = new Bundle();
            b.putString("url", url);
            mCurrentContext.getSupportLoaderManager().restartLoader(
                    url.hashCode(),
                    b,
                    new GenericLoaderCallbacksImpl<UrlDrawable>(this, null,
                            this));
            bindToUrl(url, callback);
        }
        return null;
    }

    public synchronized void removeBindIfExists(String url,
            ResourceCallback callback) {
        List<ResourceCallback> binded = binder.get(url);
        if (binded != null && binded.contains(callback)) {
            binded.remove(callback);
        }

        if (binded != null && binded.isEmpty()) {
            binder.remove(binded);
            Loader<?> loader = mCurrentContext.getSupportLoaderManager()
                    .getLoader(url.hashCode());
            if (loader != null) {
                loader.abandon();
            }
        }
    }

    private synchronized void bindToUrl(String url, ResourceCallback callback) {
        List<ResourceCallback> binded = binder.get(url);
        if (binded == null) {
            binded = new ArrayList<ResourceCallback>();
            binder.put(url, binded);
        }
        if (!binded.contains(callback)) {
            binded.add(callback);
        }
    }

    @Override
    public UrlDrawable loadInBackground(int flag, Bundle args) {
        String url = args.getString("url");
        Drawable d = null;

        try {
            d = new BitmapDrawable(mCurrentContext.getResources(), ToggApp
                    .getApplication().getTogglServices().getData(url));
        } catch (TogglApiResponseException e) {
            e.printStackTrace();
        }

        ToggApp.getApplication().getCachedResources().put(url, d);

        return new UrlDrawable(url, d);
    }

    @Override
    public void onLoadFinished(
            Loader<GenericLoaderResponse<UrlDrawable>> loader,
            GenericLoaderResponse<UrlDrawable> data) {

        UrlDrawable urlDrawable = data.getData();

        if (data.success()) {

            List<ResourceCallback> binded = binder.get(urlDrawable.url);

            if (binded != null) {
                Iterator<ResourceCallback> it = binded.iterator();
                while (it.hasNext()) {
                    it.next().onLoadedResource(urlDrawable.url,
                            urlDrawable.drawable);
                    it.remove();
                }
                binder.remove(urlDrawable.url);
            }
        }

    }

    public static interface ResourceCallback {
        void onLoadedResource(String url, Drawable resource);

        FragmentActivity getActivity();
    }

    public static class UrlDrawable {
        private Drawable drawable;
        private String url;

        public UrlDrawable(String url, Drawable drawable) {
            this.url = url;
            this.drawable = drawable;
        }
    }

    public static Integer getIdFromResource(Class<?> clazz, String name) {
        return getIdFromResource(clazz, name, null);
    }

    public static Integer getIdFromResource(Class<?> clazz, String name,
            Integer defaultId) {
        Integer resourceId = null;
        Field f;
        try {
            f = clazz.getDeclaredField(name);
            resourceId = (Integer) f.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            resourceId = defaultId;
        }
        return resourceId;
    }

    public static void setBackgroundKeepingPadding(View item, int resource) {
        if (item != null) {
            int paddingTop = item.getPaddingTop();
            int paddingLeft = item.getPaddingLeft();
            int paddingRight = item.getPaddingRight();
            int paddingBottom = item.getPaddingBottom();
            item.setBackgroundResource(resource);
            item.setPadding(paddingLeft, paddingTop, paddingRight,
                    paddingBottom);
        }
    }

    public static int[] loadResrourceColorTypedArray(Context context,
            int arrayId, int defaultColorId) {
        TypedArray typedArray = context.getResources()
                .obtainTypedArray(arrayId);

        int[] array = new int[typedArray.length()];

        for (int index = 0; index < typedArray.length(); index++) {
            array[index] = typedArray.getColor(index, context.getResources()
                    .getColor(defaultColorId));
        }

        typedArray.recycle();

        return array;
    }

}