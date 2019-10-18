package com.beetech.module.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.GpsDataBean;
import com.beetech.module.gps.GPSInfo;
import com.beetech.module.gps.GpsContent;
import com.beetech.module.utils.DateUtils;
import com.rscja.deviceapi.BDNavigation;
import com.rscja.deviceapi.exception.ConfigurationException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by zhangcs on 2019-09-20.
 */
public class GPSService extends Service {
    public static final String TAG = GPSService.class.getSimpleName();

    MyApplication myApp;
    private LocationManager locationManager;

    public static long lastLocationChanged;
    public static long validPointCnt;
    long lastSpeakOnline;
    long lastUpdateView;
    static int gps_mode;

    static final int MODE_GPS = 1;
    static final int MODE_BD = 2;

    static StringBuffer gpsContentSb = new StringBuffer();
    GpsContent gpsContent = new GpsContent();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = (MyApplication)getApplicationContext();

        lastSpeakOnline = 0;
        validPointCnt = 0;
        lastUpdateView = 0;
        gps_mode = 0;
        lastLocationChanged = SystemClock.elapsedRealtime();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        registerListener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BDNavigation.getInstance();
                    if (BDNavigation.getInstance().open()) {
                        BDNavigation.getInstance().addTestBDRawDataListener(new BDNavigation.TestResultRawData() {
                            @Override
                            public void ResultALLRawData(String s) {
                                GPSInfo.setBdAvailable(true);
                                gpsContentSb.append(s);
                                parseGpsContentSb();
                            }

                            @Override
                            public void ResultALLRawData(byte[] bytes, int i) {
                                //String str = new String(bytes);
                            }
                        });
                    }
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterListener();
        try {
            BDNavigation.getInstance().close();
            BDNavigation.getInstance().free();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 返回查询条件
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(false);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
    }

    public static double dm2dd(String dm){
        int index = dm.indexOf('.');
        if (index  >= 2){
            return (Integer.valueOf(dm.substring(0, index-2))+ Double.valueOf(dm.substring(index-2))/60.0);
        } else {
            return 0.0;
        }
    }

    /*解析GPS 标准NMEI格式数据*/
    void parseGpsContentSb(){
        if (gpsContentSb == null || gpsContentSb.length()==0){
            return;
        }
        try {
            printLog("gpsContentSb="+gpsContentSb);
            printLog("===================gpsContentSb.length="+gpsContentSb.length());
            int $indexLast = gpsContentSb.indexOf("$");
            int $indexNext = -1;
            do{
                $indexNext = gpsContentSb.indexOf("$", $indexLast+1);
                String str = gpsContentSb.substring($indexLast, $indexNext);
                gpsContentSb.delete($indexLast, $indexNext);
                Log.d(TAG, " parseGpsContentSb.str="+str);
                printLog(str);
                String[] sValues = str.split(",", 20);
                printLog(sValues[0] + " length:" + sValues.length);

                if (sValues.length >= 12 && sValues[0].contains("GGA")) {
                    if (sValues[1].length() > 0) {
                        Date utcTimeOld = DateUtils.parseStringToDate(sValues[1].substring(0, 6), DateUtils.C_HHMMSS1);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(utcTimeOld);
                        cal.add(Calendar.HOUR_OF_DAY, 8);

                        Calendar cal_gps = Calendar.getInstance();
                        cal_gps.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                        cal_gps.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                        cal_gps.set(Calendar.SECOND, cal.get(Calendar.SECOND));
                        cal_gps.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));

                        gpsContent.setGpsTime(cal_gps.getTime());
                    }
                    if (sValues[2].length() > 0) {
                        double lat = dm2dd(sValues[2]);
                        gpsContent.setLat(lat);
                    }
                    if (sValues[4].length() > 0) {
                        double lng = dm2dd(sValues[4]);
                        gpsContent.setLng(lng);
                    }
                    if (sValues[6].length() > 0) {
                        int state = Integer.valueOf(sValues[6]);
                        gpsContent.setState(state);
                    }
                    if (sValues[7].length() > 0) {
                        int useCnt = Integer.valueOf(sValues[7]);
                        gpsContent.setUsedCnt(useCnt);
                    }
                    if (sValues[9].length() > 0) {
                        double altitude = Double.valueOf(sValues[9]);
                        gpsContent.setAltitude(altitude);
                    }
                    printLog("$GNGGA, lat = "+gpsContent.getLat()+", lng="+gpsContent.getLng()+", state="+gpsContent.getState()+", useCnt="+gpsContent.getUsedCnt()+", altitude="+gpsContent.getAltitude()+", gpsTime="+gpsContent.getGpsTime());
                    myApp.gpsContent =  gpsContent;
                    try {
                        if (myApp.monitorState == 1) {
                            Calendar cal_gps = Calendar.getInstance();
                            cal_gps.setTime(gpsContent.getGpsTime());
                            int second = cal_gps.get(Calendar.SECOND);
                            double lng = gpsContent.getLng();
                            double lat = gpsContent.getLat();

                            if(second % 30 == 0 && lng != 0 && lat != 0){
                                GpsDataBean gpsDataBean = new GpsDataBean(gpsContent);
//                                myApp.gpsDataSDDao.save(gpsDataBean);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG,"gpsDataSDDao save Exception");
                    }
                }

                $indexLast = gpsContentSb.indexOf("$");
            }while(gpsContentSb.indexOf("$", $indexLast+1) > 0);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            printLog( "PERMISSION_GRANTED not alllowed !");
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        updateView(location);
        // 监听状态
        locationManager.addGpsStatusListener(listener);
//        locationManager.requestLocationUpdates(bestProvider, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    /**
     * 实时更新文本内容
     */
    private void updateView(Location location) {
        if (location != null) {
            if (SystemClock.elapsedRealtime() - lastLocationChanged < 5000){
                validPointCnt++;
                if (validPointCnt == 5){
//                    SoundManager.getInstance().playSound("定位成功");
                    Log.d(TAG, "定位成功");
                }
            } else {
                validPointCnt = 0;
            }
            lastLocationChanged = SystemClock.elapsedRealtime();
            GPSInfo.setLongitude(location.getLongitude());
            GPSInfo.setLatitude(location.getLatitude());
            if (location.hasSpeed()) {
                GPSInfo.setSpeed(location.getSpeed());
            } else {
                GPSInfo.setSpeed(0);
            }
            GPSInfo.setAltitude(location.getAltitude());
            GPSInfo.setTime(location.getTime());
        }
    }

    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {

            // 获取当前状态
            if (ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);

            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    printLog( "第一次定位");


                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    printLog( "卫星状态改变");

                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                            .iterator();
                    int count = 0;// 可视卫星数

                    int usedCount = 0;// 已连接卫星数
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                        if (s.usedInFix()) {
                            usedCount++;
                        } else {
                        }

                    }
                    GPSInfo.setUseCount(usedCount);
                    GPSInfo.setViewCount(count);
                    GPSInfo.setGpsAvailable(true);
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    printLog( "定位启动");

                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    printLog( "定位结束");
                    break;
            }
        }

        ;
    };

    // 位置监听
    private LocationListener locationListener = new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            if (gps_mode == 0){
                gps_mode = MODE_GPS;
            }
            if (gps_mode == MODE_GPS) {
                updateView(location);
            }
            printLog( "时间：" + new Date(location.getTime()));
            printLog( "经度：" + location.getLongitude());
            printLog( "纬度：" + location.getLatitude());
            printLog( "速度：" + location.getSpeed());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    printLog( "当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    printLog( "当前GPS状态为可见状态");
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        }

    };

    /**
     * 移除监听
     */
    private void unregisterListener() {
        if (locationManager != null) {
            locationManager.removeGpsStatusListener(listener);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

    void printLog(String txt){
        Log.i(TAG, txt);
    }

    public static void main(String[] args) {
        String $GNGGA = "$GNGGA,080337.00,3131.39878,N,12019.02092,E,1,12,0.74,25.6,M,7.1,M,,*45";
        String[] sValues = $GNGGA.split(",", 20);
        double lat = dm2dd(sValues[2]);
        double lng = dm2dd(sValues[4]);
        System.out.println(lat+","+lng);
    }
}
