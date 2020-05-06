package com.indra.indra.models;

import com.indra.indra.db.DatabaseUtil;

import java.util.ArrayList;

/** Use not implemented yet, just sets out the framework for a class that defines what a "Device" is
 *
 */
public class RemoteModel {
    private String displayName;
    private String lircName;
    private String user;
    private long deviceId;
    private ArrayList<RemoteButtonModel> buttonModels;

    public RemoteModel(String displayName, String lircName) {
        this.displayName = displayName;
        this.lircName = lircName;
        this.user = DatabaseUtil.DEFAULT_USER;
        deviceId = 1000; //Will be automatically generated eventually to keep track of devices
        this.buttonModels = new ArrayList<>();
    }


    public RemoteModel(String displayName, String lircName, String user, long deviceId){
        this.displayName = displayName;
        this.lircName = lircName;
        this.user = user;
        this.deviceId = deviceId;
        this.buttonModels = new ArrayList<>();
    }



    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }
    public long getDeviceId() {
        return deviceId;
    }

    public String getLircName() {
        return lircName;
    }

    public void setButtonModels(ArrayList<RemoteButtonModel> buttonModels){
        this.buttonModels = buttonModels;
    }

    public ArrayList<RemoteButtonModel> getButtonModels(){
        return buttonModels;
    }

    public void addButtonModel(RemoteButtonModel buttonModel){
        this.buttonModels.add(buttonModel);
    }

    public String getUser() {
        return user;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
}
