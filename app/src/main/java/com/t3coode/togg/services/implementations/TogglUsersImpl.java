package com.t3coode.togg.services.implementations;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.client.ClientResponse;
import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.TogglUsers;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.togg.services.routes.TogglUsersRoutes;
import com.t3coode.togg.services.utils.NullAwareBeanUtils;
import com.t3coode.togg.services.utils.UriParamsCreator;
import com.t3coode.togg.services.views.Views;

public class TogglUsersImpl extends TogglLoggedInBaseService implements
        TogglUsers {

    private static TogglUsersImpl instance = null;

    private TogglUsersImpl() {
    };

    public static TogglUsersImpl getInstance() {
        if (instance == null) {
            instance = new TogglUsersImpl();
        }
        return instance;
    }

    @Override
    public UserDTO signups(final UserDTO userData)
            throws TogglApiResponseException {

        ClientResponse response = createResponse(METHOD_POST, getRoutes()
                .getRoute(TogglUsersRoutes.CREATE_USER), userData.toJsonNodes(
                Views.Create.class, "user"));

        return readResponseTemplate(response,
                new ReadResponseFromClient<UserDTO>() {

                    @Override
                    public UserDTO readValue(ClientResponse response)
                            throws Exception {
                        UserDTO user = response.getEntity(UserDTO.class);
                        NullAwareBeanUtils.getInstance().copyProperties(
                                userData, user);

                        return userData;
                    }
                });
    }

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
     *             TogglApiResponseException
     * 
     * @return a UserDTO object which represents the current user
     */
    public UserDTO me(Boolean withRelatedData, Long since)
            throws TogglApiResponseException {

        Map<String, String> urlParams = new HashMap<String, String>();

        if (withRelatedData != null) {
            urlParams.put("with_related_data", withRelatedData.toString());
        }
        if (since != null) {
            urlParams.put("since", since.toString());
        }

        ClientResponse response = createResponse(
                METHOD_GET,
                getRoutes().getRoute(TogglUsersRoutes.ME)
                        + UriParamsCreator.create(urlParams));

        return readResponseTemplate(response,
                new ReadResponseFromClient<UserDTO>() {

                    @Override
                    public UserDTO readValue(ClientResponse response)
                            throws Exception {
                        JsonNode node = getMapper().readTree(
                                response.getEntity(String.class));

                        UserDTO remoteUser = getMapper().convertValue(
                                node.get(DATA_KEY), UserDTO.class);

                        NullAwareBeanUtils.getInstance().copyProperties(
                                getCurrentUser(), remoteUser);
                        return remoteUser;
                    }
                });
    }

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
    public UserDTO update(final UserDTO userData)
            throws TogglApiResponseException {
        ClientResponse response = createResponse(METHOD_PUT, getRoutes()
                .getRoute(TogglUsersRoutes.ME, userData.getId()));

        return readResponseTemplate(response,
                new ReadResponseFromClient<UserDTO>() {

                    @Override
                    public UserDTO readValue(ClientResponse response)
                            throws Exception {
                        UserDTO user = response.getEntity(UserDTO.class);
                        NullAwareBeanUtils.getInstance().copyProperties(
                                userData, user);
                        return userData;
                    }
                });
    }

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
    public String resetApiToken(UserDTO user) throws TogglApiResponseException {
        String token = resetApiToken();

        if (!StringUtils.isBlank(token)) {
            user.setApiToken(token);
        }

        return token;
    }

    /**
     * POST /reset_token resets the API token of the current user
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a String object with the new token;
     */
    public String resetApiToken() throws TogglApiResponseException {
        ClientResponse response = createResponse(METHOD_GET, getRoutes()
                .getRoute(TogglUsersRoutes.RESET_TOKEN));

        return readResponseTemplate(response,
                new ReadResponseFromClient<String>() {

                    @Override
                    public String readValue(ClientResponse response)
                            throws Exception {
                        return response.getEntity(String.class);
                    }
                });
    }

}