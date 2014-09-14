package com.t3coode.togg.services.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkspaceUserDTO extends IdentifiedBaseDTO {

    /** id: workspace user id (integer) */

    /** uid: user id of the workspace user (integer) */
    private long userId;

    /** admin: if user is workspace admin (boolean) */
    private boolean admin;

    /**
     * active: if the workspace user has accepted the invitation to this
     * workspace (boolean)
     */
    private boolean active;

    /**
     * invite_url: if user has not accepted the invitation the url for accepting
     * his/her invitation is sent when the request is made by workspace_admin
     */
    private String inviteUrl;

    @JsonProperty(value = "uid")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @JsonProperty(value = "invite_url")
    public String getInviteUrl() {
        return inviteUrl;
    }

    public void setInviteUrl(String inviteUrl) {
        this.inviteUrl = inviteUrl;
    }

}
