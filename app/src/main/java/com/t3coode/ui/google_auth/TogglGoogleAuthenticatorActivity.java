package com.t3coode.ui.google_auth;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.t3coode.togg.R;

public class TogglGoogleAuthenticatorActivity extends FragmentActivity {

    public static final String TAG = TogglGoogleAuthenticatorActivity.class
            .getName();
    public static final String TOGGL_GOOGLE_CREDENTIALS_RECEIVED_BROADCAST_ACTION = TogglGoogleAuthenticatorActivity.class
            .getName() + ":TOGGLE_GOOGLE_CREDENTIALS_RECEIVED_BROADCAST_ACTION";

    public static final String IS_SIGN_IN_KEY = TogglGoogleAuthenticatorActivity.class
            + "isSignInKey";

    private static final String SIGN_IN_OAUTH_URL = "https://accounts.google.com/o/oauth2/auth?"
            + "approval_prompt=auto&"
            + "redirect_uri=https%3A%2F%2Fwww.toggl.com&"
            + "access_type=&"
            + "client_id=426090949585-au9jodg0hqhc5l161evdrb667fsqf37n.apps.googleusercontent.com&"
            + "state=&"
            + "response_type=code&"
            + "scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email";

    private static final String SIGN_UP_OAUTH_URL = "https://accounts.google.com/o/oauth2/auth?"
            + "approval_prompt=auto&"
            + "redirect_uri=https%3A%2F%2Fwww.toggl.com&"
            + "access_type=&"
            + "client_id=426090949585-au9jodg0hqhc5l161evdrb667fsqf37n.apps.googleusercontent.com&"
            + "state=signup&"
            + "response_type=code&"
            + "scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email";

    private static final String JS_WEB_OBJECT_NAME = "toggWebObject";
    private static final String JAVASCRIPT_FUNCTION_URL = "javascript:"
            + "           function loadToggApiToken() {"
            + "             if (typeof toggl !== 'undefined' && typeof toggl.app !== 'undefined' && typeof "
            + JS_WEB_OBJECT_NAME
            + " !== 'undefined') {"
            + "               clearInterval(toggTokenIdWaiter);"
            + JS_WEB_OBJECT_NAME
            + ".setToken(toggl.app.currentUser.attributes.api_token);"
            + "               console.log('executed');"
            + "               console.log(JSON.stringify(toggl.app.currentUser.toJSON()));"
            + "             }"
            + "           };"
            + "           window.toggTokenIdWaiter = setInterval(loadToggApiToken, 100);";

    private static final String TOGGL_HOST = "www.toggl.com";

    private WebView mWebView;
    private View mPreloaderView;
    private String code;
    private boolean mProcessFinished;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_authenticator);

        this.mWebView = (WebView) findViewById(R.id.web_view);
        this.mPreloaderView = findViewById(R.id.preloader);
        mWebView.loadUrl(getIntent().getBooleanExtra(IS_SIGN_IN_KEY, true) ? SIGN_IN_OAUTH_URL
                : SIGN_UP_OAUTH_URL);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(false);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        cookieManager.setAcceptCookie(true);
        showPreloader();
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // if (url.contains(TOGGL_APP_PATH_FRAGMENT)) {

                Uri uri = Uri.parse(url);

                Log.d(TAG, "Inject");
                if (code == null) {
                    TogglGoogleAuthenticatorActivity.this.code = uri
                            .getQueryParameter("code");
                    if (code != null) {
                        Log.d(TAG, "OAuth code Obtained");
                    }
                }

                if (uri.getHost().equals(TOGGL_HOST)) {
                    mWebView.addJavascriptInterface(new TogglJSTokenReceiver(
                            TogglGoogleAuthenticatorActivity.this, mWebView),
                            JS_WEB_OBJECT_NAME);
                }
                // }
                Log.d(TAG, "Load " + url);
                view.loadUrl(url);

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showPreloader();
                    }
                });
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Finished " + url);

                Uri uri = Uri.parse(url);

                if (uri.getHost().equals(TOGGL_HOST)) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mWebView.evaluateJavascript(
                                        JAVASCRIPT_FUNCTION_URL, null);

                            }
                        });
                    } else {
                        mWebView.loadUrl(JAVASCRIPT_FUNCTION_URL);
                        Log.d(TAG, "Load Js");
                    }
                }

                if (!uri.getHost().equals(TOGGL_HOST) && code == null) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            hidePreloader();
                        }
                    });

                }
            }
        });
    }

    private void showPreloader() {
        mPreloaderView.setVisibility(View.VISIBLE);
    }

    private void hidePreloader() {
        mPreloaderView.setVisibility(View.GONE);
    }

    /**
     * Called when the OAuthRequestTokenTask finishes (user has authorized the
     * request token). The callback URL will be intercepted here.
     */

    public static class RetrieveAccessTokenTask extends
            AsyncTask<String, Void, Void> {

        private Context context;
        private SharedPreferences prefs;

        public RetrieveAccessTokenTask(Context context, SharedPreferences prefs) {
            this.context = context;
            this.prefs = prefs;
        }

        /**
         * Retrieve the oauth_verifier, and store the oauth and
         * oauth_token_secret for future API calls.
         */
        @Override
        protected Void doInBackground(String... params) {
            final String token = params[0];

            try {

                final Editor edit = prefs.edit();

                if (StringUtils.isNotEmpty(token)) {
                    edit.putString(
                            TogglGoogleAuthHelper.TOGGL_GOOGLE_OAUTH_CODE,
                            token);
                } else {
                    throw new Exception();
                }

                edit.commit();

                Log.e(TogglGoogleAuthenticatorActivity.TAG,
                        "OAuth - Access Token Retrieved");

            } catch (Exception e) {
                Log.e(TogglGoogleAuthenticatorActivity.TAG,
                        "OAuth - Access Token Retrieval Error", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            LocalBroadcastManager
                    .getInstance(context)
                    .sendBroadcastSync(
                            new Intent(
                                    TOGGL_GOOGLE_CREDENTIALS_RECEIVED_BROADCAST_ACTION));
            super.onPostExecute(result);
        }
    }

    public void setProcessFinished(boolean processFinished) {
        this.mProcessFinished = processFinished;

    }

    public boolean isProcessFinished() {
        return mProcessFinished;
    }

    private static final class TogglJSTokenReceiver {

        private TogglGoogleAuthenticatorActivity mActivity;
        private WebView mWebView;

        public TogglJSTokenReceiver(TogglGoogleAuthenticatorActivity activity,
                WebView webView) {
            this.mActivity = activity;
            this.mWebView = webView;
        }

        @JavascriptInterface
        public void setToken(final String token) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (!mActivity.isProcessFinished()) {
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(mActivity);
                        mWebView.removeJavascriptInterface(JS_WEB_OBJECT_NAME);
                        new RetrieveAccessTokenTask(mActivity, prefs) {
                            protected void onPostExecute(Void result) {

                                mActivity.setProcessFinished(true);
                                super.onPostExecute(result);
                                mActivity.finish();

                            };
                        }.execute(token);
                    }
                }
            });
        }
    }

}
