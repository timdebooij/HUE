package com.timdebooij.hueapplicatie.models;

public class Color {
    public int hue;
    public int bri;
    public int sat;

    public Color(int hue, int sat, int bri) {
        this.hue = hue;
        this.bri = bri;
        this.sat = sat;
    }
}
