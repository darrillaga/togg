package com.t3coode.togg.services;

import java.util.List;

import com.t3coode.togg.services.dtos.ProjectUserDTO;
import com.t3coode.togg.services.views.ProjectUserCreationView;
import com.t3coode.togg.services.views.ProjectUserUpdateView;

public interface TogglProjectUsers {

    /**
     * POST /project_users creates a projectUser
     * 
     * @param projectUserData
     *            ProjectUserCreationView containing the creation data
     * 
     *            If the projectUserData can be parsed to projectUser, the
     *            received data will be set in this object and the returned
     *            ProjectUser object will be the same as ProjectUserData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a ProjectUserDTO object which represents the created project user
     */
    ProjectUserDTO create(ProjectUserCreationView projectUserData)
            throws TogglApiResponseException;

    /**
     * GET /project_users/{id} gets a ProjectUser
     * 
     * @param projectUserId
     *            int which represents the project user id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a ProjectUserDTO object which represents the project user
     */
    ProjectUserDTO get(int projectUserId) throws TogglApiResponseException;

    /**
     * PUT /project_users/{id} updates a ProjectUser
     * 
     * @param projectUserData
     *            ProjectUserUpdateView which represents the project user data
     *            that will be updated
     * 
     *            If the ProjectUserData can be parsed to ProjectUser, the
     *            received data will be set in this object and the returned
     *            ProjectUser object will be the same as ProjectUserData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a ProjectUserDTO object which represents the project user
     */
    ProjectUserDTO update(ProjectUserUpdateView projectUserData)
            throws TogglApiResponseException;

    /**
     * DELETE /project_users/{id} deletes a ProjectUser
     * 
     * @param projectUserId
     *            int which represents the project user id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the ProjectUser was removed or
     *         not
     */
    boolean delete(int projectUserId) throws TogglApiResponseException;

    /**
     * POST /project_users creates multiple project users for single project
     * 
     * @param projectUserData
     *            ProjectUserCreationView containing the template of creation
     *            data
     * 
     * @param userIds
     *            List<Integer> containing the user's ids
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<ProjectUserDTO> object which represents the created
     *         project users
     */
    List<ProjectUserDTO> create(
            ProjectUserCreationView projectUserTemplateData,
            List<Integer> userIds) throws TogglApiResponseException;

    /**
     * PUT /project_users/{projectUserIds} updates a list of project users
     * 
     * @param projectUserData
     *            ProjectUserUpdateView which represents the template with
     *            project user data to be updated
     * 
     * @param projectUserIds
     *            List<Integer> which contains the ids of the project users to
     *            be updated
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<ProjectUserDTO> object which represents the updated
     *         project users
     */
    List<ProjectUserDTO> update(ProjectUserUpdateView projectUserData,
            List<Integer> projectUserIds) throws TogglApiResponseException;

    /**
     * DELETE /project_users/{projectUser_ids} deletes a list of project users
     * 
     * @param projectUserIds
     *            List<Integer> which represents the project user ids
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the project users were removed
     *         or not
     */
    boolean delete(List<Integer> projectUserIds)
            throws TogglApiResponseException;

}