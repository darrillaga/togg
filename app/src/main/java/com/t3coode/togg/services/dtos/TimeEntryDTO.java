package com.t3coode.togg.services.dtos;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.t3coode.togg.services.views.Views;
import com.t3coode.togg.tracking.TimeTrackerController;

public class TimeEntryDTO extends IdentifiedBaseDTO {

    public TimeEntryDTO() {
        this.at = new Date();
    }

    /** description: (string, required) */
    @JsonView({ Views.Update.class, Views.Create.class })
    private String description = "";

    /** wid: workspace ID (integer, required if pid or tid not supplied) */
    private long workspaceId;

    /** pid: project ID (integer, not required) */
    @JsonView({ Views.Update.class, Views.Create.class })
    private Long projectId;

    /** tid: task ID (integer, not required) */
    @JsonView({ Views.Update.class, Views.Create.class })
    private long taskId;

    /**
     * billable: (boolean, not required, default false, available for pro
     * workspaces)
     */
    @JsonView({ Views.Update.class, Views.Create.class })
    private boolean billable;

    /** start: time entry start time (string, required, ISO 8601 date and time) */
    @JsonView({ Views.Update.class, Views.Create.class })
    private Date start;

    /**
     * stop: time entry stop time (string, not required, ISO 8601 date and time)
     */
    @JsonView({ Views.Update.class, Views.Create.class })
    private Date stop;

    /**
     * duration: time entry duration in seconds. If the time entry is currently
     * running, the duration attribute contains a negative value, denoting the
     * start of the time entry in seconds since epoch (Jan 1 1970). The correct
     * duration can be calculated as current_time + duration, where current_time
     * is the current time in seconds since epoch. (integer, required)
     */
    @JsonView({ Views.Update.class, Views.Create.class })
    private long duration;

    /** created_with: the name of your client app (string, required) */
    @JsonView(Views.Create.class)
    private String createdWith = IdentifiedBaseDTO.CREATED_WITH;

    /** tags: a list of tag names (array of strings, not required) */
    @JsonView({ Views.Update.class, Views.Create.class })
    private List<String> tags;

    /**
     * duronly: should Toggl show the start and stop time of this time entry?
     * (boolean, not required)
     */
    @JsonView({ Views.Update.class, Views.Create.class })
    private boolean duronly;

    @JsonView({ Views.Create.class })
    private String guid;

    /**
     * at: timestamp that is sent in the response, indicates the time item was
     * last updated
     */
    private Date at;

    @ToString
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty(value = "wid")
    public long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(long workspaceId) {
        this.workspaceId = workspaceId;
    }

    @JsonProperty(value = "pid")
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @JsonProperty(value = "tid")
    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @JsonProperty(value = "created_with")
    public String getCreatedWith() {
        return createdWith;
    }

    public void setCreatedWith(String createdWith) {
        this.createdWith = createdWith;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isDuronly() {
        return duronly;
    }

    public void setDuronly(boolean duronly) {
        this.duronly = duronly;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isPersisted() {
        return getId() != null;
    }

    public TimeEntryDTO resumeTimer(boolean isDuronly) {
        TimeEntryDTO newTimeEntry = this;

        if (isDuronly) {
            newTimeEntry.setDuration(TimeTrackerController
                    .getRunningTimeStart(newTimeEntry.getDuration()));
            newTimeEntry.setDuronly(true);
            newTimeEntry.setStop(null);
        } else {
            newTimeEntry = new TimeEntryDTO();
            newTimeEntry.setStart(new Date());
            newTimeEntry.setBillable(isBillable());
            newTimeEntry.setCreatedWith(getCreatedWith());
            newTimeEntry.setDescription(getDescription());
            newTimeEntry.setDuration(TimeTrackerController
                    .getRunningTimeStart(0));
            newTimeEntry.setProjectId(getProjectId());
            newTimeEntry.setTags(getTags().subList(0, getTags().size()));
            newTimeEntry.setTaskId(getTaskId());
            newTimeEntry.setWorkspaceId(getWorkspaceId());
        }

        return newTimeEntry;
    }

}
