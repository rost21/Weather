package com.example.wheatherapp.entity;

import com.google.gson.annotations.SerializedName;

public class ListJsonObject {
    @SerializedName("_id")
    private String _id;
    @SerializedName("name")
    private String name;
    @SerializedName("country")
    private String country;
    @SerializedName("coord")
    private Coord cord;

    public ListJsonObject(String _id, String name, String country, Coord cord) {
        this._id = _id;
        this.name = name;
        this.country = country;
        this.cord = cord;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Coord getCord() {
        return cord;
    }
}
