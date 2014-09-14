package com.t3coode.togg.services.dtos;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.t3coode.togg.services.views.ProjectCreationView;
import com.t3coode.togg.services.views.ProjectUpdateView;

public class ProjectDTO extends IdentifiedBaseDTO implements
        ProjectCreationView, ProjectUpdateView {

    /**
     * name: The name of the project (string, required, unique for client and
     * workspace)
     */
    private String name;

    /** wid: workspace ID, where the project will be saved (integer, required) */
    private long workspaceId;

    /** cid: client ID(integer, not required) */
    private long clientId;

    /**
     * active: whether the project is archived or not (boolean, by default true)
     */
    private boolean active = true;

    /**
     * is_private: whether project is accessible for only project users or for
     * all workspace users (boolean, default true)
     */
    private boolean isPrivate = true;

    /**
     * template: whether the project can be used as a template (boolean, not
     * required)
     */
    private boolean template;

    /**
     * template_id: id of the template project used on current project's
     * creation
     */
    private long templateId;

    /**
     * billable: whether the project is billable or not (boolean, default true,
     * available only for pro workspaces)
     */
    private boolean billable = true;

    /**
     * auto_estimates: whether the esitamated hours is calculated based on task
     * esimations or is fixed manually(boolean, default false, not required,
     * premium functionality)
     */
    private boolean autoEstimates;

    /**
     * estimated_hours: if auto_estimates is true then the sum of task
     * estimations is returned, otherwise user inserted hours (integer, not
     * required, premium functionality)
     */
    private int estimatedHours;

    /**
     * at: timestamp that is sent in the response for PUT, indicates the time
     * task was last updated
     */
    private Date at;

    private String color;

    /** Resources */
    private List<ProjectUserDTO> users;

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

    @JsonProperty(value = "cid")
    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    @JsonProperty(value = "template_id")
    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    @JsonProperty(value = "auto_estimates")
    public boolean isAutoEstimates() {
        return autoEstimates;
    }

    public void setAutoEstimates(boolean autoEstimates) {
        this.autoEstimates = autoEstimates;
    }

    @JsonProperty(value = "estimated_hours")
    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public List<ProjectUserDTO> getUsers() {
        return users;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setUsers(List<ProjectUserDTO> users) {
        this.users = users;
    }

}
