package com.example.wheatherapp.entity;

public class WeatherObject {
    private String day;
    private int weatherIcon;
    private String weatherResult;
    private String weatherResultSmall;

    public WeatherObject(String day, int weatherIcon, String weatherResult, String weatherResultSmall) {
        this.day = day;
        this.weatherIcon = weatherIcon;
        this.weatherResult = weatherResult;
        this.weatherResultSmall = weatherResultSmall;
    }

    public String getDay() {
        return day;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public String getWeatherResult() {
        return weatherResult;
    }

    public String getWeatherResultSmall() {
        return weatherResultSmall;
    }
}
