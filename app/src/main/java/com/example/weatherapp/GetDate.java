package com.example.weatherapp;

import android.os.Build;
import android.util.Log;

import java.security.PublicKey;
import java.time.LocalDate;
import java.time.ZoneId;

public class GetDate {
    public String tomorrow = "";
    public String afterTomorrow = "";
    private LocalDate ld;

    public GetDate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ld = LocalDate.now();
            prepareTime();
        }
    }
    public GetDate(String zoneId){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                ZoneId id = ZoneId.of(zoneId);
                ld = LocalDate.now(id);
            }
            catch (Exception e){
                Log.v("Mylog","Zone Id is not valid");
            }
            prepareTime();
        }
    }
    private void prepareTime(){
        LocalDate tomo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tomo = ld.plusDays(1);
        }
        LocalDate afterTomo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            afterTomo = ld.plusDays(2);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tomorrow = tomo.getMonthValue() + "/" + tomo.getDayOfMonth();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            afterTomorrow = afterTomo.getMonthValue() + "/" + afterTomo.getDayOfMonth();
        }
    }
}
