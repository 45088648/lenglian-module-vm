package com.beetech.module.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.beetech.module.service.LocationService;
import com.baidu.location.BDLocation;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.client.ConnectUtils;
import com.beetech.module.constant.Constant;
import org.apache.mina.core.session.IoSession;
import java.util.Date;

public class AppStateUtils {
    private final static String TAG = CheckSessionUtils.class.getSimpleName();


    public static StringBuffer getState(Context context){
        long beginTimeInMills = System.currentTimeMillis();
        StringBuffer stateSb = new StringBuffer(ConnectUtils.stringNowTime()).append(": ");

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            stateSb.append("APP版本:").append(Constant.verName).append(" ");
            stateSb.append("ModuleService: ").append(ServiceAliveUtils.isServiceRunning(context,Constant.className_moduleService) ? "运行" : "未运行") .append(" ");

            stateSb.append("网络：").append(NetUtils.network_type_name.get(myApp.netWorkType)).append(" ");
            stateSb.append("信号：").append(myApp.signalStrength).append(" dbm").append(" ");
            stateSb.append("USB:").append(myApp.usbConnected ? "接通":"断开").append(" ");
            stateSb.append("充电:").append(ChargingUtils.isCharging(myApp) ? "是":"否").append(" ");
            stateSb.append("\n");

            stateSb.append("IMEI：").append(Constant.imei).append(" ");
            stateSb.append("ICCID：").append(Constant.iccid).append(" ");
            stateSb.append(" ").append(Constant.IS_SAVE_MODULE_LOG);
            stateSb.append(" ").append(Constant.IS_SAVE_SOCKET_LOG).append(" ");
            stateSb.append("isDebuggable：").append(isDebuggable(context)).append(" ");
            stateSb.append(" ").append(Constant.readDataResponseTimeOut).append(" ");
            stateSb.append("\n");

            String moduleName = myApp.module+"";
            moduleName = moduleName.substring(moduleName.lastIndexOf(".")+1);
            stateSb.append("模块：").append(moduleName.equals("")? "空" : moduleName).append(" ");
            stateSb.append(myApp.initResult ? "上电" : "释放").append(" ").append(Constant.sdf.format(new Date(myApp.initTime))).append(" ");
            stateSb.append("读：").append(Constant.sdf.format(new Date(myApp.lastReadTime))).append(" ");
            stateSb.append("写：").append(Constant.sdf.format(new Date(myApp.lastWriteTime))).append(" ");
            stateSb.append("\n");
            stateSb.append("数据标志：").append(myApp.readDataResponseError).append(" ");
            stateSb.append("时间：").append(Constant.sdf.format(new Date(myApp.readDataResponseTime))).append(" ");
            stateSb.append("RAM待发：").append(myApp.readDataResponseWaitSentSize1).append(" ");
            stateSb.append("Flash待发：").append(myApp.readDataResponseWaitSentSize2).append(" ");
            stateSb.append("Flash错误：").append(myApp.readDataResponseErrorcode).append(" ");
            stateSb.append("\n");

            QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime != null){
                stateSb.append("GWID：").append(queryConfigRealtime.getGwId()).append(" ");
                stateSb.append("客户码：").append(myApp.customer).append(" ");
                stateSb.append("工作模式：").append(myApp.pattern).append(" ");
                stateSb.append("传输速率：").append(myApp.bps).append(" ");
                stateSb.append("频段：").append(myApp.channel).append(" ");
            }
            if(myApp.setDataBeginTime != null){
                stateSb.append("数据开始时间：").append(Constant.sdf.format(myApp.setDataBeginTime)).append(" ");
            }
            stateSb.append("\n");
            if(myApp.frontDeleteResponse != null){
                stateSb.append("DeleteResponse(").append(" ");
                stateSb.append("front：").append(myApp.frontDeleteResponse).append(" ");
                stateSb.append("rear：").append(myApp.rearDeleteResponse).append(" ");
                stateSb.append("pflashLength：").append(myApp.pflashLengthDeleteResponse).append(" ");
                stateSb.append(")\n");
            }

            LocationService locationService = myApp.locationService;
            String locationServiceName = locationService+"";

            locationServiceName = locationServiceName.substring(locationServiceName.lastIndexOf(".")+1);

            stateSb.append("定位：").append(locationServiceName).append(" ");
            if(locationService != null){
                stateSb.append(locationService.isStart() ? "启动" : "关闭").append(" ");
            }
            BDLocation location = myApp.location;
            if(location != null){
                stateSb.append(location.getLongitude()).append(",").append(location.getLatitude()).append(" ").append(location.getAddrStr());
            }
            stateSb.append("\n");

            IoSession mSession = myApp.session;
            stateSb.append("会话：").append(mSession).append("\n");
            if(mSession != null){
                stateSb.append("连接：").append(mSession.isConnected() ? "连通" : "断开").append(" ");
                stateSb.append("创建：").append(Constant.sdf.format(new Date(mSession.getCreationTime()))).append(" ");
                stateSb.append("IO：").append(Constant.sdf.format(new Date(mSession.getLastIoTime()))).append(" ");
                stateSb.append("Idle：").append(Constant.sdf.format(new Date(mSession.getLastBothIdleTime()))).append(" ");
            }
            stateSb.append("\n");

            stateSb.append("模块操作：").append(myApp.moduleHandlerThread);
            if(myApp.moduleHandlerThread != null){
                stateSb.append(" ").append(myApp.moduleHandlerThread.isAlive() ? "alive" : "");
            }
            stateSb.append("\n");

            stateSb.append("定时任务：").append(myApp.threadTimeTask);
            if(myApp.threadTimeTask != null){
                stateSb.append(" ").append(myApp.threadTimeTask.isAlive() ? "alive" : "");
                stateSb.append(" ").append(Constant.sdf.format(new Date(myApp.threadTimeTask.runTime)));
                stateSb.append(" ").append(Constant.NUM.longValue());
            }
            stateSb.append("\n");

            stateSb.append("读串口：").append(myApp.threadModuleReceive);
            if(myApp.threadModuleReceive != null){
                stateSb.append(" ").append(myApp.threadModuleReceive.isAlive() ? "alive" : "");
                stateSb.append(" ").append(Constant.sdf.format(new Date(myApp.threadModuleReceive.runTime)));
                stateSb.append(" ").append(Constant.NUM_RECEIVE.longValue());
            }
            stateSb.append("\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stateSb;
    }

    public static boolean isDebuggable(Context context) {
        boolean debuggable = false;
        MyApplication myApp = (MyApplication)context.getApplicationContext();
        PackageManager pm = myApp.getPackageManager();
        try{
            ApplicationInfo appinfo = pm.getApplicationInfo(myApp.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        }catch(PackageManager.NameNotFoundException e){
            /*debuggable variable will remain false*/
        }
        return debuggable;
    }
}
