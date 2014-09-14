package com.t3coode.togg.services.factories;

public class TogglContextInitializationException extends RuntimeException {

    private static final long serialVersionUID = -7190617235549315473L;

    public TogglContextInitializationException() {
    }

    public TogglContextInitializationException(String detailMessage) {
        super(detailMessage);
    }

    public TogglContextInitializationException(Throwable throwable) {
        super(throwable);
    }

    public TogglContextInitializationException(String detailMessage,
            Throwable throwable) {
        super(detailMessage, throwable);
    }

}
