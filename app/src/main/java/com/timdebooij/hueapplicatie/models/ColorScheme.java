package com.timdebooij.hueapplicatie.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class ColorScheme implements Parcelable {
    @NonNull
    @PrimaryKey
    private String schemeName;
    private int hue;
    private int sat;
    private int bri;

    public ColorScheme(){

    }

    protected ColorScheme(Parcel in) {
        schemeName = in.readString();
        hue = in.readInt();
        sat = in.readInt();
        bri = in.readInt();
    }

    public static final Creator<ColorScheme> CREATOR = new Creator<ColorScheme>() {
        @Override
        public ColorScheme createFromParcel(Parcel in) {
            return new ColorScheme(in);
        }

        @Override
        public ColorScheme[] newArray(int size) {
            return new ColorScheme[size];
        }
    };

    @NonNull
    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(@NonNull String schemeName) {
        this.schemeName = schemeName;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public int getBri() {
        return bri;
    }

    public void setBri(int bri) {
        this.bri = bri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(schemeName);
        parcel.writeInt(hue);
        parcel.writeInt(sat);
        parcel.writeInt(bri);
    }
}
