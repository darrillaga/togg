package com.t3coode.togg.activities;

import android.view.View;

import com.t3coode.togg.services.dtos.UserDTO;

public interface UserSignInProvider {
    void onSignInClick(View v);

    void onGoogleSignInClick(View v);

    void onSignUpClick(View v);

    void onLoggedIn(UserDTO user);
}