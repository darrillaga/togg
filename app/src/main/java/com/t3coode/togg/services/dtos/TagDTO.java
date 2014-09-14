package com.t3coode.togg.services.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.t3coode.togg.services.views.TagCreationView;
import com.t3coode.togg.services.views.TagUpdateView;

public class TagDTO extends IdentifiedBaseDTO implements TagCreationView,
        TagUpdateView {

    /** name: The name of the tag (string, required, unique in workspace) */
    private String name;

    /** wid: workspace ID, where the tag will be used (integer, required) */
    private long workspaceId;

    @ToString
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value = "wid")
    public long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(long workspaceId) {
        this.workspaceId = workspaceId;
    }
}
