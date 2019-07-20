package com.beetech.module.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;
import java.util.List;

public class DevStateUtils {
    private final static String TAG = DevStateUtils.class.getSimpleName();

    public static StringBuffer getDevState(Context context){
        long beginTimeInMills = System.currentTimeMillis();
        StringBuffer devStateSb = null;
        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            try {
                if(!myApp.usbConnected && !ChargingUtils.isCharging(myApp)){
                    myApp.power = 0;
                } else {
                    myApp.power = 1;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            //dev_state|1440020706789|aaa|100|1|2.31|20190522134740|61172|18427|0|IMEI|863691010960419|
            devStateSb = new StringBuffer("dev_state|8888888888|aaaaaa|").append(myApp.batteryPercent).append("|"+myApp.power+"|")
                    .append(Constant.verName).append("|")
                    .append(DateUtils.getCurrentDateStr(DateUtils.C_YYYYMMDDHHMMSS))
                    .append("|");
            //获取手机基站信息
            int mcc = 460;
            int mnc = 0;
            int cid = 0;
            int lac = 0;
            try {
                if(hasSimCard(myApp)){
                    TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String operator = manager.getNetworkOperator();
                    if(!StringUtils.isBlank(operator)){
                        /**通过operator获取 MCC 和MNC */
                        mcc = Integer.parseInt(operator.substring(0, 3));
                        mnc = Integer.parseInt(operator.substring(3));
                    }

                    if(manager.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA){
                        GsmCellLocation gsmCellLocation = (GsmCellLocation) manager.getCellLocation();
                        if(gsmCellLocation != null){
                            cid = gsmCellLocation.getCid(); //获取gsm基站识别标号
                            lac = gsmCellLocation.getLac(); //获取gsm网络编号
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "获取基站信息异常 ", e);
            }

            try {
                int dbm = 0;
                myApp.netWorkType = NetUtils.getNetworkState(myApp);
                if (myApp.netWorkType == NetUtils.NETWORK_WIFI) {
                    dbm = getWifiRssi(myApp);
                }else if(myApp.netWorkType == NetUtils.NETWORK_2G || myApp.netWorkType == NetUtils.NETWORK_3G || myApp.netWorkType == NetUtils.NETWORK_4G ){
                    dbm = getMobileDbm(myApp);
                }
                myApp.signalStrength = dbm;
            } catch (Exception e) {
                e.printStackTrace();
            }
            devStateSb.append(mnc).append("|").append(lac).append("|").append(cid)
                    .append("|IMEI|").append(Constant.imei)
                    .append("|-2|100|33|0|")
                    .append(Math.abs(myApp.signalStrength)).append("|");

            Log.d(TAG, "dev_state="+devStateSb.toString());
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "getDevState 异常 ", e);

        } finally {
            Log.d(TAG, "getDevState 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }
        return devStateSb;
    }

    /**
     * 判断是否包含SIM卡
     */
    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        Log.d(TAG, result ? "有SIM卡" : "无SIM卡");
        return result;
    }

    /**
     * 获取手机信号强度，需添加权限 android.permission.ACCESS_COARSE_LOCATION <br>
     * API要求不低于17 <br>
     *
     * @return 当前手机主卡信号强度,单位 dBm（-1是默认值，表示获取失败）
     */
    public static int getMobileDbm(Context context)
    {
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            cellInfoList = tm.getAllCellInfo();
            if (null != cellInfoList)
            {
                for (CellInfo cellInfo : cellInfoList)
                {
                    if (cellInfo instanceof CellInfoGsm)
                    {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm)cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                    }
                    else if (cellInfo instanceof CellInfoCdma)
                    {
                        CellSignalStrengthCdma cellSignalStrengthCdma =
                                ((CellInfoCdma)cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                    }
                    else if (cellInfo instanceof CellInfoWcdma)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                        {
                            CellSignalStrengthWcdma cellSignalStrengthWcdma =
                                    ((CellInfoWcdma)cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthWcdma.getDbm();
                        }
                    }
                    else if (cellInfo instanceof CellInfoLte)
                    {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte)cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthLte.getDbm();
                    }
                }
            }
        }
        return dbm;
    }

    public static int getWifiRssi(Context context){
        WifiManager wifi_service = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        return wifiInfo.getRssi();
    }
}
