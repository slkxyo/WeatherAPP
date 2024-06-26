package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class WeatherInfo {
    public HashMap<String, Object> basic,moredays,index;

    public WeatherInfo(double x,double y){
        basic = new HashMap<>();
        moredays = new HashMap<>();
        index = new HashMap<>();
        try {
            String realtime = IOUtils.toString(new URL(String.format("https://api.caiyunapp.com/v2.6/2q0N4gfM2ndO5QJh/%f,%f/realtime",x,y)), StandardCharsets.UTF_8);
            JsonObject jo = new JsonParser().parse(realtime).getAsJsonObject().getAsJsonObject("result").getAsJsonObject("realtime");
            basic.put("timezone",new JsonParser().parse(realtime).getAsJsonObject().get("timezone").getAsString());
            basic.put("current_temp", jo.get("temperature").getAsDouble());
            basic.put("current_icon",jo.get("skycon").getAsString());
            index.put("wind_speed",jo.getAsJsonObject("wind").get("speed").getAsDouble());
            index.put("apparent_temperature",jo.get("apparent_temperature").getAsString());
            double chn = jo.getAsJsonObject("air_quality").getAsJsonObject("aqi").get("chn").getAsDouble();
            index.put("air_des",jo.getAsJsonObject("air_quality").getAsJsonObject("description").get("chn").getAsString());
            index.put("air_chn",chn);
            index.put("ultraviolet",jo.getAsJsonObject("life_index").getAsJsonObject("ultraviolet").get("desc").getAsString());
            index.put("humidity",jo.get("humidity").getAsDouble());
            index.put("visibility",jo.get("visibility").getAsDouble());

            @SuppressLint("DefaultLocale") String hour = IOUtils.toString(new URL(String.format("https://api.caiyunapp.com/v2.6/2q0N4gfM2ndO5QJh/%f,%f/hourly?hourlysteps=24",x,y)), StandardCharsets.UTF_8);
            JsonObject joHour = new JsonParser().parse(hour).getAsJsonObject().getAsJsonObject("result").getAsJsonObject("hourly");
            double[] hoursTemp = new double[24];
            getHoursTem(hoursTemp,joHour.getAsJsonArray("temperature"));
            basic.put("hourly_temp",hoursTemp);

            String[] hourIcon = new String[24];
            getHoursIcon(hourIcon,joHour.getAsJsonArray("skycon"));
            basic.put("hourly_icon",hourIcon);

            @SuppressLint("DefaultLocale") String day = IOUtils.toString(new URL(String.format("https://api.caiyunapp.com/v2.6/2q0N4gfM2ndO5QJh/%f,%f/daily?dailysteps=3",x,y)), StandardCharsets.UTF_8);
            JsonObject joDay = new JsonParser().parse(day).getAsJsonObject().getAsJsonObject("result").getAsJsonObject("daily");
            JsonArray tempsArray = joDay.getAsJsonArray("temperature");
            double[]  temps  = new double[6];
            temps[0] = tempsArray.get(0).getAsJsonObject().get("max").getAsDouble();
            temps[1] = tempsArray.get(0).getAsJsonObject().get("min").getAsDouble();
            temps[2] = tempsArray.get(1).getAsJsonObject().get("max").getAsDouble();
            temps[3] = tempsArray.get(1).getAsJsonObject().get("min").getAsDouble();
            temps[4] = tempsArray.get(2).getAsJsonObject().get("max").getAsDouble();
            temps[5] = tempsArray.get(2).getAsJsonObject().get("min").getAsDouble();
            moredays.put("temp",temps);

            JsonArray iconArray = joDay.getAsJsonArray("skycon");
            String[]  icons  = new String[3];
            icons[0] = iconArray.get(0).getAsJsonObject().get("value").getAsString();
            icons[1] = iconArray.get(1).getAsJsonObject().get("value").getAsString();
            icons[2] = iconArray.get(2).getAsJsonObject().get("value").getAsString();
            moredays.put("icon",icons);

            JsonArray coldArray = joDay.getAsJsonObject("life_index").getAsJsonArray("coldRisk");
            String[] colds = new String[3];
            colds[0] = coldArray.get(0).getAsJsonObject().get("desc").getAsString();
            colds[1] = coldArray.get(1).getAsJsonObject().get("desc").getAsString();
            colds[2] = coldArray.get(2).getAsJsonObject().get("desc").getAsString();
            index.put("cold_risk",colds);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void getHoursTem(double[] tems,JsonArray jsonArray){
        for(int i=0;i<24;i++){
            tems[i] = jsonArray.get(i).getAsJsonObject().get("value").getAsDouble();
        }
    }
    private void getHoursIcon(String[] icons,JsonArray jsonArray){
        for(int i=0;i<24;i++){
            icons[i] =  jsonArray.get(i).getAsJsonObject().get("value").getAsString();
        }
    }
    private static String determineAirQuality(double aqi) {
        // 根据AQI判断空气质量类别，这里简化为几个示例条件
        if (aqi <= 50) {
            return "优";
        } else if (aqi <= 100) {
            return "良";
        } else if (aqi <= 150) {
            return "轻度污染";
        } else if (aqi <= 200) {
            return "中度污染";
        } else if (aqi <= 300) {
            return "重度污染";
        } else {
            return "严重污染";
        }
    }
}
