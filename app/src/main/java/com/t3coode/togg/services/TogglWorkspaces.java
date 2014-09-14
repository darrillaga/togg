package com.t3coode.togg.services;

import java.util.List;

import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.dtos.WorkspaceDTO;
import com.t3coode.togg.services.utils.ActivesFilter;

public interface TogglWorkspaces {

    /**
     * GET /workspaces gets a list of workspaces
     * 
     * @param active
     *            ActivesFilter containing information about the kind of filter
     *            used for retrieve the data, the default is true
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<WorkspaceDTO> object which represents the list of
     *         workspaces
     */
    List<WorkspaceDTO> list() throws TogglApiResponseException;

    /**
     * GET /clients/{client_id}/projects gets a list of projects for the client
     * 
     * @param active
     *            ActivesFilter containing information about the kind of filter
     *            used for retrieve the data, the default is true
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<ProjectDTO> object which represents the list of the
     *         client's projects
     */
    List<ProjectDTO> list(ActivesFilter active)
            throws TogglApiResponseException;
}