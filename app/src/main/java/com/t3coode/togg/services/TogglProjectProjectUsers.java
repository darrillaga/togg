package com.t3coode.togg.services;

import java.util.List;

import com.t3coode.togg.services.dtos.ProjectUserDTO;

public interface TogglProjectProjectUsers {

    /**
     * GET /projects/{project_id}/project_userss gets a list of project users
     * for the project
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<ProjectUserDTO> object which represents the list of the
     *         project's project users
     */
    List<ProjectUserDTO> list() throws TogglApiResponseException;

}