package com.t3coode.togg.services.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.t3coode.togg.services.views.ClientCreationView;
import com.t3coode.togg.services.views.ClientUpdateView;

public class ClientDTO extends IdentifiedBaseDTO implements ClientCreationView,
        ClientUpdateView {

    /** name: The name of the client (string, required, unique in workspace) */
    private String name;

    /** wid: workspace ID, where the client will be used (integer, required) */
    private long workspaceId;

    /** notes: Notes for the client (string, not required) */
    private Date at;

    /**
     * hrate: The hourly rate for this client (float, not required, available
     * only for pro workspaces)
     */
    private String notes;

    /**
     * cur: The name of the client's currency (string, not required, available
     * only for pro workspaces)
     */
    private int hrate;

    /**
     * at: timestamp that is sent in the response, indicates the time client was
     * last updated
     */
    private String cur;

    @JsonProperty(value = "wid")
    public long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(long workspaceId) {
        this.workspaceId = workspaceId;
    }

    @ToString
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getHrate() {
        return hrate;
    }

    public void setHrate(int hrate) {
        this.hrate = hrate;
    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

}
