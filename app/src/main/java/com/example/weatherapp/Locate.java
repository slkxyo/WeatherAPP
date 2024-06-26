package com.example.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItemV2;
import com.amap.api.services.poisearch.PoiResultV2;
import com.amap.api.services.poisearch.PoiSearchV2;

import java.util.ArrayList;

public class Locate implements PoiSearchV2.OnPoiSearchListener {
    private Context context;
    private Activity activity;
    private AutoLocateListener autoListener;
    private ManualLocateListener manualListener;
    //声明定位客户端(用于自动定位)
    AMapLocationClient mLocationClient = null;

    public Locate(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void manualLocate(String keyWord,ManualLocateListener man){
        this.manualListener = man;
        Log.v("MyTag","Poi started");
        try {
            poisearch(keyWord);
        } catch (AMapException e) {
            Log.v("MyTag","Poi got bugs");
            throw new RuntimeException(e);
        }
    }
    private void poisearch(String keyWord) throws AMapException {
        PoiSearchV2.Query query = new PoiSearchV2.Query(keyWord,"","");
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码

        PoiSearchV2  poiSearch = new PoiSearchV2(this.context, query);
        poiSearch.setOnPoiSearchListener(this);

        poiSearch.searchPOIAsyn();
    }

    public void autoLocate(AutoLocateListener auto){
        this.autoListener = auto;
        // 设置隐私权政策是否弹窗告知用户
        AMapLocationClient.updatePrivacyShow(context, true, true);
        // 设置用户是否已经同意隐私权政策
        AMapLocationClient.updatePrivacyAgree(context, true);

        //检查危险权限
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(activity,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        }


        //初始化定位客户端
        try {
            mLocationClient = new AMapLocationClient(context);
        } catch (Exception e) {
            Log.v("MyTag",e.getMessage());
        }

        //初始化回调回调监听器
        AMapLocationListener mLocationListener = amapLocation -> {
            if (amapLocation != null) {
                //成功定位后的操作
                if (amapLocation.getErrorCode() == 0) {
                    //获取经纬度
                    double x = amapLocation.getLongitude();
                    double y = amapLocation.getLatitude();

                    //获取地区名称
                    String district = amapLocation.getDistrict();
                    if(district.trim().isEmpty())
                        district = amapLocation.getCity();

                    //返回数据
                    autoListener.onSuccess(new double[]{x,y},district);

                    //销毁定位服务及定位客户端
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                }
                else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("MyTag","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                    autoListener.onError(new Exception(amapLocation.getErrorInfo()));
                }
            }
        };

        //定位客户端绑定回调监听器
        mLocationClient.setLocationListener(mLocationListener);


        //初始化选项
        AMapLocationClientOption option = new AMapLocationClientOption();
        //设置选项
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setNeedAddress(true);
        //选项绑定定位客户端
        if(null != mLocationClient){
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    public void onPoiSearched(PoiResultV2 poiResultV2, int rescode) {
        if (rescode == 1000){
            ArrayList<PoiItemV2>  items = poiResultV2.getPois();
            manualListener.onSuccess(items);
        }
        else {
            manualListener.onError(new Exception("Error in poi"));
        }
    }

    @Override
    public void onPoiItemSearched(PoiItemV2 poiItemV2, int i) {

    }

    public interface AutoLocateListener{
        void onSuccess(double[] loc,String dis);
        void onError(Exception e);
    }
    public interface ManualLocateListener{
        void onSuccess(ArrayList<PoiItemV2> items);
        void onError(Exception e);
    }
}
