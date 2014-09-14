package com.t3coode.togg.services.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ClientCreationView {

    @JsonProperty(value = "wid")
    long getWorkspaceId();

    String getName();

    String getNotes();

    int getHrate();

    String getCur();

}
