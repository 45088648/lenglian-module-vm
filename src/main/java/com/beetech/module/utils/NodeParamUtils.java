package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.bean.vt.NodeParamRequestBean;
import com.beetech.module.constant.Constant;
import org.apache.mina.core.session.IoSession;
import java.util.List;

public class NodeParamUtils {
    private final static String TAG = NodeParamUtils.class.getSimpleName();

    public static int requestNodeParam(Context context){
        long runTime = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            List<ReadDataRealtime> readDataRealtimeList = myApp.readDataRealtimeSDDao.queryAll();
            if(readDataRealtimeList == null || readDataRealtimeList.isEmpty()){
                return -1;
            }

            NodeParamRequestBean nodeParamRequestBean = new NodeParamRequestBean(readDataRealtimeList);
            IoSession mSession = myApp.session;
            if(mSession == null || !mSession.isConnected()){
                return -2;
            }
            String inText = JSON.toJSONString(nodeParamRequestBean);
            mSession.write(inText);
            if(Constant.IS_SAVE_SOCKET_LOG){
                try{
                    myApp.vtSocketLogSDDao.save(inText, 0, 0L, Thread.currentThread().getName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "requestNodeParam 异常", e);
        } finally {
            Log.d(TAG, "requestNodeParam 耗时：" + (System.currentTimeMillis()-runTime));
        }
        return 0;
    }
}
