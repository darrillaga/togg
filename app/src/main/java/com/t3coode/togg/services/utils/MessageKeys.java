package com.t3coode.togg.services.utils;

public enum MessageKeys {
    // TODO add here keys for service messages
    INTERNAL_ERROR("internal_error"), 
    CONTENT_READ_ERROR("content_read_error"), 
    ERROR_CONTENT_READ_ERROR("error_content_read_error"),
    CONNECTION_ERROR("connection_error"),
    DATA_WRITE_ERROR("data_write_error");

    private String key;

    private MessageKeys(String key) {
        this.key = key;
    }

    public String value() {
        return key;
    }
}
