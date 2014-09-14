package com.t3coode.togg.services.factories;

import com.t3coode.togg.services.TogglApiContext;

public class TogglApiContextFactory {

    // FIXME externalize FQN
    public static final String currentContext = "com.t3coode.togg.AndroidTogglApiContext";

    @SuppressWarnings("unchecked")
    public static TogglApiContext getContext()
            throws TogglContextInitializationException {
        Class<TogglApiContext> contextClass;
        try {
            contextClass = (Class<TogglApiContext>) Class
                    .forName(currentContext);
            return (TogglApiContext) contextClass.getDeclaredMethod(
                    "getInstance").invoke(null);
        } catch (Exception e) {
            throw new TogglContextInitializationException(
                    "Could not instantiate TogglContext.", e);
        }
    }

}