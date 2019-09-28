package com.beetech.module.gps;

import android.os.SystemClock;

import com.beetech.module.service.GPSService;

import java.util.Calendar;

/**
 * Created by Administrator on 2016-06-19.
 */
public class GPSInfo {

    private static double longitude=0;
    private static double latitude=0;
    private static double speed=0;
    private static int viewCount=0;
    private static int useCount=0;
    private static double altitude=0;
    private static int angle=0;
    private static  boolean accOn = false;

    public static boolean isAccAvailable() {
        return accAvailable;
    }

    public static void setAccAvailable(boolean accAvailable) {
        GPSInfo.accAvailable = accAvailable;
    }

    public static boolean isBdAvailable() {
        return bdAvailable;
    }

    public static void setBdAvailable(boolean bdAvailable) {
        GPSInfo.bdAvailable = bdAvailable;
    }

    public static boolean isGpsAvailable() {
        return gpsAvailable;
    }

    public static void setGpsAvailable(boolean gpsAvailable) {
        GPSInfo.gpsAvailable = gpsAvailable;
    }

    private static  boolean gpsAvailable = false;
    private static  boolean accAvailable = false;
    private static  boolean bdAvailable = false;

    public static double getTotalMiles() {
        return totalMiles;
    }

    public static void setTotalMiles(double totalMiles) {
        GPSInfo.totalMiles = totalMiles;
    }

    private static double totalMiles = 0;

    public static synchronized boolean isAccOn() {
        return accOn;
    }

    public static synchronized void setAccOn(boolean accOn) {
        GPSInfo.accOn = accOn;
    }
    public static synchronized long getTime() {
        return time;
    }

    public static synchronized void setTime(long dateTime) {
        GPSInfo.time = dateTime;
    }

    private static long time;

    public static  synchronized double getAltitude() {
        return altitude;
    }

    public static  synchronized void setAltitude(double altitude) {
        GPSInfo.altitude = altitude;
    }

    public static  synchronized double getLatitude() {
        return latitude;
    }
    public static  synchronized void setLatitude(double latitude) {
        GPSInfo.latitude = latitude;
    }

    public static  synchronized double getLongitude() {
        return longitude;
    }

    public static  synchronized void setLongitude(double longitude) {
        GPSInfo.longitude = longitude;
    }

    public static  synchronized double getSpeed() {
        if (isValid()) {
            return (speed * 3.6);
        } else {
            return 0;
        }
    }

    public static  synchronized void setSpeed(double speed) {
        GPSInfo.speed = speed;
    }

    public static  synchronized int getUseCount() {
        return useCount;
    }

    public static  synchronized boolean isValid() {
        return (GPSService.validPointCnt > 5 && SystemClock.elapsedRealtime() - GPSService.lastLocationChanged < 5000);
    }

    public static  synchronized void setUseCount(int useCount) {
        GPSInfo.useCount = useCount;
    }

    public static  synchronized int getViewCount() {
        return viewCount;
    }

    public static  synchronized void setViewCount(int viewCount) {
        GPSInfo.viewCount = viewCount;
    }

    public static  synchronized int getAngle() {
        return angle;
    }

    public static  synchronized void setAngle(int angle) {
        GPSInfo.angle = angle;
    }

    private static byte intToHex(int val) {
        return (byte) (((val / 10) << 4) + val % 10);
    }
    private static byte[] getDateTime() {
        byte[] dateTime = new byte[6];
        int[] val = new int[6];

        Calendar c = Calendar.getInstance();
        val[0] = c.get(Calendar.YEAR) - 2000;
        val[1] = c.get(Calendar.MONTH) + 1;
        val[2] = c.get(Calendar.DATE);
        val[3] = c.get(Calendar.HOUR_OF_DAY);
        val[4] = c.get(Calendar.MINUTE);
        val[5] = c.get(Calendar.SECOND);
        for (int i = 0; i < 6; i++) {
            dateTime[i] = intToHex(val[i]);
        }
        return dateTime;
    }

    public static byte[] getGpsData(){
        return getGpsData(false);
    }
    private static byte[] getGpsData(boolean tenMetre) {
        byte[] gpsData = new byte[28];
        byte[] temp;
        long warmingFlag;//报警信息
        int state;//状态信息
        long latitude;//维度乘以10的6次方
        long longtitude;//精度，运算同上
        int altitude;//海拔高度，单位为米
        int speed;//GPS速度信息 单位：1/10km/h
        int orientition;//方向0~359
        byte[] time; //BCD编码yymmddhhmmss

//        warmingFlag = RunningCache.getGpsWarningFlag();
        state = 0;
        if (GPSInfo.isAccOn()){
            state |= 1;
        }
        if (GPSInfo.isValid()){
            state |= 1<<1;
        }
        if (tenMetre) {
            state |= 1 << 7;//表示分钟学时单位为10M
        }
        latitude = (long) (GPSInfo.getLatitude() * 1000000);
        longtitude =(long)(GPSInfo.getLongitude() * 1000000);
        altitude = (int) GPSInfo.getAltitude();
        speed = (int) (GPSInfo.getSpeed() * 10);
        orientition = GPSInfo.getAngle();
        time = getDateTime();
        if (altitude <= 0){//屏蔽负数
            altitude = 0;
        }
//        temp = StringUtils.longToBytes(warmingFlag);
//        System.arraycopy(temp, 0, gpsData, 0, 4);
//        temp = StringUtils.longToBytes(state);
//        System.arraycopy(temp, 0, gpsData, 4, 4);
//        temp = StringUtils.longToBytes(latitude);
//        System.arraycopy(temp, 0, gpsData, 8, 4);
//        temp = StringUtils.longToBytes(longtitude);
//        System.arraycopy(temp, 0, gpsData, 12, 4);
//        temp = StringUtils.intToBytes(speed);
//        System.arraycopy(temp, 0, gpsData, 16, 2);
//        temp = StringUtils.intToBytes(speed);
//        System.arraycopy(temp, 0, gpsData, 18, 2);
//        temp = StringUtils.intToBytes(orientition);
//        System.arraycopy(temp, 0, gpsData, 20, 2);
//        System.arraycopy(time, 0, gpsData, 22, 6);
        return gpsData;
    }

    public static byte[] getGnssData() {
     return getGnssData(false);
    }

    public static byte[] getGnssData(boolean tenMetre) {
        byte[] gnssData = new byte[38];
        System.arraycopy(getGpsData(tenMetre), 0, gnssData, 0, 28);
        gnssData[28] = 0x01;//总里程
        gnssData[29] = 4;
        long mile = (long)(totalMiles/100);
        gnssData[30] = (byte)((mile>>24)&0xFF);
        gnssData[31] = (byte)((mile>>16)&0xFF);
        gnssData[32] = (byte)((mile>>8)&0xFF);
        gnssData[33] = (byte)((mile)&0xFF);

        gnssData[34] = 0x05;//发动机转速
        gnssData[35] = 2;
        gnssData[36] = (byte)((1000>>8)&0xFF);
        gnssData[37] = (byte)(1000&0xFF);
        return gnssData;
    }
}
