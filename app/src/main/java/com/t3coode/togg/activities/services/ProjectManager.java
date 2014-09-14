package com.t3coode.togg.activities.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.DataSetObservable;

import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.async.AsyncLoaderTask;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;
import com.t3coode.togg.activities.async.LoaderTask;
import com.t3coode.togg.services.dtos.ProjectDTO;
import com.t3coode.togg.services.dtos.UserDTO;

public class ProjectManager extends DataSetObservable implements
        LoaderTask<List<ProjectDTO>> {

    public static ProjectManager instance;

    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }

        return instance;
    }

    public static void init() {
        getInstance().loadData();
    }

    private Map<Long, ProjectDTO> mProjectByIds = new HashMap<Long, ProjectDTO>();
    private boolean mIsLoading;
    private AsyncLoaderTask<List<ProjectDTO>> loader;

    private ProjectManager() {
        generateProjectsByIds();
    }

    public ProjectDTO get(Long id) {
        if (id == null) {
            return null;
        }

        if (mProjectByIds == null) {
            loadData();
            return null;
        }

        ProjectDTO project = mProjectByIds.get(id);

        if (project == null) {
            loadData();
        }

        return project;
    }

    private void loadData() {
        if (!mIsLoading) {
            loader = new AsyncLoaderTask<List<ProjectDTO>>();
            loader.execute(this);
            this.mIsLoading = true;
        }
    }

    @Override
    public void onPostExecute(GenericLoaderResponse<List<ProjectDTO>> result,
            Map<String, Object> params) {
        if (result.success()) {
            generateProjectsByIds();
        }
        this.mIsLoading = false;
    }

    @Override
    public List<ProjectDTO> onExecute(Map<String, Object> params)
            throws Exception {
        UserDTO user = ToggApp.getApplication().getTogglServices()
                .manageUsers().me(true, null);
        ToggApp.getApplication().onUpdateUser(user);
        return user.getProjects();
    }

    @Override
    public boolean awaitingTaskResult(Map<String, Object> params) {
        return true;
    }

    private void generateProjectsByIds() {
        Map<Long, ProjectDTO> projectsByIds = new HashMap<Long, ProjectDTO>();

        for (ProjectDTO project : ToggApp.getApplication().getCurrentUser()
                .getProjects()) {
            projectsByIds.put(project.getId(), project);
        }

        mProjectByIds.clear();
        mProjectByIds.putAll(projectsByIds);
        notifyChanged();
    }
}
