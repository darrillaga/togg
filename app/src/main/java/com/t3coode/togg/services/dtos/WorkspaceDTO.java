package com.t3coode.togg.services.dtos;

import java.util.Date;
import java.util.List;

public class WorkspaceDTO extends BaseDTO {

    /** name: (string, required) */
    private String name;

    /**
     * premium: If it's a pro workspace or not. Shows if someone is paying for
     * the workspace or not (boolean, not required)
     */
    private boolean premium;

    /**
     * at: timestamp that is sent in the response, indicates the time item was
     * last updated
     */
    private Date at;

    /** Resources */
    private List<UserDTO> users;
    private List<WorkspaceUserDTO> workspaceUsers;
    private List<ClientDTO> clients;
    private List<TaskDTO> tasks;
    private List<ProjectDTO> projects;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public List<ClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clients = clients;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    public List<WorkspaceUserDTO> getWorkspaceUsers() {
        return workspaceUsers;
    }

    public void setWorkspaceUsers(List<WorkspaceUserDTO> workspaceUsers) {
        this.workspaceUsers = workspaceUsers;
    }

}
