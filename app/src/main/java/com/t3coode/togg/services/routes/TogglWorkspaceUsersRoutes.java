package com.t3coode.togg.services.routes;

public class TogglWorkspaceUsersRoutes {
    public static final String PATH = "/wokspaces_users";
    public static final String WORKSPACE_USER = PATH + "/%s";
    public static final String INVITE = TogglWorkspacesRoutes.WORKSPACE
            + "/invite";
    public static final String WORKSPACE_USERS = TogglWorkspacesRoutes.WORKSPACE
            + "/workspace_users";
}
