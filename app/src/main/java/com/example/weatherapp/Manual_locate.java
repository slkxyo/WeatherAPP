package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amap.api.services.core.PoiItemV2;

import java.util.ArrayList;
import java.util.Date;

public class Manual_locate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manual_locate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //声明各个控件
        EditText et = findViewById(R.id.edit);
        TextView tv1  = findViewById(R.id.tv_item0);
        TextView tv2  = findViewById(R.id.tv_item1);
        TextView tv3  = findViewById(R.id.tv_item2);
        TextView tv4  = findViewById(R.id.tv_item3);
        TextView tv5  = findViewById(R.id.tv_item4);
        ArrayList<Double> position = new ArrayList<>();
        ArrayList<String> dist = new ArrayList<>();

        //处理点击事件
        Button bt = findViewById(R.id.bt_search);
        Locate locater = new Locate(this,this);
        bt.setOnClickListener(e ->{
            //检查keyword合法性
            String keyword = et.getText().toString();
            if(keyword.trim().isEmpty()){
                Toast.makeText(this,"请输入搜索地址",Toast.LENGTH_SHORT).show();
                return;
            }

            //启动手动定位(耗时操作，放到线程里)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    locater.manualLocate(keyword, new Locate.ManualLocateListener() {
                        @Override
                        public void onSuccess(ArrayList<PoiItemV2> items) {
                            Log.v("MyTag","Poi got result");
                            //处理locater返回数据
                            ArrayList<String> poi_results = new ArrayList<>();
                            for(int i=0;i<5;i++){
                                PoiItemV2 item = items.get(i);
                                poi_results.add(item.getCityName()+"  "+item.getAdName());
                                position.add(item.getLatLonPoint().getLongitude());
                                position.add(item.getLatLonPoint().getLatitude());
                                dist.add(item.getAdName());
                            }
                            //更新UI
                            Manual_locate.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv1.setText(poi_results.get(0));
                                    tv2.setText(poi_results.get(1));
                                    tv3.setText(poi_results.get(2));
                                    tv4.setText(poi_results.get(3));
                                    tv5.setText(poi_results.get(4));
                                }
                            });

                        }
                        @Override
                        public void onError(Exception e) {

                        }
                    });

                }// end fo run()
            }).start();//end of new Thread()
        });//end of listener

        tv1.setOnClickListener(e ->{
            String text  = tv1.getText().toString();
            if(text.trim().isEmpty())
                return;
            double x = position.get(0);
            double y = position.get(1);

            //结束自身并返回数据给主界面
            Intent returnIntent = new Intent();
            returnIntent.putExtra("x", x);
            returnIntent.putExtra("y", y);
            returnIntent.putExtra("dist",dist.get(0));
            setResult(Activity.RESULT_OK, returnIntent);
            Manual_locate.this.finish();
        });
        tv2.setOnClickListener(e ->{
            String text  = tv2.getText().toString();
            if(text.trim().isEmpty())
                return;
            double x = position.get(2);
            double y = position.get(3);

            //结束自身并返回数据给主界面
            Intent returnIntent = new Intent();
            returnIntent.putExtra("x", x);
            returnIntent.putExtra("y", y);
            returnIntent.putExtra("dist",dist.get(1));
            setResult(Activity.RESULT_OK, returnIntent);
            Manual_locate.this.finish();
        });
        tv3.setOnClickListener(e ->{
            String text  = tv3.getText().toString();
            if(text.trim().isEmpty())
                return;
            double x = position.get(4);
            double y = position.get(5);

            //结束自身并返回数据给主界面
            Intent returnIntent = new Intent();
            returnIntent.putExtra("x", x);
            returnIntent.putExtra("y", y);
            returnIntent.putExtra("dist",dist.get(2));
            setResult(Activity.RESULT_OK, returnIntent);
            Manual_locate.this.finish();
        });
        tv4.setOnClickListener(e ->{
            String text  = tv4.getText().toString();
            if(text.trim().isEmpty())
                return;
            double x = position.get(6);
            double y = position.get(7);

            //结束自身并返回数据给主界面
            Intent returnIntent = new Intent();
            returnIntent.putExtra("x", x);
            returnIntent.putExtra("y", y);
            returnIntent.putExtra("dist",dist.get(3));
            setResult(Activity.RESULT_OK, returnIntent);
            Manual_locate.this.finish();
        });
        tv5.setOnClickListener(e ->{
            String text  = tv5.getText().toString();
            if(text.trim().isEmpty())
                return;
            double x = position.get(8);
            double y = position.get(9);

            //结束自身并返回数据给主界面
            Intent returnIntent = new Intent();
            returnIntent.putExtra("x", x);
            returnIntent.putExtra("y", y);
            returnIntent.putExtra("dist",dist.get(4));
            setResult(Activity.RESULT_OK, returnIntent);
            Manual_locate.this.finish();
        });
    }//end of onCreate()
}