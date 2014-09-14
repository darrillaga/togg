package com.t3coode.togg.activities.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;

public class SettingsPreferencesFragment extends BaseFragment {

    SharedPreferences mPreferences;

    public static final String CONTINUOUS_TIMER_NOTIFICATION_AVAILABLE_KEY = "continuousTimerNotificationAvailable";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.mPreferences = PreferenceManager
                .getDefaultSharedPreferences(ToggApp.getApplication());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_preference,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpBindings();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpBindings() {
        setUpContinuousTimerNotificationAvailableSetting();
    }

    private void setUpContinuousTimerNotificationAvailableSetting() {
        final CheckBox continuousTimerNotificationAvailableCB = (CheckBox) getView()
                .findViewById(
                        R.id.settings_continuous_timer_notification_available);

        continuousTimerNotificationAvailableCB.setChecked(mPreferences
                .getBoolean(CONTINUOUS_TIMER_NOTIFICATION_AVAILABLE_KEY, true));

        continuousTimerNotificationAvailableCB
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        mPreferences
                                .edit()
                                .putBoolean(
                                        CONTINUOUS_TIMER_NOTIFICATION_AVAILABLE_KEY,
                                        isChecked).commit();

                        ToggApp.getApplication()
                                .checkContinuousTimerNotificationAvailability(
                                        isChecked);
                    }
                });

        getView().findViewById(
                R.id.settings_continuous_timer_notification_available_wrapper)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        continuousTimerNotificationAvailableCB
                                .setChecked(!continuousTimerNotificationAvailableCB
                                        .isChecked());

                    }
                });
    }
}
