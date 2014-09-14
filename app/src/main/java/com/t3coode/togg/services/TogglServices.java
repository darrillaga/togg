package com.t3coode.togg.services;

import java.io.InputStream;
import java.util.List;

import com.t3coode.togg.services.dtos.UserDTO;

public interface TogglServices {

    /**
     * /users namespace is derived to Users manager
     * 
     * @return an implementation of TogglUsers
     */
    TogglUsers manageUsers();

    /**
     * /clients namespace is derived to Clients manager
     * 
     * @return an implementation of TogglClients
     */
    TogglClients manageClients();

    /**
     * /projects namespace is derived to Projects manager
     * 
     * @return an implementation of TogglProjects
     */
    TogglProjects manageProjects();

    /**
     * /project_users namespace is derived to ProjectUsers manager
     * 
     * @return an implementation of TogglProjectUsers
     */
    TogglProjectUsers manageProjectUsers();

    /**
     * /tags namespace is derived to Tags manager
     * 
     * @return an implementation of TogglTagsUsers
     */
    TogglTags manageTags();

    /**
     * /tasks namespace is derived to Tasks manager
     * 
     * @return an implementation of TogglTasksUsers
     */
    TogglTasks manageTasks();

    /**
     * /time_entries namespace is derived to TimeEntries manager
     * 
     * @return an implementation of TogglTimeEntries
     */
    TogglTimeEntries manageTimeEntries();

    /**
     * /workspaces namespace is derived to Workspaces manager
     * 
     * @return an implementation of TogglWorkspaces
     */
    TogglWorkspaces manageWrokspaceUsers();

    /**
     * /workspace_users namespace is derived to WorkspaceUsers manager
     * 
     * @return an implementation of TogglWorkspaceUsers
     */
    TogglWorkspaceUsers manageWorkspaces();

    /**
     * Returns the login data
     * 
     * @return a Login object
     */
    UserDTO getCurrentUser();

    void setCurrentUser(UserDTO user);

    /**
     * Returns a list of timezones
     * 
     * @return a List of String
     */
    List<String> getTimezones() throws TogglApiResponseException;

    /**
     * Checks the login authentication
     * 
     * @param userName
     * @param password
     * @param userhost
     * 
     */
    void performLogin() throws TogglApiResponseException;

    /**
     * Returns info about the jira server
     * 
     * @return an implementation of ServerInfoDTO
     */
    // ServerInfoDTO getServerInfo() throws TogglApiResponseException;

    /**
     * Returns data from the jira server
     * 
     * @return an input stream object
     */
    InputStream getData(String strUrl) throws TogglApiResponseException;

}
