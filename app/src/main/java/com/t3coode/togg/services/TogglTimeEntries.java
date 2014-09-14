package com.t3coode.togg.services;

import java.util.Date;
import java.util.List;

import com.t3coode.togg.services.dtos.TimeEntryDTO;

public interface TogglTimeEntries {

    /**
     * GET /time_entries gets a list of timeEntries filtered by a specific time
     * range
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<TimeEntryDTO> object which represents the list of
     *         timeEntries
     */
    List<TimeEntryDTO> list(Date startDate, Date endDate)
            throws TogglApiResponseException;

    /**
     * POST /time_entries creates a timeEntry
     * 
     * @param timeEntryData
     *            TimeEntryCreationView containing the creation data
     * 
     *            If the timeEntryData can be parsed to timeEntry, the received
     *            data will be set in this object and the returned timeEntry
     *            object will be the same as timeEntryData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TimeEntryDTO object which represents the created TimeEntry
     */
    TimeEntryDTO create(TimeEntryDTO timeEntryData)
            throws TogglApiResponseException;

    /**
     * POST /time_entries/start starts a timeEntry
     * 
     * @param timeEntryData
     *            TimeEntryCreationView containing the creation data
     * 
     *            If the timeEntryData can be parsed to timeEntry, the received
     *            data will be set in this object and the returned timeEntry
     *            object will be the same as timeEntryData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TimeEntryDTO object which represents the started TimeEntry
     */
    TimeEntryDTO start(TimeEntryDTO timeEntryData)
            throws TogglApiResponseException;

    /**
     * PUT /time_entries/{id}/stop stops a timeEntry
     * 
     * @param timeEntryId
     *            int which represents the timeEntry id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TimeEntryDTO object which represents the started TimeEntry
     */
    TimeEntryDTO stop(int timeEntryId) throws TogglApiResponseException;

    /**
     * PUT /time_entries/{id}/stop stops a timeEntry
     * 
     * @param timeEntry
     *            TimeEntryDTO which represents the time entry
     * 
     *            The received data will be set in this object and the returned
     *            TimeEntry object will be the same as the param object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TimeEntryDTO object which represents the started TimeEntry
     */
    TimeEntryDTO stop(TimeEntryDTO timeEntry) throws TogglApiResponseException;

    /**
     * GET /time_entries/{id} gets a timeEntry
     * 
     * @param timeEntryId
     *            int which represents the timeEntry id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TimeEntryDTO object which represents the TimeEntry
     */
    TimeEntryDTO get(long timeEntryId) throws TogglApiResponseException;

    /**
     * GET /time_entries/current returns the current time entry
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TimeEntryDTO object which represents the current TimeEntry
     */
    TimeEntryDTO current() throws TogglApiResponseException;

    /**
     * PUT /time_entries/{id} updates a timeEntry
     * 
     * @param timeEntryData
     *            TimeEntryUpdateView which represents the timeEntry data that
     *            will be updated
     * 
     *            If the timeEntryData can be parsed to timeEntry, the received
     *            data will be set in this object and the returned timeEntry
     *            object will be the same as timeEntryData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TimeEntryDTO object which represents the TimeEntry
     */
    TimeEntryDTO update(TimeEntryDTO timeEntryData)
            throws TogglApiResponseException;

    /**
     * DELETE /time_entries/{id} deletes a timeEntry
     * 
     * @param timeEntryId
     *            int which represents the timeEntry id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the timeEntry was removed or
     *         not
     */
    boolean delete(long timeEntryId) throws TogglApiResponseException;

}