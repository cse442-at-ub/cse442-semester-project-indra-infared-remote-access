package com.indra.indra.models;

public class IpAddressModel {

    private long id;
    private String ipAddress;
    private String username;

    public IpAddressModel(){
        this.id = -1;
    }

    public IpAddressModel(long id, String ipAddress, String username) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
