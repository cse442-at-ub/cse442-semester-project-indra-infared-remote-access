package com.indra.indra;

public class Device {
    private String deviceName;
    private int deviceId;

    public Device(String name) {
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
