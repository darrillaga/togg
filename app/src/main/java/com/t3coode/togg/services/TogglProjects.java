package com.t3coode.togg.services;

import java.util.List;

import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.views.ProjectCreationView;
import com.t3coode.togg.services.views.ProjectUpdateView;

public interface TogglProjects {

    /**
     * GET /projects gets a list of projects
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<projectDTO> object which represents the list of projects
     */
    List<ProjectDTO> list() throws TogglApiResponseException;

    /**
     * POST /projects creates a project
     * 
     * @param projectData
     *            projectCreationView containing the creation data
     * 
     *            If the projectData can be parsed to project, the received data
     *            will be set in this object and the returned project object
     *            will be the same as projectData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a ProjectDTO object which represents the created project
     */
    ProjectDTO create(ProjectCreationView projectData)
            throws TogglApiResponseException;

    /**
     * GET /projects/{id} gets a project
     * 
     * @param projectId
     *            long which represents the project id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a projectDTO object which represents the project
     */
    ProjectDTO get(Long projectId) throws TogglApiResponseException;

    /**
     * PUT /projects/{id} updates a project
     * 
     * @param projectData
     *            projectUpdateView which represents the project data that will
     *            be updated
     * 
     *            If the projectData can be parsed to project, the received data
     *            will be set in this object and the returned project object
     *            will be the same as projectData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a projectDTO object which represents the project
     */
    ProjectDTO update(ProjectUpdateView projectData)
            throws TogglApiResponseException;

    /**
     * DELETE /projects/{id} deletes a project
     * 
     * @param projectId
     *            int which represents the project id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the project was removed or not
     */
    boolean delete(int projectId) throws TogglApiResponseException;

    /**
     * DELETE /projects/{project_ids} deletes a list of projects
     * 
     * @param projectIds
     *            List<Integer> which represents the project ids
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the projects were removed or
     *         not
     */
    boolean delete(List<Integer> projectIds) throws TogglApiResponseException;

    /**
     * /projects/{id}/project_users namespace is derived to ProjectProjectUsers
     * manager
     * 
     * @return an implementation of TogglProjectProjectUsers
     */
    TogglProjectProjectUsers manageProjectUsers();

}