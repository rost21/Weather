package com.example.wheatherapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        holder.dayOfWeek.setText(dailyWeather.get(position).getDay());
        holder.weatherIcon.setImageResource(dailyWeather.get(position).getWeatherIcon());

        double mTemp = Double.parseDouble(dailyWeather.get(position).getWeatherResult());
        holder.weatherResult.setText(String.valueOf(Math.round(mTemp)) + "°");

        holder.weatherResultSmall.setText(dailyWeather.get(position).getWeatherResultSmall());
        holder.weatherResultSmall.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dailyWeather.size();
    }
}
