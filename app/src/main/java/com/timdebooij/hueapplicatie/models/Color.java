package com.timdebooij.hueapplicatie.models;

public class Color {
    public int hue;
    public int bri;
    public int sat;
    public int colorInt;

    public Color(int hue, int sat, int bri) {
        this.hue = hue;
        this.bri = bri;
        this.sat = sat;
        getColorInt();
    }

    public void getColorInt(){
        float huef = (float)hue/65535*360;
        float satf = (float)sat/254;
        float brif = (float)bri/254;
        float[] hsb = {huef, satf, brif};
        colorInt = android.graphics.Color.HSVToColor(hsb);
    }
}
