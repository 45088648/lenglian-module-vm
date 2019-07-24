package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.GpsDataBean;
import com.beetech.module.bean.vt.GpsDataRequestBean;
import com.beetech.module.constant.Constant;
import org.apache.mina.core.session.IoSession;
import java.util.List;

public class SendGpsDataUtils {
    private final static String TAG = ModuleInitUtils.class.getSimpleName();
    private final static int queryCount = 30;

    public static void sendGpsData(Context context){
        long beginTimeInMills = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            IoSession mSession = myApp.session;
            if(mSession == null || !mSession.isConnected()){
                return;
            }
            List<GpsDataBean> dataList = myApp.gpsDataSDDao.queryForSend(queryCount, 0);
            if(dataList == null || dataList.isEmpty()){
                return;
            }
            for (GpsDataBean gpsDataBean : dataList){
                final Long id = gpsDataBean.get_id();
                GpsDataRequestBean gpsDataRequestBean = new GpsDataRequestBean(gpsDataBean);
                String inText = JSON.toJSONString(gpsDataRequestBean);
                Log.d(TAG, "gpsData.inText = "+inText);
                //保存日志
                try {
                    if(Constant.IS_SAVE_SOCKET_LOG){
                        myApp.vtSocketLogSDDao.save(inText, 0, id, Thread.currentThread().getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mSession.write(inText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "sendGpsData 异常 ", e);

        } finally {
            Log.d(TAG, "sendGpsData 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }

    }
}
