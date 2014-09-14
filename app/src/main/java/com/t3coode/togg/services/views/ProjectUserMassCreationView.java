package com.t3coode.togg.services.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ProjectUserMassCreationView {

    @JsonProperty(value = "pid")
    long getProjectId();

    boolean isAmdin();

    float getRate();

}
