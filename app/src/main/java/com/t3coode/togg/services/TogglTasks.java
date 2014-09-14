package com.t3coode.togg.services;

import java.util.List;

import com.t3coode.togg.services.dtos.TaskDTO;
import com.t3coode.togg.services.views.TaskMassUpdateView;

public interface TogglTasks {

    /**
     * POST /tasks creates a task
     * 
     * @param taskData
     *            TaskCreationView containing the creation data
     * 
     *            If the taskData can be parsed to task, the received data will
     *            be set in this object and the returned task object will be the
     *            same as taskData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TaskDTO object which represents the created Task
     */
    TaskDTO create(TaskDTO taskData) throws TogglApiResponseException;

    /**
     * GET /tasks/{id} gets a task
     * 
     * @param taskId
     *            int which represents the task id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TaskDTO object which represents the Task
     */
    TaskDTO get(int taskId) throws TogglApiResponseException;

    /**
     * PUT /tasks/{id} updates a task
     * 
     * @param taskData
     *            TaskUpdateView which represents the task data that will be
     *            updated
     * 
     *            If the taskData can be parsed to task, the received data will
     *            be set in this object and the returned task object will be the
     *            same as taskData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TaskDTO object which represents the Task
     */
    TaskDTO update(TaskDTO taskData) throws TogglApiResponseException;

    /**
     * DELETE /tasks/{id} deletes a task
     * 
     * @param taskId
     *            int which represents the task id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the task was removed or not
     */
    boolean delete(int taskId) throws TogglApiResponseException;

    /**
     * PUT /tasks/{tasks_ids} updates a list of tasks
     * 
     * @param taskData
     *            TaskUpdateView which represents the template with task data to
     *            be updated
     * 
     * @param taskIds
     *            List<Integer> which contains the ids of the tasks to be
     *            updated
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a List<TaskDTO> object which represents the updated tasks
     */
    List<TaskDTO> update(TaskMassUpdateView taskData, List<Integer> tasksIds)
            throws TogglApiResponseException;

    /**
     * DELETE /tasks/{tasks_ids} deletes a list of tasks
     * 
     * @paramprojectUserIds List<Integer> which represents the tasks ids
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the tasks were removed or not
     */
    boolean delete(List<Integer> tasksIds) throws TogglApiResponseException;

}