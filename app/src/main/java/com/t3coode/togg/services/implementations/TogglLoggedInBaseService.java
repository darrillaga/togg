package com.t3coode.togg.services.implementations;

import com.t3coode.togg.services.TogglBaseService;
import com.t3coode.togg.services.dtos.UserDTO;

public class TogglLoggedInBaseService extends TogglBaseService {

    @Override
    protected UserDTO getCurrentUser() {
        return TogglServicesImpl.getInstance().getCurrentUser();
    }

}
