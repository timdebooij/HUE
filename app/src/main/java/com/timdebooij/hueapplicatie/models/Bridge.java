package com.timdebooij.hueapplicatie.models;

import java.util.ArrayList;

public class Bridge {
    public String ipAddress;
    public String name;
    public String port;
    public String token;
    public ArrayList<LightBulb> lightBulbs;

    public Bridge(String ipAddress, String name, String port) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.port = port;
        this.lightBulbs = new ArrayList<>();
    }
}
