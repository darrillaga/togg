package com.t3coode.togg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.t3coode.togg.R;
import com.t3coode.togg.activities.fragments.UserSignInFragment;
import com.t3coode.togg.activities.fragments.UserSignUpFragment;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.ui.AnalyticsActivity;

public class LoginActivity extends AnalyticsActivity implements
        UserSignInProvider {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.form_container, new UserSignInFragment())
                .commit();

        super.onPostCreate(savedInstanceState);
    }

    public static interface UserProvider {
        UserDTO getUser();
    }

    @Override
    public void onSignInClick(View v) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.form_container, new UserSignInFragment())
                .commit();

    }

    @Override
    public void onGoogleSignInClick(View v) {
    }

    @Override
    public void onSignUpClick(View v) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.form_container, new UserSignUpFragment())
                .commit();

    }

    @Override
    public void onLoggedIn(UserDTO user) {
        startActivity(new Intent(this, TimeEntriesActivity.class));
        finish();
    }
}
