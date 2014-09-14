package com.t3coode.togg.services;

public interface TogglApiContext {

    /**
     * Messages source for BcpApi
     * 
     * @param messageKey
     *            Represents a message
     */
    String getMessage(TogglApiMessage messageKey);

    /**
     * If you want to get a message for eg. represented by an enum, it must
     * implement this interface
     * */
    public static interface TogglApiMessage {
        String getMessage();
    }
}
