package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {

    private List<HourlyWeather> mData;

    public HourlyWeatherAdapter(List<HourlyWeather> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyWeather weather = mData.get(position);
        holder.timeTextView.setText(weather.getTime());
        holder.temperatureTextView.setText(weather.getTemperature());
        holder.weatherIconImageView.setImageResource(weather.getWeatherIconRes());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        ImageView weatherIconImageView;
        TextView temperatureTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
        }
    }
}
