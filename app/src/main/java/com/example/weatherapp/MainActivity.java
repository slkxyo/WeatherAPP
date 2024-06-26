package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private WeatherInfo weatherInfo;
    private double[] location;
    private HashMap<String ,Object> moredays_gloable;
    private HashMap<String ,Object> basic_gloable;
    private HashMap<String ,Object> index_gloable;
    private String dist;
    Locate locate  = new Locate(this,this);
    Locate.AutoLocateListener autoListener;

    //声明各个控件;
    TextView tv_curr_place ;
    TextView tv_curr_temp ;
    TextView tv_curr_icon ;
    TextView tv_today_temp ;
    TextView tv_day1_icon ;;
    TextView tv_day1_temp ;
    TextView tv_day2_icon ;
    TextView tv_day2_temp ;
    TextView tv_day3_icon ;
    TextView tv_day3_temp ;
    TextView tv_tigan_temp ;
    TextView tv_shidu ;
    TextView tv_air_cond ;
    TextView tv_ziwai ;
    TextView tv_cold_risk ;
    TextView tv_vis ;
    TextView tv_date1 ;
    TextView tv_date2 ;
    TextView tv_date3 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //禁用暗色模式(出于视觉效果目的)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        //初始化各个控件
        tv_curr_place = findViewById(R.id.curr_place);
        tv_curr_temp = findViewById(R.id.curr_temp);
        tv_curr_icon = findViewById(R.id.curr_icon);
        tv_today_temp = findViewById(R.id.today_temp);
        tv_day1_icon = findViewById(R.id.des_day1);
        tv_day1_temp = findViewById(R.id.tem_day1);
        tv_day2_icon = findViewById(R.id.des_day2);
        tv_day2_temp = findViewById(R.id.tem_day2);
        tv_day3_icon = findViewById(R.id.des_day3);
        tv_day3_temp = findViewById(R.id.tem_day3);
        tv_tigan_temp = findViewById(R.id.tigan_temp);
        tv_shidu = findViewById(R.id.shidu);
        tv_air_cond = findViewById(R.id.ari_cond);
        tv_ziwai = findViewById(R.id.ziwaixian);
        tv_cold_risk = findViewById(R.id.cold_risk);
        tv_vis = findViewById(R.id.vis);
        tv_date1 =findViewById(R.id.date_day1);
        tv_date2 =findViewById(R.id.date_day2);
        tv_date3 =findViewById(R.id.date_day3);


        //初始化自动定位
        autoListener = new Locate.AutoLocateListener() {
            @Override
            public void onSuccess(double[] loc, String dist) {
                location = loc;
                MainActivity.this.dist = dist;
                Log.v("MyTag","Locate get result");
                //存储定位信息
                SharedPreferences sp = getSharedPreferences("LocalWeather", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("dist",dist);
                ed.putFloat("x",(float)loc[0]);
                ed.putFloat("y",(float)loc[1]);
                ed.apply();

                //启动天气模块
                new GetWeather().start();
            }

            @Override
            public void onError(Exception e) {

            }
        };

        Button bt_autoLocate = findViewById(R.id.bt_autolocate);
        bt_autoLocate.setOnClickListener(e ->{
            tv_curr_place.setText("定位中");
            locate.autoLocate(autoListener);
        });


        //设置手动定位
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                //处理返回的数据
                                double x = data.getDoubleExtra("x",-10000);
                                double y = data.getDoubleExtra("y",-10000);
                                String dist = data.getStringExtra("dist");
                                if (x != -10000 && y !=-10000)
                                    autoListener.onSuccess(new double[]{x,y},dist);
                            }
                        }
                    }
                }
        );
        Button bt_manual = findViewById(R.id.bt_manuallocate);
        bt_manual.setOnClickListener(e ->{
            Intent intent = new Intent(this, Manual_locate.class);
            activityResultLauncher.launch(intent);
        });

        new GetSp().start();
    }//end of onCreate()
    private void setData(){
    }
    private class GetSp extends Thread{
        @Override
        public void run() {
            super.run();
            //启动自动定位之前首先读取存储数据
            SharedPreferences sp = getSharedPreferences("LocalWeather", MODE_PRIVATE);
            double x = sp.getFloat("x",-10000);
            double y = sp.getFloat("y",-10000);
            String district = sp.getString("dist","0000");
            if(x != -10000 && y!=-10000 && !district.equals("0000")){
                location = new double[]{x,y};
                MainActivity.this.dist = district;
                new GetWeather().start();
            }
            else {
                //启动自动定位
                MainActivity.this.locate.autoLocate(autoListener);
            }
        }
    }
    private class GetWeather extends Thread{
        @Override
        public void run(){
            super.run();
            Log.v("MyTag","GetWeathet Start");
            weatherInfo = new WeatherInfo(location[0],location[1]);
            HashMap<String,Object> basic = basic_gloable=  weatherInfo.basic;
            HashMap<String ,Object> moredays = moredays_gloable = weatherInfo.moredays;
            HashMap<String ,Object> index = index_gloable = weatherInfo.index;
            MainActivity.this.runOnUiThread(() -> {
                //展示当前位置
                if (dist.trim().isEmpty())
                    tv_curr_place.setText("未知位置");
                else
                    tv_curr_place.setText(dist);
                //设置实况天气
                tv_curr_temp.setText(basic.get("current_temp")==null?"00":String.format("%d °C",((Double)basic.get("current_temp")).intValue()));
                tv_curr_icon.setText(setIcon(basic.get("current_icon")==null?"":(String)basic.get("current_icon")));
                double[] temps = moredays.get("temp")==null?null:(double[])moredays.get("temp");
                if(temps != null){
                    tv_today_temp.setText(setDouble(temps[1])+"°C / "+setDouble(temps[0])+"°C");
                }

                //设置小时天气
                setRecycleView();

                //设置未来几天的天气
                String[] moreday_icon = moredays.get("icon")==null?null:(String[])moredays.get("icon");
                double[] moreday_temp = moredays.get("temp")==null?null:(double[]) moredays.get("temp");
                if(moreday_icon != null  && moreday_temp!=null){
                    tv_day1_icon.setText(setIcon(moreday_icon[0]));
                    tv_day2_icon.setText(setIcon(moreday_icon[1]));
                    tv_day3_icon.setText(setIcon(moreday_icon[2]));

                    tv_day1_temp.setText(setDouble(moreday_temp[1]) + "°C / " + setDouble(moreday_temp[0]) + "°C");
                    tv_day2_temp.setText(setDouble(moreday_temp[3]) + "°C / " + setDouble(moreday_temp[2]) + "°C");
                    tv_day3_temp.setText(setDouble(moreday_temp[5]) + "°C / " + setDouble(moreday_temp[4]) + "°C");

                    GetDate gd = new GetDate(basic.get("timezone") == null?"":(String)basic.get("timezone"));
                    tv_date1.setText("今天");
                    tv_date2.setText(gd.tomorrow);
                    tv_date3.setText(gd.afterTomorrow);
                }

                //设置生活指数
                tv_tigan_temp.setText(index.get("apparent_temperature")==null?"00":(String)index.get("apparent_temperature"));
                tv_shidu.setText(index.get("humidity")==null?"00":setHum(index.get("humidity")));
                tv_air_cond.setText(index.get("air_des")==null?"00":(String)index.get("air_des"));
                tv_ziwai.setText(index.get("ultraviolet")==null?"00":(String)index.get("ultraviolet"));
                tv_cold_risk.setText(index.get("cold_risk")==null?"00":((String[])(index.get("cold_risk")))[0]);
                tv_vis.setText(index.get("visibility")==null?"00":setDouble(index.get("visibility")) + " KM");
            });
        }

    }


    private String  setHum(Object obj){
        double hum = (double) obj;
        int intHum = (int)(hum*100);
        return String.valueOf(intHum) + "%";

    }
    private String setDouble(Object obj){
        double res = (double) obj;
        int intRes  = Double.valueOf(res).intValue();
        return Integer.valueOf(intRes).toString();
    }
    private String setIcon(String str){
            str = str.trim();
            if(str.equals("CLEAR_DAY") || str.equals("CLEAR_NIGHT"))
                return "晴";
            if(str.equals("PARTLY_CLOUDY_DAY") || str.equals("PARTLY_CLOUDY_NIGHT"))
                return "多云";
            if(str.equals("CLOUDY"))
                return "阴";
            if(str.equals("LIGHT_HAZE"))
                return "轻度雾霾";
            if(str.equals("MODERATE_HAZE"))
                return "中度雾霾";
            if(str.equals("HEAVY_HAZE"))
                return "重度雾霾";
            if(str.equals("LIGHT_RAIN" ))
                return "小雨";
            if(str.equals("MODERATE_RAIN"))
                return "中雨";
            if(str.equals("HEAVY_RAIN"))
                return "大雨";
            if(str.equals("STORM_RAIN"))
                return "暴雨";
            if(str.equals("FOG"))
                return "雾";
            if(str.equals("LIGHT_SNOW"))
                return "小雪";
            if(str.equals("MODERATE_SNOW"))
                return "中雪";
            if(str.equals("HEAVY_SNOW"))
                return "大雪";
            if(str.equals("STORM_SNOW"))
                return "暴雪";
            if(str.equals("DUST"))
                return "浮尘";
            if(str.equals("SAND"))
                return "沙尘";
            if(str.equals("WIND"))
                return "大风";
            return "";
    }
    private void setRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = 10; // 从资源文件中获取间距大小
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        List<HourlyWeather> list = generateDummyData();
        if (list == null)
            return;
        List<HourlyWeather> hourlyWeatherList = list; // 替换成你的实际数据源
        HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(hourlyWeatherList);
        recyclerView.setAdapter(adapter);
    }
    private List<HourlyWeather> generateDummyData() {
        List<HourlyWeather> data = new ArrayList<>();
        double[] temps = basic_gloable.get("hourly_temp")==null?null:(double[])basic_gloable.get("hourly_temp");
        String[] icons = basic_gloable.get("hourly_icon")==null?null:(String[]) basic_gloable.get("hourly_icon");

        if(temps == null)
            return null;
        else if(icons == null)
            return null;
        for(int i=0;i<24;i++){
            String time = String.format("%d:00",i);
            String temp = setDouble(temps[i])+"°C";
            data.add(new HourlyWeather(time,setHourIcon(icons[i]),temp));
        }
        // 添加更多的数据项
        return data;
    }
    private int setHourIcon(String str){
        str = str.trim();
        if(str.equals("CLEAR_DAY") || str.equals("CLEAR_NIGHT"))
            return R.drawable.sunny;
        if(str.equals("PARTLY_CLOUDY_DAY") || str.equals("PARTLY_CLOUDY_NIGHT"))
            return R.drawable.cloudy;
        if(str.equals("CLOUDY"))
            return R.drawable.yin;
        if(str.equals("LIGHT_HAZE"))
            return R.drawable.xiaomai;
        if(str.equals("MODERATE_HAZE"))
            return R.drawable.zhongmai;
        if(str.equals("HEAVY_HAZE"))
            return R.drawable.damai;
        if(str.equals("LIGHT_RAIN" ))
            return R.drawable.xiaoyu;
        if(str.equals("MODERATE_RAIN"))
            return R.drawable.zhongyu;
        if(str.equals("HEAVY_RAIN"))
            return R.drawable.dayu;
        if(str.equals("STORM_RAIN"))
            return R.drawable.baoyu;
        if(str.equals("FOG"))
            return R.drawable.fog;
        if(str.equals("LIGHT_SNOW"))
            return R.drawable.xiaoxue;
        if(str.equals("MODERATE_SNOW"))
            return R.drawable.zhongxue;
        if(str.equals("HEAVY_SNOW"))
            return R.drawable.daxue;
        if(str.equals("STORM_SNOW"))
            return R.drawable.baoxue;
        if(str.equals("DUST"))
            return R.drawable.fuchen;
        if(str.equals("SAND"))
            return R.drawable.shachen;
        if(str.equals("WIND"))
            return R.drawable.wind;
        return R.drawable.fuchen;
    }
}