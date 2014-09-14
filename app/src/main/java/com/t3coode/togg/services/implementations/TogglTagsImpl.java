package com.t3coode.togg.services.implementations;

import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.ClientResponse;
import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.TogglTags;
import com.t3coode.togg.services.dtos.TagDTO;
import com.t3coode.togg.services.factories.TogglApiContextFactory;
import com.t3coode.togg.services.routes.TogglTagsRoutes;
import com.t3coode.togg.services.utils.NullAwareBeanUtils;
import com.t3coode.togg.services.views.Views;

public class TogglTagsImpl extends TogglLoggedInBaseService implements
        TogglTags {

    private static TogglTagsImpl instance = null;

    private TogglTagsImpl() {
    };

    public static TogglTagsImpl getInstance() {
        if (instance == null) {
            instance = new TogglTagsImpl();
        }
        return instance;
    }

    public TagDTO create(final TagDTO tagData) throws TogglApiResponseException {

        ObjectNode json = getMapper().createObjectNode();

        try {
            json.putAll(tagData.toJsonNodes(Views.Create.class));
        } catch (Exception e) {
            throw new TogglApiResponseException(TogglApiContextFactory
                    .getContext().getMessage(Messages.ERROR_WRITING_DATA), e);
        }

        ClientResponse response = createResponse(METHOD_POST, getRoutes()
                .getRoute(TogglTagsRoutes.PATH), json.toString());

        return readResponseTemplate(response,
                new ReadResponseFromClient<TagDTO>() {

                    @Override
                    public TagDTO readValue(ClientResponse response)
                            throws Exception {
                        TagDTO tag = response.getEntity(TagDTO.class);
                        NullAwareBeanUtils.getInstance().copyProperties(
                                tagData, tag);

                        return tagData;
                    }
                });
    }

    public TagDTO update(final TagDTO tagData) throws TogglApiResponseException {

        ClientResponse response = this.createResponse(METHOD_PUT, getRoutes()
                .getRoute(TogglTagsRoutes.TAG, tagData.getId()));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TagDTO>() {

                    @Override
                    public TagDTO readValue(ClientResponse response)
                            throws Exception {
                        TagDTO tag = response.getEntity(TagDTO.class);
                        NullAwareBeanUtils.getInstance().copyProperties(
                                tagData, tag);
                        return tagData;
                    }
                });
    }

    public boolean delete(int tagId) throws TogglApiResponseException {

        ClientResponse response = this.createResponse(METHOD_DELETE,
                getRoutes().getRoute(TogglTagsRoutes.TAG, tagId));

        return readResponseTemplate(response,
                new ReadResponseFromClient<Boolean>() {

                    @Override
                    public Boolean readValue(ClientResponse response)
                            throws Exception {
                        return response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL;
                    }
                });
    }

}