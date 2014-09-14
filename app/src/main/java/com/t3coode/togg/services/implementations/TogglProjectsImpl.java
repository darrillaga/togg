package com.t3coode.togg.services.implementations;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.ClientResponse;
import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.TogglProjectProjectUsers;
import com.t3coode.togg.services.TogglProjects;
import com.t3coode.togg.services.dtos.DataDTO;
import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.routes.TogglProjectsRoutes;
import com.t3coode.togg.services.views.ProjectCreationView;
import com.t3coode.togg.services.views.ProjectUpdateView;

public class TogglProjectsImpl extends TogglLoggedInBaseService implements
        TogglProjects {

    private static TogglProjectsImpl instance = null;

    private TogglProjectsImpl() {
    };

    public static TogglProjectsImpl getInstance() {
        if (instance == null) {
            instance = new TogglProjectsImpl();
        }
        return instance;
    }

    /**
     * GET /projects gets a list of projects
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<projectDTO> object which represents the list of projects
     */
    public List<ProjectDTO> list() throws TogglApiResponseException {
        return null;
    }

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
    public ProjectDTO create(ProjectCreationView projectData)
            throws TogglApiResponseException {
        return null;
    }

    /**
     * GET /projects/{id} gets a project
     * 
     * @param projectId
     *            int which represents the project id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a projectDTO object which represents the project
     */
    public ProjectDTO get(Long projectId) throws TogglApiResponseException {
        ClientResponse response = this.createResponse(METHOD_GET, getRoutes()
                .getRoute(TogglProjectsRoutes.PROJECT, projectId));

        return readResponseTemplate(response,
                new ReadResponseFromClient<ProjectDTO>() {

                    @Override
                    public ProjectDTO readValue(ClientResponse response)
                            throws Exception {

                        return getProjectFromResponse(response);
                    }
                });
    }

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
    public ProjectDTO update(ProjectUpdateView projectData)
            throws TogglApiResponseException {
        return null;
    }

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
    public boolean delete(int projectId) throws TogglApiResponseException {
        return false;
    }

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
    public boolean delete(List<Integer> projectIds)
            throws TogglApiResponseException {
        return false;
    }

    /**
     * /projects/{id}/project_users namespace is derived to ProjectProjectUsers
     * manager
     * 
     * @return an implementation of TogglProjectProjectUsers
     */
    public TogglProjectProjectUsers manageProjectUsers() {
        return null;
    }

    ProjectDTO getProjectFromResponse(ClientResponse response) throws Exception {
        DataDTO<ProjectDTO> remoteData = getMapper().readValue(
                response.getEntity(String.class),
                new TypeReference<DataDTO<ProjectDTO>>() {
                });

        return remoteData.getData();
    }

}