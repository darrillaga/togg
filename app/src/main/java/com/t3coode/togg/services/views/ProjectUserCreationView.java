package com.t3coode.togg.services.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ProjectUserCreationView {

    @JsonProperty(value = "pid")
    long getProjectId();

    @JsonProperty(value = "uid")
    long getUserId();

    boolean isAmdin();

    float getRate();

}
