package com.example.wheatherapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.wheatherapp.R;
import com.example.wheatherapp.entity.WeatherObject;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private List<WeatherObject> dailyWeather;
    private LayoutInflater layoutInflater;
    protected Context context;

    public RecyclerViewAdapter(Context context, List<WeatherObject> dailyWeather) {
        this.dailyWeather = dailyWeather;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = layoutInflater.inflate(R.layout.weather_daily_list, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        String load = "http://openweathermap.org/img/w/" + dailyWeather.get(position).getWeatherIcon() + ".png";
        holder.dayOfWeek.setText(dailyWeather.get(position).getDay());
        Glide.with(context).load(load).into(holder.weatherIcon);

        double mTempMin = Double.parseDouble(dailyWeather.get(position).getTemp());
        holder.weatherResult.setText(String.valueOf(Math.round(mTempMin)) + "°");


    }

    @Override
    public int getItemCount() {
        return dailyWeather.size();
    }
}
