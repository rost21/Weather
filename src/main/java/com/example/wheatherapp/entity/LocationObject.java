package com.example.wheatherapp.entity;

public class LocationObject {
    private int id;
    private String locationCity;
    private String weatherInfo;

    public LocationObject(int id, String locationCity, String weatherInfo) {
        this.id = id;
        this.locationCity = locationCity;
        this.weatherInfo = weatherInfo;
    }

    public int getId() {
        return id;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }
}
