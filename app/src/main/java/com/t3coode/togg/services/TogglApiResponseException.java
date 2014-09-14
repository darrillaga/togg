package com.t3coode.togg.services;

import android.util.Log;

import com.t3coode.togg.services.dtos.ErrorWrapperDTO;

public class TogglApiResponseException extends Exception {

    public static final String TAG = "TogglApiResponseException";

    private static final long serialVersionUID = -5858303910602747087L;
    private String errorCode;
    // TODO review errors;
    private ErrorWrapperDTO error;

    public TogglApiResponseException() {
        super();
        logError();
    }

    public TogglApiResponseException(String message, Throwable cause) {
        super(message, cause);
        logError();
    }

    public TogglApiResponseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        logError();
    }

    public TogglApiResponseException(ErrorWrapperDTO error) {
        super("Api error message");
        this.error = error;
        logError();
    }

    public ErrorWrapperDTO getError() {
        return error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    private void logError() {
        String errorMsg = "";
        if (getErrorCode() != null) {
            errorMsg += "Error Code: " + getErrorCode() + ", ";
        }
        if (getMessage() != null) {
            errorMsg += "Error Message: " + getMessage() + ", ";
        }

        if (getCause() != null) {
            errorMsg += " Cause: " + getCause().getMessage();
        }

        if (errorMsg != "") {
            Log.w(TAG, errorMsg);
        } else {
            Log.w(TAG, this);
        }

    }
}
