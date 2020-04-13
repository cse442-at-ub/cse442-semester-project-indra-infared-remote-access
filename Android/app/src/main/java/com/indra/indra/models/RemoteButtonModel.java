package com.indra.indra.models;

public class RemoteButtonModel {

    private String displayName;
    private String lircName;
    private long remoteId;
    private long id;

    public RemoteButtonModel(String displayName, String lircName, long remoteId, long id) {
        this.displayName = displayName;
        this.lircName = lircName;
        this.remoteId = remoteId;
        this.id = id;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLircName() {
        return lircName;
    }

    public void setLircName(String lircName) {
        this.lircName = lircName;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
