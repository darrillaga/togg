package com.t3coode.togg.activities.fragments;

import java.util.TimeZone;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.ErrorHandler;
import com.t3coode.togg.activities.LoginActivity.UserProvider;
import com.t3coode.togg.activities.UserSignInProvider;
import com.t3coode.togg.activities.async.GenericLoader.DataLoader;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderCallbacksImpl;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;
import com.t3coode.togg.activities.async.GenericLoader.LoaderCallbacksFinishedListener;
import com.t3coode.togg.activities.validators.EditTextValidator;
import com.t3coode.togg.activities.validators.ValidatorsRunner;
import com.t3coode.togg.services.TogglServicesFactory;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.ui.ProgressButton;
import com.t3coode.ui.google_auth.TogglGoogleAuthHelper;
import com.t3coode.ui.google_auth.TogglGoogleAuthHelper.TogglGoogleCredentialsCallback;
import com.t3coode.ui.google_auth.ToggleCredentials;

public class UserSignUpFragment extends BaseFragment implements UserProvider,
        LoaderCallbacksFinishedListener<UserDTO>, DataLoader<UserDTO>,
        TogglGoogleCredentialsCallback {

    private UserDTO mUser;
    private ValidatorsRunner mValidations;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirmation;
    private TogglGoogleAuthHelper mHelper;

    private ProgressButton mEnter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mUser = ToggApp.getApplication().getCurrentUser();

        mValidations = new ValidatorsRunner(getActivity());

        mEmail = (EditText) getView().findViewById(R.id.email);
        mPassword = (EditText) getView().findViewById(R.id.password);
        mPasswordConfirmation = (EditText) getView().findViewById(
                R.id.password_confirmation);

        mValidations
                .addValidation(new EditTextValidator(
                        "email",
                        getString(R.string.email),
                        mEmail,
                        new EditTextValidator.ValidationTypes[] { EditTextValidator.ValidationTypes.TEXT_IS_NOT_EMPTY }));

        mValidations.addValidation(new EditTextValidator("password",
                getString(R.string.password), new EditText[] { mPassword,
                        mPasswordConfirmation },
                new EditTextValidator.ValidationTypes[] {
                        EditTextValidator.ValidationTypes.TEXT_IS_NOT_EMPTY,
                        EditTextValidator.ValidationTypes.TEXT_IS_EQUAL }));

        getView().findViewById(R.id.button_sign_in).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((UserSignInProvider) getActivity()).onSignInClick(v);

                    }
                });

        getView().findViewById(R.id.button_google_sign_up).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        mEnter.showPreloader();
                        UserSignUpFragment.this.mHelper = new TogglGoogleAuthHelper(
                                getActivity());
                        mHelper.setSignUp(true);
                        mHelper.clearCredentials();
                        TogglGoogleAuthHelper.getTogglGoogleCredentials(
                                UserSignUpFragment.this, mHelper,
                                UserSignUpFragment.this);

                        if (mHelper.getCredentials() != null) {
                            onGoogleSignedUp();
                        }

                    }
                });

        mEnter = (ProgressButton) getView().findViewById(R.id.enter);
        mEnter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onEnterClick(v);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void onEnterClick(View v) {
        if (mValidations.validateAll()) {
            onLogginIn();
            mUser.setEmail(mEmail.getText().toString());

            mUser.setPassword(mPassword.getText().toString());

            mUser.setTimezone(TimeZone.getDefault().getID());

            startLogIn();
        }
    }

    @Override
    public UserDTO getUser() {
        return mUser;
    }

    @Override
    public void onResume() {
        mValidations.stopMessages();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mHelper != null) {
            mHelper.recycle(this);
            this.mHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public void onTogglGoogleCredentialsReceived() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                onGoogleSignedUp();
            }
        });
    }

    @Override
    public void onError(Exception exception) {
        // TODO Auto-generated method stub

    }

    private void onGoogleSignedUp() {
        ToggleCredentials credentials = mHelper.getCredentials();
        mUser.setApiToken(credentials.getApiToken());
        startLogIn();
    }

    private void onLogginIn() {
        mEnter.showPreloader();
        getView().findViewById(R.id.options_wrapper).setVisibility(View.GONE);
    }

    private void onFinishedLogIn() {
        mEnter.hidePreloader();
        getView().findViewById(R.id.options_wrapper)
                .setVisibility(View.VISIBLE);
    }

    private void startLogIn() {
        getLoaderManager().restartLoader(this.hashCode(), null,
                new GenericLoaderCallbacksImpl<UserDTO>(this, null, this));
    }

    @Override
    public UserDTO loadInBackground(int flag, Bundle args) throws Exception {
        if (mUser.getApiToken() != null) {
            return TogglServicesFactory.getToggleServices().manageUsers()
                    .me(true, null);
        } else {
            return TogglServicesFactory.getToggleServices().manageUsers()
                    .signups(mUser);
        }
    }

    @Override
    public void onLoadFinished(Loader<GenericLoaderResponse<UserDTO>> loader,
            GenericLoaderResponse<UserDTO> data) {

        if (data.success()) {
            ((UserSignInProvider) getActivity()).onLoggedIn(mUser);
        } else {
            onFinishedLogIn();
            ErrorHandler.handle(getActivity(), data.getError());
        }
    }

}
