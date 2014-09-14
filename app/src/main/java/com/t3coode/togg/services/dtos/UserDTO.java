package com.t3coode.togg.services.dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.t3coode.togg.services.views.UserCreationView;
import com.t3coode.togg.services.views.UserUpdateView;
import com.t3coode.togg.services.views.Views;

public class UserDTO extends IdentifiedBaseDTO implements UserCreationView,
        UserUpdateView {

    public static final String[] DATE_FORMATS = { "YYYY-MM-DD", "DD.MM.YYYY",
            "DD-MM-YYYY", "MM/DD/YYYY", "DD/MM/YYYY", "MM-DD-YYYY" };

    /**
     * two formats are supported: "H:mm" for 24-hour format "h:mm A" for 12-hour
     * format (AM/PM)
     */
    public static final String[] TIME_OF_DAY_FORMATS = { "H:mm", "h:mm A" };

    /** api_token: (string) */
    private String apiToken;

    /** default_wid: default workspace id (integer) */
    private int defaultWorkspaceId;

    @JsonView(Views.Create.class)
    /** email: (string) */
    private String email;

    /** fullname: (string) */
    private String fullname;

    @JsonView(Views.Create.class)
    /** password: (String) */
    private String password;

    /** current_password: (String) */
    private String currentPassword;

    /** jquery_timeofday_format: (string) */
    private String jqueryTimeofdayFormat;

    /** jquery_date_format:(string) */
    private String jqueryDataFormat;

    /** timeofday_format: (string) */
    private String timeofdayFormat;

    /** date_format: (string) */
    private String dateFormat;

    /**
     * store_start_and_stop_time: whether start and stop time are saved on time
     * entry (boolean)
     */
    private boolean storeStartAndStopTime;

    /** beginning_of_week: (integer 0-6, Sunday=0) */
    private int beginningOfWeek;

    /** language: user's language (string) */
    private String language;

    /** image_url: url with the user's profile picture(string) */
    private String imageUrl;

    /** sidebar_piechart: should a piechart be shown on the sidebar (boolean) */
    private String sidebarPiechart;

    /** at: timestamp of last changes */
    private Date at;

    /** new_blog_post: an object with toggl blog post title and link */
    private Map<String, Object> newBlogPost;

    /**
     * send_product_emails: (boolean) Toggl can send newsletters over e-mail to
     * the user
     */
    private boolean sendProductEmails;

    /** send_weekly_report: (boolean) if user receives weekly report */
    private boolean sendWeeklyReport;

    /**
     * send_timer_notifications: (boolean) email user about long-running (more
     * than 8 hours) tasks
     */
    private boolean sendTimerNotifications;

    /** openid_enabled: (boolean) google signin enabled */
    private boolean openidEnabled;

    /**
     * timezone: (string) timezone user has set on the "My profile" page ( IANA
     * TZ timezones )
     */
    @JsonView(Views.Create.class)
    private String timezone;

    /**
     * created_with: in free form, name of the app that signed the user app
     * (string, required)
     */
    @JsonView(Views.Create.class)
    private String createdWith = IdentifiedBaseDTO.CREATED_WITH;

    /** user resources */
    private List<TimeEntryDTO> timeEntries = new ArrayList<TimeEntryDTO>();
    private List<ProjectDTO> projects = new ArrayList<ProjectDTO>();
    private List<TagDTO> tags = new ArrayList<TagDTO>();
    private List<WorkspaceDTO> workspaces = new ArrayList<WorkspaceDTO>();
    private List<ClientDTO> clients = new ArrayList<ClientDTO>();

    @JsonProperty(value = "api_token")
    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
        if (apiToken != null) {
            this.currentPassword = null;
            this.password = null;
        }
        notifyChange("apiToken");
    }

    @JsonProperty(value = "default_wid")
    public int getDefaultWorkspaceId() {
        return defaultWorkspaceId;
    }

    public void setDefaultWorkspaceId(int workspaceId) {
        this.defaultWorkspaceId = workspaceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty(value = "current_password")
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    @JsonProperty(value = "jquery_timeofday_format")
    public String getJqueryTimeofdayFormat() {
        return jqueryTimeofdayFormat;
    }

    public void setJqueryTimeofdayFormat(String jqueryTimeofdayFormat) {
        this.jqueryTimeofdayFormat = jqueryTimeofdayFormat;
    }

    @JsonProperty(value = "jquery_data_format")
    public String getJqueryDataFormat() {
        return jqueryDataFormat;
    }

    public void setJqueryDataFormat(String jqueryDataFormat) {
        this.jqueryDataFormat = jqueryDataFormat;
    }

    @JsonProperty(value = "timeofday_format")
    public String getTimeofdayFormat() {
        return timeofdayFormat;
    }

    public void setTimeofdayFormat(String timeofdayFormat) {
        this.timeofdayFormat = timeofdayFormat;
    }

    @JsonProperty(value = "date_format")
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @JsonProperty(value = "store_start_and_stop_time")
    public boolean isStoreStartAndStopTime() {
        return storeStartAndStopTime;
    }

    public void setStoreStartAndStopTime(boolean storeStartAndStopTime) {
        this.storeStartAndStopTime = storeStartAndStopTime;
    }

    @JsonProperty(value = "beginning_of_week")
    public int getBeginningOfWeek() {
        return beginningOfWeek;
    }

    public void setBeginningOfWeek(int beginningOfWeek) {
        this.beginningOfWeek = beginningOfWeek;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty(value = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty(value = "sidebar_piechart")
    public String getSidebarPiechart() {
        return sidebarPiechart;
    }

    public void setSidebarPiechart(String sidebarPiechart) {
        this.sidebarPiechart = sidebarPiechart;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    @JsonProperty(value = "new_blog_post")
    public Map<String, Object> getNewBlogPost() {
        return newBlogPost;
    }

    public void setNewBlogPost(Map<String, Object> newBlogPost) {
        this.newBlogPost = newBlogPost;
    }

    @JsonProperty(value = "send_product_emails")
    public boolean isSendProductEmails() {
        return sendProductEmails;
    }

    public void setSendProductEmails(boolean sendProductEmails) {
        this.sendProductEmails = sendProductEmails;
    }

    @JsonProperty(value = "send_weekly_report")
    public boolean isSendWeeklyReport() {
        return sendWeeklyReport;
    }

    public void setSendWeeklyReport(boolean sendWeeklyReport) {
        this.sendWeeklyReport = sendWeeklyReport;
    }

    @JsonProperty(value = "send_timer_notifications")
    public boolean isSendTimerNotifications() {
        return sendTimerNotifications;
    }

    public void setSendTimerNotifications(boolean sendTimerNotifications) {
        this.sendTimerNotifications = sendTimerNotifications;
    }

    @JsonProperty(value = "openid_enabled")
    public boolean isOpenidEnabled() {
        return openidEnabled;
    }

    public void setOpenidEnabled(boolean openidEnabled) {
        this.openidEnabled = openidEnabled;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @JsonProperty(value = "created_with")
    public String getCreatedWith() {
        return createdWith;
    }

    public void setCreatedWith(String createdWith) {
        this.createdWith = createdWith;
    }

    @JsonProperty("time_entries")
    public List<TimeEntryDTO> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntryDTO> timeEntries) {
        this.timeEntries = timeEntries;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public List<WorkspaceDTO> getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(List<WorkspaceDTO> workspaces) {
        this.workspaces = workspaces;
    }

    public List<ClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clients = clients;
    }

}
