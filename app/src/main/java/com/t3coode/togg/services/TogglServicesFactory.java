package com.t3coode.togg.services;

import com.t3coode.togg.services.implementations.TogglServicesImpl;

public class TogglServicesFactory {

    public static TogglServices getToggleServices() {
        return TogglServicesImpl.getInstance();
    }

}
