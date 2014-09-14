package com.t3coode.togg;

import java.lang.reflect.Field;

import com.t3coode.togg.services.TogglApiContext;

public class AndroidTogglApiContext implements TogglApiContext {

    private static TogglApiContext instance;

    @Override
    public String getMessage(TogglApiMessage messageKey) {
        try {
            Field field = R.string.class.getDeclaredField(messageKey
                    .getMessage());
            int messageId = field.getInt(null);
            return ToggApp.getApplication().getString(messageId);
        } catch (Exception e) {
            throw new IllegalStateException("Resource does not exists.", e);
        }
    }

    public static TogglApiContext getInstance() {
        if (instance == null) {
            synchronized (AndroidTogglApiContext.class) {
                if (instance == null) {
                    instance = new AndroidTogglApiContext();
                }
            }
        }
        return instance;
    }
}
