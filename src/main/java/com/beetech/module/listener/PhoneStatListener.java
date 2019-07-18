package com.beetech.module.listener;

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
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.beetech.module.application.MyApplication;
import com.beetech.module.utils.NetUtils;

import java.util.List;

//获取信号强度
public class PhoneStatListener extends PhoneStateListener {
    private Context mContext;
    private MyApplication myApp;
    public PhoneStatListener(Context mContext){
        this.mContext = mContext;
        myApp = (MyApplication) mContext.getApplicationContext();
    }

    @Override
    public void onSignalStrengthChanged(int asu) {
        super.onSignalStrengthChanged(asu);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        //获取网络信号强度
        int cdmaDbm = signalStrength.getCdmaDbm();
        int evdoDbm = signalStrength.getEvdoDbm();
//        System.out.println("cdmaDbm=====" + cdmaDbm);
//        System.out.println("evdoDbm=====" + evdoDbm);

        int gsmSignalStrength = signalStrength.getGsmSignalStrength();
        int dbm = -113 + 2 * gsmSignalStrength;
//        System.out.println("dbm===========" + dbm);

        //获取网络类型
        int netWorkType = NetUtils.getNetworkState(mContext);
        MyApplication myApplication = (MyApplication)mContext.getApplicationContext();

        myApplication.netWorkType = netWorkType;
//        myApplication.signalStrength = dbm;

        switch (netWorkType) {
            case NetUtils.NETWORK_WIFI: // 当前网络为wifi
                break;
            case NetUtils.NETWORK_2G: //  当前网络为2G移动网络
                break;
            case NetUtils.NETWORK_3G: // 当前网络为3G移动网络
                break;
            case NetUtils.NETWORK_4G: // 当前网络为4G移动网络
                break;
            case NetUtils.NETWORK_NONE: // 当前没有网络
                break;
            case -1: //当前网络错误
                break;
        }

        try {
            myApp.netWorkType = NetUtils.getNetworkState(mContext);
            if (myApp.netWorkType == NetUtils.NETWORK_WIFI) {
                dbm = getWifiRssi(myApp);
            }else if(myApp.netWorkType == NetUtils.NETWORK_2G || myApp.netWorkType == NetUtils.NETWORK_3G || myApp.netWorkType == NetUtils.NETWORK_4G ){
                dbm = getMobileDbm(myApp);
            }
            myApp.signalStrength = dbm;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取手机信号强度，需添加权限 android.permission.ACCESS_COARSE_LOCATION <br>
     * API要求不低于17 <br>
     *
     * @return 当前手机主卡信号强度,单位 dBm（-1是默认值，表示获取失败）
     */
    public int getMobileDbm(Context context)
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

    public int getWifiRssi(Context context){
        WifiManager wifi_service = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        return wifiInfo.getRssi();
    }
}