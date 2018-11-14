package com.timdebooij.hueapplicatie.models;

public class LightBulb {
    public String id;
    private String name;
    private boolean on;
    private int hue;
    private int sat;
    private int bri;

    public LightBulb(String id, String name, boolean on, int hue, int sat, int bri) {
        this.id = id;
        this.name = name;
        this.on = on;
        this.hue = hue;
        this.sat = sat;
        this.bri = bri;
    }

    @Override
    public String toString() {
        return "##name: " + name + " ##id: " + id + " ##on: " + on + " ##hue: " + hue + " ##sat: " + sat + " ##bri: " + bri;
    }
}
