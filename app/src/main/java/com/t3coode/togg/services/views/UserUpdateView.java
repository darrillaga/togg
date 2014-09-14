package com.t3coode.togg.services.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface UserUpdateView {

    String getEmail();

    public String getFullname();

    public String getPassword();

    @JsonProperty(value = "current_password")
    public String getCurrentPassword();

    @JsonProperty(value = "timeofday_format")
    public String getTimeofdayFormat();

    @JsonProperty(value = "date_format")
    public String getDateFormat();

    @JsonProperty(value = "store_start_and_stop_time")
    public boolean isStoreStartAndStopTime();

    @JsonProperty(value = "beginning_of_week")
    public int getBeginningOfWeek();

    @JsonProperty(value = "send_product_emails")
    public boolean isSendProductEmails();

    @JsonProperty(value = "send_weekly_report")
    public boolean isSendWeeklyReport();

    @JsonProperty(value = "send_timer_notifications")
    public boolean isSendTimerNotifications();

    public String getTimezone();

}
