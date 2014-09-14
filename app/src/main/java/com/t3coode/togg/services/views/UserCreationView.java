package com.t3coode.togg.services.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface UserCreationView {

    String getEmail();

    public String getPassword();

    public String getTimezone();

    @JsonProperty(value = "created_with")
    public String getCreatedWith();

}
