package com.timdebooij.hueapplicatie.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LightBulb implements Parcelable {
    public String id;
    public String name;
    public boolean on;
    public int hue;
    public int sat;
    public int bri;

    public LightBulb(String id, String name, boolean on, int hue, int sat, int bri) {
        this.id = id;
        this.name = name;
        this.on = on;
        this.hue = hue;
        this.sat = sat;
        this.bri = bri;
    }

    protected LightBulb(Parcel in) {
        id = in.readString();
        name = in.readString();
        on = in.readByte() != 0;
        hue = in.readInt();
        sat = in.readInt();
        bri = in.readInt();
    }

    public static final Creator<LightBulb> CREATOR = new Creator<LightBulb>() {
        @Override
        public LightBulb createFromParcel(Parcel in) {

            return new LightBulb(in);
        }

        @Override
        public LightBulb[] newArray(int size) {
            return new LightBulb[size];
        }
    };

    @Override
    public String toString() {
        return "##name: " + name + " ##id: " + id + " ##on: " + on + " ##hue: " + hue + " ##sat: " + sat + " ##bri: " + bri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeByte((byte) (on ? 1 : 0));
        parcel.writeInt(hue);
        parcel.writeInt(sat);
        parcel.writeInt(bri);
    }
}
