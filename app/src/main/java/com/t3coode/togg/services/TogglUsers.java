package com.t3coode.togg.services;

import com.t3coode.togg.services.dtos.UserDTO;

public interface TogglUsers {

    /**
     * POST /signups creates a user
     * 
     * @param userData
     *            UserCreationView containing the creation data
     * 
     *            If the userData can be parsed to user, the received data will
     *            be set in this object and the returned user object will be the
     *            same as userData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a UserDTO object which represents the created User
     */
    UserDTO signups(UserDTO userData) throws TogglApiResponseException;

    /**
     * GET /me returns the current user
     * 
     * @param withRelatedData
     *            Boolean which indicates if the user's related data will be
     *            returned
     * 
     * @param since
     *            Long must be set If you want to retrieve objects which have
     *            changed after certain time. The value should be a unix
     *            timestamp (e.g. since=1362579886)
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a UserDTO object which represents the current user
     */
    UserDTO me(Boolean withRelatedData, Long since)
            throws TogglApiResponseException;

    /**
     * PUT /me updates a user
     * 
     * @param userData
     *            UserUpdateView which represents the user data that will be
     *            updated
     * 
     *            If the userData can be parsed to user, the received data will
     *            be set in this object and the returned user object will be the
     *            same as userData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a UserDTO object which represents the User
     */
    UserDTO update(UserDTO userData) throws TogglApiResponseException;

    /**
     * POST /reset_token resets the API token of the current user
     * 
     * @param user
     *            UserDTO whose token will be updated
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a String object with the new token;
     */
    String resetApiToken(UserDTO user) throws TogglApiResponseException;

    /**
     * POST /reset_token resets the API token of the current user
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a String object with the new token;
     */
    String resetApiToken() throws TogglApiResponseException;

}