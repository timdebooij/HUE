package com.timdebooij.hueapplicatie.models;

public class Bridge {
    public String ipAddress;
    public String name;
    public String port;
    public String token;

    public Bridge(String ipAddress, String name, String port) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.port = port;
    }
}
