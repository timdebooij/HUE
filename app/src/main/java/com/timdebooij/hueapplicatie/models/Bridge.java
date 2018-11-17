package com.timdebooij.hueapplicatie.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Bridge implements Parcelable {
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

    protected Bridge(Parcel in) {
        ipAddress = in.readString();
        name = in.readString();
        port = in.readString();
        token = in.readString();
        lightBulbs = in.readArrayList(LightBulb.class.getClassLoader());
    }

    public static final Creator<Bridge> CREATOR = new Creator<Bridge>() {
        @Override
        public Bridge createFromParcel(Parcel in) {
            return new Bridge(in);
        }

        @Override
        public Bridge[] newArray(int size) {
            return new Bridge[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ipAddress);
        parcel.writeString(name);
        parcel.writeString(port);
        parcel.writeString(token);
        parcel.writeList(lightBulbs);
    }
}
