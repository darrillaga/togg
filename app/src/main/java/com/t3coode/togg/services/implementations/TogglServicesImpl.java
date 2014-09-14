package com.t3coode.togg.services.implementations;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.TogglBaseService;
import com.t3coode.togg.services.TogglClients;
import com.t3coode.togg.services.TogglProjectUsers;
import com.t3coode.togg.services.TogglProjects;
import com.t3coode.togg.services.TogglServices;
import com.t3coode.togg.services.TogglTags;
import com.t3coode.togg.services.TogglTasks;
import com.t3coode.togg.services.TogglTimeEntries;
import com.t3coode.togg.services.TogglUsers;
import com.t3coode.togg.services.TogglWorkspaceUsers;
import com.t3coode.togg.services.TogglWorkspaces;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.togg.services.routes.TogglRoutes;

public class TogglServicesImpl extends TogglBaseService implements
        TogglServices {

    private static TogglServicesImpl instance;

    private UserDTO currentUser;

    // private JiraUserDTO loggedInUser;

    private TogglServicesImpl() {
    };

    public static TogglServices getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Could not instantiate class. Execute build operation first.");
        }
        return instance;
    }

    public static void build(UserDTO currentUser) {
        if (instance == null) {
            synchronized (TogglServices.class) {
                if (instance == null) {
                    instance = new TogglServicesImpl();
                    instance.currentUser = currentUser;
                }
            }
        }
    }

    @Override
    public TogglUsers manageUsers() {
        return TogglUsersImpl.getInstance();
    }

    @Override
    public TogglClients manageClients() {
        return null;
    }

    @Override
    public TogglProjects manageProjects() {
        return TogglProjectsImpl.getInstance();
    }

    @Override
    public TogglProjectUsers manageProjectUsers() {
        return null;
    }

    @Override
    public TogglTags manageTags() {
        return null;
    }

    @Override
    public TogglTasks manageTasks() {
        return null;
    }

    @Override
    public TogglTimeEntries manageTimeEntries() {
        return TogglTimeEntriesImpl.getInstance();
    }

    @Override
    public TogglWorkspaces manageWrokspaceUsers() {
        return null;
    }

    @Override
    public TogglWorkspaceUsers manageWorkspaces() {
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.mooveit.TogglServices.services.JiraGreenHopperServices#getLogin()
     */
    @Override
    public UserDTO getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(UserDTO user) {
        this.currentUser = user;
    }

    public List<String> getTimezones() throws TogglApiResponseException {
        ClientResponse response = createResponse(METHOD_GET, getRoutes()
                .getRoute(TogglRoutes.TIMEZONES));

        return readResponseTemplate(response,
                new ReadResponseFromClient<List<String>>() {

                    @Override
                    public List<String> readValue(ClientResponse response)
                            throws Exception {
                        return getMapper().readValue(
                                response.getEntity(String.class),
                                new TypeReference<List<String>>() {
                                });
                    }
                });

    }

    /**
     * (non-Javadoc)
     * 
     * @see com.mooveit.TogglServices.services.JiraGreenHopperServices#performLogin()
     */
    @Override
    public void performLogin() throws TogglApiResponseException {

        // Map<String, String> urlParams = new HashMap<String, String>();
        // urlParams.put("username", getLogin().getUserName());
        //
        // ClientResponse response = this.createResponse(
        // TogglBaseService.METHOD_GET, login.getHostWithVerb()
        // + TogglRoutes.USER + UriParamsCreator.create(urlParams));

        // loggedInUser = readResponseTemplate(response,
        // new ReadResponseFromClient<JiraUserDTO>() {
        //
        // @Override
        // public JiraUserDTO readValue(ClientResponse response)
        // throws Exception {
        // return response.getEntity(JiraUserDTO.class);
        // }
        // });
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.mooveit.TogglServices.services.JiraGreenHopperServices#getData()
     */
    @Override
    public InputStream getData(String strUrl) throws TogglApiResponseException {

        Client client = Client.create();
        client.setFollowRedirects(true);
        WebResource r = client.resource(strUrl);
        ClientResponse response = r.get(ClientResponse.class);

        return readResponseTemplate(response,
                new ReadResponseFromClient<InputStream>() {

                    @Override
                    public InputStream readValue(ClientResponse response)
                            throws Exception {
                        return response.getEntity(InputStream.class);
                    }

                });
    }
}
