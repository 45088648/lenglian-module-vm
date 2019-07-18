package com.beetech.module.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 获取手机信息工具类
 */
public class MobileInfoUtil {

    /**
     * 获取手机IMEI
     * 
     * @param context
     * @return
     */
    public static final String getIMEI(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            String imei = telephonyManager.getImei();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static TelephonyManager getTelephonyManager(Context context) {
        // 获取telephony系统服务，用于取得SIM卡和网络相关信息
        TelephonyManager mTelephonyManager = null;
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    /**
     * 获取手机IMSI
     */
    public static String getIMSI(Context context){
        try {
            TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI号
            String imsi=telephonyManager.getSubscriberId();
            if(null==imsi){
                imsi="";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}