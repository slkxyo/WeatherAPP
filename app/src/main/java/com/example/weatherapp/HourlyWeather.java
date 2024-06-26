package com.example.weatherapp;

public class HourlyWeather {
    private String time;
    private int weatherIconRes; // 可以是图片资源的 ID 或者其他表示天气图标的标识
    private String temperature;

    public HourlyWeather(String time, int weatherIconRes,String temperature) {
        this.time = time;
        this.weatherIconRes = weatherIconRes;
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public int getWeatherIconRes() {
        return weatherIconRes;
    }

    public String getTemperature() {
        return temperature;
    }
}

