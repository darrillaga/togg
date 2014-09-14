package com.t3coode.togg.services.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.t3coode.togg.services.utils.Jsonable;

public interface TagCreationView extends Jsonable {

    String getName();

    @JsonProperty(value = "wid")
    long getWorkspaceId();
}
