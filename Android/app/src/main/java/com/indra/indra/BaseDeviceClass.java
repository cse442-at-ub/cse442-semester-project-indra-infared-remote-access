package com.indra.indra;

/** Use not implemented yet, just sets out the framework for a class that defines what a "Device" is
 *
 */
public class BaseDeviceClass {
    private String deviceName;
    private int deviceId;

    public BaseDeviceClass(String name) {
        deviceName = name;
        deviceId = 1000; //Will be automatically generated eventually to keep track of devices
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        deviceName = name;
    }
    public int getDeviceId() {
        return deviceId;
    }
}
