package com.t3coode.togg.services.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ProjectUpdateView {

    String getName();

    @JsonProperty(value = "cid")
    long getClientId();

    boolean isActive();

    boolean isPrivate();

    boolean isTemplate();

    @JsonProperty(value = "template_id")
    long getTemplateId();

    boolean isBillable();

    @JsonProperty(value = "auto_estimates")
    boolean isAutoEstimates();

    @JsonProperty(value = "estimated_hours")
    int getEstimatedHours();

}
