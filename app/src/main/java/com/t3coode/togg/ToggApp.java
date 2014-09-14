package com.t3coode.togg;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.deploygate.sdk.DeployGate;
import com.t3coode.togg.activities.fragments.SettingsPreferencesFragment;
import com.t3coode.togg.activities.services.TimeNotificationService;
import com.t3coode.togg.activities.services.TimeTrackingService;
import com.t3coode.togg.activities.services.TimeTrackingService.TimeTrackingBinder;
import com.t3coode.togg.activities.services.UserUpdateBroadcastReceiver;
import com.t3coode.togg.services.TogglServices;
import com.t3coode.togg.services.dtos.BaseDTO;
import com.t3coode.togg.services.dtos.BaseDTO.DTOObserver;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.togg.services.implementations.TogglServicesImpl;
import com.t3coode.togg.services.routes.Routes;
import com.t3coode.togg.services.utils.NullAwareBeanUtils;

public class ToggApp extends Application implements UserObserver, DTOObserver,
        UserUpdateBroadcastReceiver.ReceiverListener {

    private static final String USER_KEY = "userKey";
    private static final String EMPTY_JSON = "{}";

    private static ToggApp togglApp;
    private TogglServices mTogglServices;
    private UserDTO mCurrentUser;
    private Map<String, Object> tempObjects = new HashMap<String, Object>();
    private CacheManager cacheManager;
    private TimeTrackingService mTimeTrackingService;
    private boolean mTimeTrackingServiceBound;
    private UserUpdateBroadcastReceiver mUserUpdateBroadcastReceiver;
    private OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            if (key.equals(SettingsPreferencesFragment.CONTINUOUS_TIMER_NOTIFICATION_AVAILABLE_KEY)) {
                checkContinuousTimerNotificationAvailability(sharedPreferences
                        .getBoolean(
                                SettingsPreferencesFragment.CONTINUOUS_TIMER_NOTIFICATION_AVAILABLE_KEY,
                                true));
            }
        }
    };

    private ServiceConnection mTimeTrackingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ToggApp.this.mTimeTrackingService = null;
            ToggApp.this.mTimeTrackingServiceBound = false;
            unregisterReceiver(mUserUpdateBroadcastReceiver);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ToggApp.this.mTimeTrackingService = ((TimeTrackingBinder) service)
                    .getService();

            if (mCurrentUser != null && mCurrentUser.getApiToken() != null) {
                mTimeTrackingService.startWebSocketClient();
            }

            IntentFilter intentFilter = new IntentFilter(
                    TimeTrackingService.BROADCAST_USER_UPDATE);
            registerReceiver(mUserUpdateBroadcastReceiver, intentFilter);

            ToggApp.this.mTimeTrackingServiceBound = true;

            setUpContinuousNotificationChecker();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        DeployGate.install(this);

        togglApp = this;

        cacheManager = CacheManager.getInstance();
        initLogin();

        Routes.build(getApplicationContext().getString(
                R.string.api_service_path));

        TogglServicesImpl.build(mCurrentUser);

        this.mTogglServices = TogglServicesImpl.getInstance();

        Intent i = new Intent(this, TimeTrackingService.class);
        // startService(i);

        this.mUserUpdateBroadcastReceiver = new UserUpdateBroadcastReceiver(
                this);

        bindService(i, mTimeTrackingServiceConnection, Service.BIND_AUTO_CREATE);

        setUpContinuousNotificationChecker();
    }

    public void initLogin() {
        mCurrentUser = new UserDTO();
        mCurrentUser.registerDTOObserver(this);
        setUpContinuousNotificationChecker();
        loadUserPreferences();
    }

    public static ToggApp getApplication() {
        return togglApp;
    }

    public UserDTO getCurrentUser() {
        return mCurrentUser;
    }

    public TogglServices getTogglServices() {
        return mTogglServices;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Map<String, Object> getCachedResources() {
        return cacheManager.getResources();
    }

    public boolean hasTempObject(String key) {
        return tempObjects.get(key) != null;
    }

    public Object getTempObject(String key) {
        Object o = null;
        o = tempObjects.get(key);
        tempObjects.remove(key);
        return o;
    }

    public void addTempObject(String key, Object object) {
        tempObjects.put(key, object);
    }

    public boolean userIsLoggedIn() {
        return mCurrentUser.getApiToken() != null;
    }

    @Override
    public void onUserChanged() {
        saveUserPreferences();
        setUpContinuousNotificationChecker();
    }

    private void loadUserPreferences() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        mCurrentUser.loadFromJSON(preferences.getString(USER_KEY, EMPTY_JSON));
    }

    private void saveUserPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(USER_KEY, mCurrentUser.toJSON()).commit();
    }

    private void clearUserPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(USER_KEY, EMPTY_JSON).commit();
    }

    public void resetUser() {
        clearUserPreferences();
        initLogin();
        TogglServicesImpl.getInstance().setCurrentUser(mCurrentUser);
    }

    @Override
    public void notifyDTOChanged(BaseDTO object, String fieldName) {
        if (object instanceof UserDTO
                && ((UserDTO) object).getApiToken() != null
                & mTimeTrackingService != null) {
            mTimeTrackingService.startWebSocketClient();
        }
    }

    @Override
    public boolean isListenToUser(long id) {
        return mCurrentUser != null && mCurrentUser.getId() == id;
    }

    @Override
    public void onUpdateUser(UserDTO user) {
        try {
            NullAwareBeanUtils.getInstance().copyProperties(mCurrentUser, user);
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        onUserChanged();
    }

    private void setUpContinuousNotificationChecker() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        preferences
                .registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        checkContinuousTimerNotificationAvailability(preferences
                .getBoolean(
                        SettingsPreferencesFragment.CONTINUOUS_TIMER_NOTIFICATION_AVAILABLE_KEY,
                        true));
    }

    public void checkContinuousTimerNotificationAvailability(boolean available) {
        Intent i = new Intent(this, TimeNotificationService.class);
        if (available && userIsLoggedIn()) {
            startService(i);
        } else {
            stopService(i);
        }
    }
}
