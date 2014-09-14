package com.t3coode.togg.services.implementations;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status.Family;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.ClientResponse;
import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.TogglTimeEntries;
import com.t3coode.togg.services.dtos.DataDTO;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.services.routes.TogglTimeEntriesRoutes;
import com.t3coode.togg.services.utils.DateUtil.JodaDateFormat;
import com.t3coode.togg.services.utils.NullAwareBeanUtils;
import com.t3coode.togg.services.utils.UriParamsCreator;
import com.t3coode.togg.services.views.Views;

public class TogglTimeEntriesImpl extends TogglLoggedInBaseService implements
        TogglTimeEntries {

    private static TogglTimeEntriesImpl instance = null;

    private TogglTimeEntriesImpl() {
    };

    public static TogglTimeEntriesImpl getInstance() {
        if (instance == null) {
            instance = new TogglTimeEntriesImpl();
        }
        return instance;
    }

    @Override
    public List<TimeEntryDTO> list(Date startDate, Date endDate)
            throws TogglApiResponseException {
        Map<String, String> urlParams = new HashMap<String, String>();

        JodaDateFormat dateFormatter = new JodaDateFormat(DATE_FORMAT);

        if (startDate != null) {
            urlParams.put("start_date", dateFormatter.format(startDate));
        }

        if (endDate != null) {
            urlParams.put("end_date", dateFormatter.format(endDate));
        }

        ClientResponse response = this.createResponse(METHOD_GET,
                getRoutes().getRoute(TogglTimeEntriesRoutes.PATH)
                        + UriParamsCreator.create(urlParams));

        return readResponseTemplate(response,
                new ReadResponseFromClient<List<TimeEntryDTO>>() {

                    @Override
                    public List<TimeEntryDTO> readValue(ClientResponse response)
                            throws Exception {
                        return getMapper().readValue(
                                response.getEntity(String.class),
                                new TypeReference<List<TimeEntryDTO>>() {
                                });
                    }
                });
    }

    @Override
    public TimeEntryDTO create(final TimeEntryDTO timeEntryData)
            throws TogglApiResponseException {

        ClientResponse response = this.createResponse(METHOD_POST, getRoutes()
                .getRoute(TogglTimeEntriesRoutes.PATH), timeEntryData
                .toJsonNodes(Views.Create.class, "time_entry"));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TimeEntryDTO>() {

                    @Override
                    public TimeEntryDTO readValue(ClientResponse response)
                            throws Exception {
                        TimeEntryDTO timeEntry = getTimeEntryFromResponse(response);

                        NullAwareBeanUtils.getInstance().copyProperties(
                                timeEntryData, timeEntry);
                        return timeEntryData;
                    }
                });
    }

    @Override
    public TimeEntryDTO start(final TimeEntryDTO timeEntryData)
            throws TogglApiResponseException {

        ClientResponse response = this.createResponse(METHOD_POST, getRoutes()
                .getRoute(TogglTimeEntriesRoutes.START), timeEntryData
                .toJsonNodes(Views.Update.class, "time_entry"));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TimeEntryDTO>() {

                    @Override
                    public TimeEntryDTO readValue(ClientResponse response)
                            throws Exception {
                        TimeEntryDTO timeEntry = getTimeEntryFromResponse(response);

                        NullAwareBeanUtils.getInstance().copyProperties(
                                timeEntryData, timeEntry);
                        return timeEntryData;
                    }
                });
    }

    @Override
    public TimeEntryDTO stop(int timeEntryId) throws TogglApiResponseException {
        ClientResponse response = this.createResponse(METHOD_POST, getRoutes()
                .getRoute(TogglTimeEntriesRoutes.STOP, timeEntryId));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TimeEntryDTO>() {

                    @Override
                    public TimeEntryDTO readValue(ClientResponse response)
                            throws Exception {
                        return getTimeEntryFromResponse(response);
                    }
                });
    }

    @Override
    public TimeEntryDTO stop(final TimeEntryDTO timeEntry)
            throws TogglApiResponseException {
        ClientResponse response = this.createResponse(METHOD_PUT, getRoutes()
                .getRoute(TogglTimeEntriesRoutes.STOP, timeEntry.getId()));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TimeEntryDTO>() {

                    @Override
                    public TimeEntryDTO readValue(ClientResponse response)
                            throws Exception {
                        TimeEntryDTO remoteTimeEntry = getTimeEntryFromResponse(response);

                        NullAwareBeanUtils.getInstance().copyProperties(
                                timeEntry, remoteTimeEntry);

                        return timeEntry;
                    }
                });
    }

    @Override
    public TimeEntryDTO get(long timeEntryId) throws TogglApiResponseException {
        ClientResponse response = this.createResponse(METHOD_GET, getRoutes()
                .getRoute(TogglTimeEntriesRoutes.TIME_ENTRY));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TimeEntryDTO>() {

                    @Override
                    public TimeEntryDTO readValue(ClientResponse response)
                            throws Exception {

                        return getTimeEntryFromResponse(response);
                    }
                });
    }

    @Override
    public TimeEntryDTO current() throws TogglApiResponseException {
        ClientResponse response = this.createResponse(METHOD_GET, getRoutes()
                .getRoute(TogglTimeEntriesRoutes.CURRENT_TIME_ENTRY));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TimeEntryDTO>() {

                    @Override
                    public TimeEntryDTO readValue(ClientResponse response)
                            throws Exception {

                        return getTimeEntryFromResponse(response);
                    }
                });
    }

    @Override
    public TimeEntryDTO update(final TimeEntryDTO timeEntryData)
            throws TogglApiResponseException {

        ClientResponse response = this.createResponse(
                METHOD_PUT,
                getRoutes().getRoute(TogglTimeEntriesRoutes.TIME_ENTRY,
                        timeEntryData.getId()),
                timeEntryData.toJsonNodes(Views.Update.class, "time_entry"));

        return readResponseTemplate(response,
                new ReadResponseFromClient<TimeEntryDTO>() {

                    @Override
                    public TimeEntryDTO readValue(ClientResponse response)
                            throws Exception {

                        NullAwareBeanUtils.getInstance().copyProperties(
                                timeEntryData,
                                getTimeEntryFromResponse(response));

                        return timeEntryData;
                    }
                });
    }

    @Override
    public boolean delete(long timeEntryId) throws TogglApiResponseException {
        ClientResponse response = this.createResponse(
                METHOD_DELETE,
                getRoutes().getRoute(TogglTimeEntriesRoutes.TIME_ENTRY,
                        timeEntryId));

        return readResponseTemplate(response,
                new ReadResponseFromClient<Boolean>() {

                    @Override
                    public Boolean readValue(ClientResponse response)
                            throws Exception {
                        return response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL;
                    }
                });
    }

    private TimeEntryDTO getTimeEntryFromResponse(ClientResponse response)
            throws Exception {
        DataDTO<TimeEntryDTO> remoteData = getMapper().readValue(
                response.getEntity(String.class),
                new TypeReference<DataDTO<TimeEntryDTO>>() {
                });

        return remoteData.getData();
    }

}