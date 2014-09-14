package com.t3coode.togg.services.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskDTO extends IdentifiedBaseDTO {

    /** name: The name of the task (string, required, unique in project) */
    private String name;

    /** pid: project ID for the task (integer, required) */
    private long projectId;

    /**
     * wid: workspace ID, where the task will be saved (integer, project's
     * workspace id is used when not supplied)
     */
    private long workspaceId;

    /** Workspace id (wid) and project id (pid) can't be changed on update. */

    /** uid: user ID, to whom the task is assigned to (integer, not required) */
    private long userId;

    /**
     * estimated_seconds: estimated duration of task in seconds (integer, not
     * required)
     */
    private long estimatedSeconds;

    /** active: whether the task is done or not (boolean, by default true) */
    private boolean active;

    /**
     * at: timestamp that is sent in the response for PUT, indicates the time
     * task was last updated
     */
    private Date at;

    /**
     * Additional fields
     * 
     * It's possible to get additional info for the task. For that you have to
     * send the fields parameter in request with desired property names
     * separated by comma.
     */

    /**
     * done_seconds: duration (in seconds) of all the time entries registered
     * for this task
     */
    private long doneSeconds;

    /** uname: full name of the person to whom the task is assigned to */
    private String uname;

    @ToString
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value = "pid")
    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @JsonProperty(value = "wid")
    public long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(long workspaceId) {
        this.workspaceId = workspaceId;
    }

    @JsonProperty(value = "uid")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @JsonProperty(value = "estimated_seconds")
    public long getEstimatedSeconds() {
        return estimatedSeconds;
    }

    public void setEstimatedSeconds(long estimatedSeconds) {
        this.estimatedSeconds = estimatedSeconds;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    @JsonProperty(value = "done_seconds")
    public long getDoneSeconds() {
        return doneSeconds;
    }

    public void setDoneSeconds(long doneSeconds) {
        this.doneSeconds = doneSeconds;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

}
