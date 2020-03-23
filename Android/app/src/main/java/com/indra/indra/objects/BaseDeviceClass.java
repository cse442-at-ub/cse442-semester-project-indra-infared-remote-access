package com.indra.indra.objects;

/** Use not implemented yet, just sets out the framework for a class that defines what a "Device" is
 *
 */
public class BaseDeviceClass {
    private String displayName;
    private String lircName;
    private int deviceId;

    public BaseDeviceClass(String displayName, String lircName) {
        this.displayName = displayName;
        this.lircName = lircName;
        deviceId = 1000; //Will be automatically generated eventually to keep track of devices
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }
    public int getDeviceId() {
        return deviceId;
    }

    public String getLircName() {
        return lircName;
    }
}
