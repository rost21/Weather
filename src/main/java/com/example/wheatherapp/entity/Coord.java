package com.example.wheatherapp.entity;

import com.google.gson.annotations.SerializedName;

public class Coord {
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;

    public Coord(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
