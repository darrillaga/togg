package com.t3coode.togg.services.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.t3coode.togg.services.views.ProjectUserMassCreationView;
import com.t3coode.togg.services.views.ProjectUserMassUpdateView;

public class ProjectUserDTO extends IdentifiedBaseDTO implements
        ProjectUserMassCreationView, ProjectUserMassUpdateView {

    /** pid: project ID (integer, required) */
    private long projectId;

    /** uid: user ID, who is added to the project (integer, required) */
    private long userId;

    /**
     * wid: workspace ID, where the project belongs to (integer, not-required,
     * project's workspace id is used)
     */
    private long workspaceId;

    /**
     * Workspace id (wid), project id (pid) and user id (uid) can't be changed
     * on update.
     */

    /** manager: admin rights for this project (boolean, default false) */
    private boolean amdin;

    /**
     * rate: hourly rate for the project user (float, not-required, only for pro
     * workspaces) in the currency of the project's client or in workspace
     * default currency.
     */
    private float rate;

    /**
     * at: timestamp that is sent in the response, indicates when the project
     * user was last updated
     */
    private Date at;

    /**
     * Additional fields It's possible to get user's fullname. For that you have
     * to send the fields parameter in request with desired property name.
     */

    /** fullname: full name of the user, who is added to the project */
    private String fullname;

    @JsonProperty(value = "pid")
    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @JsonProperty(value = "uid")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @JsonProperty(value = "wid")
    public long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public boolean isAmdin() {
        return amdin;
    }

    public void setAmdin(boolean amdin) {
        this.amdin = amdin;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

}
