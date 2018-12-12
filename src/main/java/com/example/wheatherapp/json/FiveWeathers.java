package com.example.wheatherapp.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FiveWeathers {

    private String dt_txt;
    private Main main;
    @SerializedName("weather")
    private List<WeatherResults> conditions;

    public FiveWeathers(String dt_txt, Main main, List<WeatherResults> conditions) {
        this.dt_txt = dt_txt;
        this.main = main;
        this.conditions = conditions;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public Main getMain() {
        return main;
    }

    public WeatherResults getConditions() {
        return conditions.get(0);
    }
}
