package com.beetech.module.listener;

import android.content.Context;
import android.util.Log;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.GpsDataBean;
import java.util.LinkedList;

public class MyBDLocationListener extends BDAbstractLocationListener {
    private final static String TAG = MyBDLocationListener.class.getSimpleName();

    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    private MyApplication myApp;
    private Context mContext;

    public MyBDLocationListener(Context mContext){
        this.mContext = mContext;
        this.myApp = (MyApplication) mContext.getApplicationContext();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        Log.d(TAG, "onReceiveLocation, location="+location+", LocType = "+(location != null ? location.getLocType() : "location is null"));
        if (location != null && (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeOffLineLocation)) {
            myApp.location = location;
            GpsDataBean gpsDataBean = new GpsDataBean(location);
            if(myApp.monitorState == 1) {
                myApp.gpsDataSDDao.save(gpsDataBean);
            }
        }
    }

     /**
     * 封装定位结果和时间的实体类
     */
    class LocationEntity {
        BDLocation location;
        long time;
    }
}
