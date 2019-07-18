package com.beetech.module.bean.vt;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Formatter;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.NetUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VtStateRequestBeanUtils {
    private final static String TAG = VtStateRequestBeanUtils.class.getSimpleName();

    private Context mContext;
    private MyApplication myApp;

    public VtStateRequestBeanUtils(Context context){
        this.mContext = context;
        myApp = (MyApplication) mContext.getApplicationContext();
    }

    public VtStateRequestBean getMessage(){
        VtStateRequestBean vtStateRequestBean = null;
        try {
            QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime != null){
                vtStateRequestBean = new VtStateRequestBean(queryConfigRealtime);
                StateRequestBean body = vtStateRequestBean.getBody();
                body.setBt(myApp.batteryPercent);
                body.setPower(myApp.power);
                try {
                    int dbm = 0;
                    myApp.netWorkType = NetUtils.getNetworkState(mContext);
                    if (myApp.netWorkType == NetUtils.NETWORK_WIFI) {
                        dbm = getWifiRssi(myApp);
                    }else if(myApp.netWorkType == NetUtils.NETWORK_2G || myApp.netWorkType == NetUtils.NETWORK_3G || myApp.netWorkType == NetUtils.NETWORK_4G ){
                        dbm = getMobileDbm(myApp);
                    }
                    myApp.signalStrength = dbm;
                    body.setCsq(dbm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                body.setVar(Constant.verName);
                body.setGwstate(myApp.initResult ? 1 : 0);

                List<ReadDataRealtime> readDataRealtimeList = myApp.readDataRealtimeSDDao.queryAll();
                if(readDataRealtimeList != null && !readDataRealtimeList.isEmpty()){
                    List<StateData> data = new ArrayList<>();
                    for (ReadDataRealtime readDataRealtime: readDataRealtimeList) {
                        StateData stateData = new StateData(readDataRealtime);
                        data.add(stateData);
                    }
                    body.setLjs(readDataRealtimeList.size());
                    body.setData(data);
                }

                //获取手机基站信息
                try {
                    int mnc = 0;
                    int cid = 0;
                    int lac = 0;

                    if(hasSimCard(mContext)){
                        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                        String operator = manager.getNetworkOperator();
                        int mcc = 460;

                        if(operator != null && !"".equals(operator)){
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

                            // 获取邻区基站信息
                            List<NeighboringCellInfo> infos = manager.getNeighboringCellInfo();
                            int index = 0;
                            for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环
                                lac = info1.getLac(); // 取出当前邻区的LAC
                                cid = info1.getCid(); // 取出当前邻区的CID
                                int rssi = (-113 + 2 * info1.getRssi()); // 获取邻区基站信号强度

                                if(index == 0){
                                    body.setMnc1(mnc);
                                    body.setCi1(cid);
                                    body.setLac1(lac);

                                } else if (index == 1){
                                    body.setMnc2(mnc);
                                    body.setCi2(cid);
                                    body.setLac2(lac);
                                }
                                index++;
                            }
                        }


                        String iccid = manager.getSimSerialNumber();
                        body.setIccid(iccid);
                    }

                    body.setMnc(mnc);
                    body.setCi(cid);
                    body.setLac(lac);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    //获取SD卡可用剩余空间
                    String state = Environment.getExternalStorageState();
                    if(Environment.MEDIA_MOUNTED.equals(state)) {
                        File sdcardDir = Environment.getExternalStorageDirectory();
                        StatFs sf = new StatFs(sdcardDir.getPath());
                        long blockSize = sf.getBlockSize();
                        long blockCount = sf.getBlockCount();
                        long availCount = sf.getAvailableBlocks();
//                        Log.d(TAG, "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
//                        Log.d(TAG, "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
                        long sDFreeSpace = sdcardDir.getFreeSpace();
                        //格式化大小
                        String sDFreeSpaceFormat = Formatter.formatFileSize(mContext, sDFreeSpace);
//                        Log.d(TAG, "剩余空间格式化大小:"+ sDFreeSpaceFormat);

                        body.setUf(Long.valueOf(availCount*blockSize/1024/1024).intValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    long readDataCountNotSend = myApp.readDataSDDao.queryCountNotSend();
                    long readDataCount = myApp.readDataSDDao.queryCount();

                    body.setDatafile(readDataCount);
                    body.setUnup(readDataCountNotSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vtStateRequestBean;
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
