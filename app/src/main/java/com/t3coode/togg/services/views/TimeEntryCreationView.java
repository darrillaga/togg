package com.t3coode.togg.services.views;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface TimeEntryCreationView {

    String getDescription();

    @JsonProperty(value = "wid")
    long getWorkspaceId();

    @JsonProperty(value = "pid")
    long getProjectId();

    @JsonProperty(value = "tid")
    long getTaskId();

    boolean isBillable();

    Date getStart();

    Date getStop();

    long getDuration();

    @JsonProperty(value = "created_with")
    String getCreatedWith();

    List<String> getTags();

    boolean isDuronly();

}
