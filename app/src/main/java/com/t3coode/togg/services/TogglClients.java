package com.t3coode.togg.services;

import java.util.List;

import com.t3coode.togg.services.dtos.ClientDTO;
import com.t3coode.togg.services.views.ClientCreationView;
import com.t3coode.togg.services.views.ClientUpdateView;

public interface TogglClients {

    /**
     * GET /clients gets a list of clients
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<ClientDTO> object which represents the list of clients
     */
    List<ClientDTO> list() throws TogglApiResponseException;

    /**
     * POST /clients creates a client
     * 
     * @param clientData
     *            ClientCreationView containing the creation data
     * 
     *            If the clientData can be parsed to client, the received data
     *            will be set in this object and the returned client object will
     *            be the same as clientData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a ClientDTO object which represents the created Client
     */
    ClientDTO create(ClientCreationView clientData)
            throws TogglApiResponseException;

    /**
     * GET /clients/{id} gets a client
     * 
     * @param clientId
     *            int which represents the client id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a ClientDTO object which represents the Client
     */
    ClientDTO get(int clientId) throws TogglApiResponseException;

    /**
     * PUT /clients/{id} updates a client
     * 
     * @param clientData
     *            ClientUpdateView which represents the client data that will be
     *            updated
     * 
     *            If the clientData can be parsed to client, the received data
     *            will be set in this object and the returned client object will
     *            be the same as clientData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a ClientDTO object which represents the Client
     */
    ClientDTO update(ClientUpdateView clientData)
            throws TogglApiResponseException;

    /**
     * DELETE /clients/{id} deletes a client
     * 
     * @param clientId
     *            int which represents the client id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the client was removed or not
     */
    boolean delete(int clientId) throws TogglApiResponseException;

    /**
     * /clients/{id}/projects namespace is derived to ClientProjects manager
     * 
     * @return an implementation of TogglClientProjects
     */
    TogglClientProjects manageProjects();

}