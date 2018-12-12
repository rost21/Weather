package com.example.wheatherapp.entity;

public class WeatherObject {
    private String day;
    private String weatherIcon;
    private String temp;

    public WeatherObject(String day, String weatherIcon, String temp) {
        this.day = day;
        this.weatherIcon = weatherIcon;
        this.temp = temp;
    }

    public String getDay() {
        return day;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getTemp() {
        return temp;
    }

}
