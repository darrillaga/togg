package com.t3coode.ui.google_auth;

import org.apache.commons.lang.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

public class TogglGoogleAuthHelper {

    public static final String TOGGL_GOOGLE_OAUTH_CODE = TogglGoogleAuthHelper.class
            .getName() + ":TogglGoogleOAuthCode";

    public static final int GOOGLE_REQUEST_CODE = 893;

    public static TogglGoogleAuthHelper getTogglGoogleCredentials(
            Fragment fragment, TogglGoogleAuthHelper helper,
            final TogglGoogleCredentialsCallback callback) {

        helper.setFragment(fragment);

        helper.setCallback(callback);

        if (helper.getCredentials() == null) {
            helper.setCredentials(helper.retreiveTogglGoogleCredentials());
        }

        if (helper.getCredentials() == null) {
            Intent i = new Intent(fragment.getActivity(),
                    TogglGoogleAuthenticatorActivity.class);
            i.putExtra(TogglGoogleAuthenticatorActivity.IS_SIGN_IN_KEY,
                    !helper.isSignUp());
            fragment.startActivity(i);
        }

        return helper;
    }

    public static interface TogglGoogleCredentialsCallback {
        void onTogglGoogleCredentialsReceived();

        void onError(Exception exception);
    }

    private ToggleCredentials credentials;
    private Fragment fragment;
    private BroadcastReceiver receiver;
    private TogglGoogleCredentialsCallback callback;
    private Context context;
    private boolean resumed;
    private boolean isSignUp;

    public TogglGoogleAuthHelper(Context context) {
        this.context = context;
        this.credentials = retreiveTogglGoogleCredentials();
    }

    public boolean isSignUp() {
        return isSignUp;
    }

    public void setSignUp(boolean isSignUp) {
        this.isSignUp = isSignUp;
    }

    public ToggleCredentials getCredentials() {
        if (credentials == null) {
            this.credentials = retreiveTogglGoogleCredentials();
        }
        return credentials;
    }

    public void setCredentials(ToggleCredentials credentials) {
        this.credentials = credentials;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(BroadcastReceiver receiver) {
        this.receiver = receiver;
    }

    public TogglGoogleCredentialsCallback getCallback() {
        return callback;
    }

    public void setCallback(final TogglGoogleCredentialsCallback callback) {

        clearReceiver();

        setReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                callback.onTogglGoogleCredentialsReceived();
            }
        });

        startReceiver();

        this.callback = callback;
    }

    public void onResume() {
        this.resumed = true;
    }

    public void onPause() {
        this.resumed = false;
    }

    public void onDestroy() {
        clearReceiver();
    }

    public boolean isResumed() {
        return resumed;
    }

    private ToggleCredentials retreiveTogglGoogleCredentials() {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String apiToken = preferences.getString(TOGGL_GOOGLE_OAUTH_CODE, null);

        ToggleCredentials session = null;

        if (StringUtils.isNotEmpty(apiToken)) {
            session = new ToggleCredentials();
            session.setApiToken(apiToken);
        }

        return session;

    }

    public void recycle(Fragment fragment) {
        clearReceiver();
    }

    private void startReceiver() {
        clearReceiver();
        if (fragment != null && receiver != null) {
            LocalBroadcastManager
                    .getInstance(fragment.getActivity())
                    .registerReceiver(
                            getReceiver(),
                            new IntentFilter(
                                    TogglGoogleAuthenticatorActivity.TOGGL_GOOGLE_CREDENTIALS_RECEIVED_BROADCAST_ACTION));
        }
    }

    private void clearReceiver() {
        try {
            if (fragment != null && receiver != null) {
                LocalBroadcastManager.getInstance(fragment.getActivity())
                        .unregisterReceiver(receiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void clearCredentials() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();

        editor.remove(TOGGL_GOOGLE_OAUTH_CODE);
        editor.commit();
        this.credentials = null;
    }

}
