package com.t3coode.togg.services;

import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.spi.service.ServiceFinder;
import com.t3coode.togg.AndroidServiceIteratorProvider;
import com.t3coode.togg.services.TogglApiContext.TogglApiMessage;
import com.t3coode.togg.services.dtos.ErrorWrapperDTO;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.togg.services.factories.TogglApiContextFactory;
import com.t3coode.togg.services.routes.Routes;
import com.t3coode.togg.services.utils.JsonableImpl;
import com.t3coode.togg.services.utils.SSLClientUtils;

public abstract class TogglBaseService {

    public enum Messages implements TogglApiMessage {
        ERROR_WRITING_DATA("error_writing_data"), COULD_NOT_READ_CONTENT_FROM_SERVICE(
                "could_not_read_content_from_service"), INTERNAL_TOGGL_API_SERVER_ERROR(
                "internal_toggle_api_server_error"), COULD_NOT_READ_ERROR_CONTENT_FROM_SERVICE(
                "could_not_read_error_content_from_service"), COULD_NOT_CONNECT_TO_SERVICE(
                "could_not_connect_to_service");

        private String message;

        private Messages(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public static String METHOD_GET = "GET";
    public static String METHOD_POST = "POST";
    public static String METHOD_PUT = "PUT";
    public static String METHOD_DELETE = "DELETE";

    public static String DATA_KEY = "data";
    public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";

    private Routes routes;
    private Client client;
    private ObjectMapper mapper;

    public TogglBaseService() {
        ServiceFinder
                .setIteratorProvider(new AndroidServiceIteratorProvider<Object>());

        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJsonProvider.class);

        this.client = SSLClientUtils.createSSLClient(clientConfig);
        this.routes = Routes.getInstance();
        this.mapper = JsonableImpl.createMapper();
        this.mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
    }

    protected abstract UserDTO getCurrentUser();

    protected ObjectMapper getMapper() {
        return this.mapper;
    }

    protected Routes getRoutes() {
        return this.routes;
    }

    protected static interface ReadResponseFromClient<T> {
        T readValue(ClientResponse response) throws Exception;
    }

    protected ClientResponse createResponse(String method, String path,
            Object requestObject) throws TogglApiResponseException {
        ClientResponse response;

        setupCurrentUser();

        try {
            WebResource webResource = client.resource(path);

            if (requestObject != null) {
                response = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .method(method, ClientResponse.class, requestObject);
            } else {
                response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
                        .method(method, ClientResponse.class);
            }

        } catch (Exception e) {
            throw new TogglApiResponseException(
                    TogglApiContextFactory.getContext().getMessage(
                            Messages.COULD_NOT_READ_ERROR_CONTENT_FROM_SERVICE),
                    e);
        }

        return response;
    }

    protected ClientResponse createFileResponse(String method, String path,
            File file) throws TogglApiResponseException {
        ClientResponse response;

        setupCurrentUser();

        try {
            WebResource webResource = client.resource(path);

            FileDataBodyPart fdp = new FileDataBodyPart("file", file,
                    MediaType.APPLICATION_OCTET_STREAM_TYPE);
            FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
            formDataMultiPart.bodyPart(fdp);

            response = webResource.type(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("X-Atlassian-Token", "nocheck")
                    .method(method, ClientResponse.class, formDataMultiPart);

        } catch (Exception e) {
            throw new TogglApiResponseException(TogglApiContextFactory
                    .getContext().getMessage(Messages.ERROR_WRITING_DATA), e);
        }

        return response;
    }

    protected ClientResponse createResponse(String method, String path)
            throws TogglApiResponseException {
        return createResponse(method, path, null);
    }

    protected <T> T readResponseTemplate(ClientResponse response,
            ReadResponseFromClient<T> reader) throws TogglApiResponseException {
        return readResponseTemplate(response, reader, false);
    }

    // TODO review errors
    protected <T> T readResponseTemplate(ClientResponse response,
            ReadResponseFromClient<T> reader, boolean recurrent)
            throws TogglApiResponseException {
        T value = null;
        Status responseStatus = response.getClientResponseStatus();

        if (responseStatus.getFamily() == Family.SUCCESSFUL) {
            if (responseStatus != Status.NO_CONTENT) {
                try {
                    value = reader.readValue(response);
                } catch (Exception e) {
                    throw new TogglApiResponseException(
                            TogglApiContextFactory
                                    .getContext()
                                    .getMessage(
                                            Messages.COULD_NOT_READ_CONTENT_FROM_SERVICE),
                            e);
                }
            }
            return value;
        } else if (responseStatus == Status.MOVED_PERMANENTLY && !recurrent) {

            response = this.createResponse(Routes.getMethod(response),
                    Routes.getPathAndQuery(response.getLocation()));

            return readResponseTemplate(response, reader, true);
        } else if (responseStatus.getFamily() == Family.SERVER_ERROR
                || responseStatus.getFamily() == Family.CLIENT_ERROR) {

            TogglApiResponseException exception;

            try {
                exception = new TogglApiResponseException(
                        response.getEntity(ErrorWrapperDTO.class));
            } catch (Exception e) {
                exception = new TogglApiResponseException(
                        Messages.INTERNAL_TOGGL_API_SERVER_ERROR.getMessage(),
                        TogglApiContextFactory.getContext().getMessage(
                                Messages.INTERNAL_TOGGL_API_SERVER_ERROR));
            }

            throw exception;

        } else {
            TogglApiResponseException exception;

            try {
                exception = new TogglApiResponseException(
                        response.getEntity(ErrorWrapperDTO.class));
            } catch (Exception e) {
                exception = new TogglApiResponseException(
                        TogglApiContextFactory
                                .getContext()
                                .getMessage(
                                        Messages.COULD_NOT_READ_ERROR_CONTENT_FROM_SERVICE),
                        e);
            }

            throw exception;
        }
    }

    protected void setupCurrentUser() {
        UserDTO currentUser = getCurrentUser();
        if (currentUser != null) {
            if (StringUtils.isNotBlank(currentUser.getApiToken())) {
                this.setApiToken(currentUser.getApiToken());
            } else {
                this.setEmailPassword(currentUser.getEmail(),
                        currentUser.getPassword());
            }
        }
    }

    protected void setEmailPassword(String username, String password) {
        client.addFilter(new HTTPBasicAuthFilter(username, password));
    }

    protected void setApiToken(String apiToken) {
        client.addFilter(new HTTPBasicAuthFilter(apiToken, "api_token"));
    }

}
