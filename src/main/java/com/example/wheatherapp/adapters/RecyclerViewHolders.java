package com.example.wheatherapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.wheatherapp.R;
public class RecyclerViewHolders extends RecyclerView.ViewHolder {
    private static final String TAG = RecyclerViewHolders.class.getSimpleName();
    public TextView dayOfWeek;
    public ImageView weatherIcon;
    public TextView weatherResult;

    public RecyclerViewHolders(final View itemView){
        super(itemView);
        dayOfWeek = itemView.findViewById(R.id.day_of_week);
        weatherIcon = itemView.findViewById(R.id.weather_icon);
        weatherResult = itemView.findViewById(R.id.weather_result);
    }
}
