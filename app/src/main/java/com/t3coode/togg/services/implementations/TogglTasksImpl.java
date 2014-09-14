package com.t3coode.togg.services.implementations;

import java.util.List;

import com.t3coode.togg.services.TogglApiResponseException;
import com.t3coode.togg.services.TogglTasks;
import com.t3coode.togg.services.dtos.TaskDTO;
import com.t3coode.togg.services.views.TaskMassUpdateView;

public class TogglTasksImpl extends TogglLoggedInBaseService implements
        TogglTasks {

    private static TogglTasksImpl instance = null;

    private TogglTasksImpl() {
    };

    public static TogglTasksImpl getInstance() {
        if (instance == null) {
            instance = new TogglTasksImpl();
        }
        return instance;
    }

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
    @Override
    public TaskDTO create(TaskDTO taskData) throws TogglApiResponseException {
        return null;
    }

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
    @Override
    public TaskDTO get(int taskId) throws TogglApiResponseException {
        return null;
    }

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
    @Override
    public TaskDTO update(TaskDTO taskData) throws TogglApiResponseException {
        return null;
    }

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
    @Override
    public boolean delete(int taskId) throws TogglApiResponseException {
        return false;
    }

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
    @Override
    public List<TaskDTO> update(TaskMassUpdateView taskData,
            List<Integer> tasksIds) throws TogglApiResponseException {
        return null;
    }

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
    @Override
    public boolean delete(List<Integer> tasksIds)
            throws TogglApiResponseException {
        return false;
    }

}