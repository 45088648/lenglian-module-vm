package com.beetech.module.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.vt.SysResponseBean;
import com.beetech.module.bean.vt.SysResponseBody;
import com.beetech.module.client.ConnectUtils;
import com.beetech.module.code.response.QueryConfigResponse;
import com.beetech.module.constant.Constant;
import java.util.Date;
import java.util.List;

public class QueryConfigRealtimeSDDao {
    private final static String TAG = QueryConfigRealtimeSDDao.class.getSimpleName();

    private MyApplication myApp;
    public QueryConfigRealtimeSDDao(Context context){
        myApp = (MyApplication)context.getApplicationContext();
    }

    public void save(QueryConfigRealtime queryConfigRealtime){
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (queryConfigRealtime == null) {
                return;
            }
            myApp.daoSession.getQueryConfigRealtimeDao().insertInTx(queryConfigRealtime);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save异常", e);
            throw e;

        } finally {
            Log.d(TAG, "save耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void update(QueryConfigRealtime queryConfigRealtime) {
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (queryConfigRealtime == null) {
                return;
            }
            myApp.daoSession.getQueryConfigRealtimeDao().updateInTx(queryConfigRealtime);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "update 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void update(QueryConfigResponse queryConfigResponse) {
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (queryConfigResponse == null) {
                return;
            }

            QueryConfigRealtime queryConfigRealtime = myApp.daoSession.getQueryConfigRealtimeDao().queryBuilder().unique();
            if(queryConfigRealtime == null){
                queryConfigRealtime = new QueryConfigRealtime();
                queryConfigRealtime.update(queryConfigResponse);
                queryConfigRealtime.setImei(Constant.imei);
                queryConfigRealtime.setDevServerIp(ConnectUtils.HOST);
                queryConfigRealtime.setDevServerPort(ConnectUtils.PORT);
                myApp.daoSession.getQueryConfigRealtimeDao().save(queryConfigRealtime);
                return;
            }

            queryConfigRealtime.update(queryConfigResponse);

            myApp.daoSession.getQueryConfigRealtimeDao().updateInTx(queryConfigRealtime);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "update 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void update(SysResponseBean sysResponseBean) {
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (sysResponseBean == null) {
                return;
            }

            QueryConfigRealtime queryConfigRealtime = myApp.daoSession.getQueryConfigRealtimeDao().queryBuilder().unique();
            if(queryConfigRealtime == null){
                return;
            }
            SysResponseBody sysResponseBody = sysResponseBean.getData();
            Constant.devNum = sysResponseBody.getNum();
            ConnectUtils.HOST = sysResponseBody.getIp1();
            ConnectUtils.PORT = sysResponseBody.getPort1();
            Constant.devName = sysResponseBody.getDn();
            queryConfigRealtime.setDevNum(Constant.devNum);
            queryConfigRealtime.setDevName(Constant.devName);
            queryConfigRealtime.setDevServerIp(ConnectUtils.HOST);
            queryConfigRealtime.setDevServerPort(ConnectUtils.PORT);
            queryConfigRealtime.setUpdateTime(new Date());
            myApp.daoSession.getQueryConfigRealtimeDao().updateInTx(queryConfigRealtime);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "update 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void updateByIsSetDataBeginTimeByBoot(boolean isSetDataBeginTimeByBoot) throws Exception{
        long startTimeInMills = System.currentTimeMillis();
        try{


            List<QueryConfigRealtime> list = myApp.daoSession.getQueryConfigRealtimeDao().queryBuilder().limit(1).list();
            if(list == null || list.isEmpty()){
                return;
            }

            QueryConfigRealtime queryConfigRealtime = list.get(0);
            if(queryConfigRealtime != null){
                queryConfigRealtime.setIsSetDataBeginTimeByBoot(isSetDataBeginTimeByBoot);
                queryConfigRealtime.setUpdateTime(new Date());
                myApp.daoSession.getQueryConfigRealtimeDao().updateInTx(queryConfigRealtime);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateByIsSetDataBeginTimeByBoot异常", e);
            throw e;
        } finally {
            Log.d(TAG, "updateByIsSetDataBeginTimeByBoot耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void updateByAlarmFlag(boolean alarmFlag) throws Exception{
        long startTimeInMills = System.currentTimeMillis();
        try{
            Log.d(TAG, "alarmFlag="+alarmFlag);

            List<QueryConfigRealtime> list = myApp.daoSession.getQueryConfigRealtimeDao().queryBuilder().limit(1).list();
            if(list == null || list.isEmpty()){
                return;
            }

            QueryConfigRealtime queryConfigRealtime = list.get(0);
            if(queryConfigRealtime != null){
                queryConfigRealtime.setAlarmFlag(alarmFlag);
                queryConfigRealtime.setUpdateTime(new Date());
                myApp.daoSession.getQueryConfigRealtimeDao().updateInTx(queryConfigRealtime);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateByAlarmFlag异常", e);
            throw e;
        } finally {
            Log.d(TAG, "updateByAlarmFlag耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void updateBySmsSt(String smsContent) throws Exception{
        long startTimeInMills = System.currentTimeMillis();
        try{
            if (TextUtils.isEmpty(smsContent)) {
                return;
            }
            String[] stParamStrArr = smsContent.substring(3).split("\\|");
            Constant.devNum = stParamStrArr[0];
            Constant.devEncryption = stParamStrArr[2];
            ConnectUtils.HOST = stParamStrArr[8];

            if(TextUtils.isDigitsOnly(stParamStrArr[9])){
                ConnectUtils.PORT = Integer.valueOf(stParamStrArr[9]);
            }

            List<QueryConfigRealtime> list = myApp.daoSession.getQueryConfigRealtimeDao().queryBuilder().limit(1).list();
            if(list == null || list.isEmpty()){
                return;
            }
            QueryConfigRealtime queryConfigRealtime = list.get(0);
            if(queryConfigRealtime != null){
                queryConfigRealtime.setDevNum(Constant.devNum);
                queryConfigRealtime.setDevEncryption(Constant.devEncryption);
                queryConfigRealtime.setDevServerIp(ConnectUtils.HOST);
                queryConfigRealtime.setDevServerPort(ConnectUtils.PORT);
                queryConfigRealtime.setUpdateTime(new Date());
                myApp.daoSession.getQueryConfigRealtimeDao().updateInTx(queryConfigRealtime);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateBySmsSt异常", e);
            throw e;
        } finally {
            Log.d(TAG, "updateBySmsSt耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public List<QueryConfigRealtime> query(String gwId) {
        long startTimeInMills = System.currentTimeMillis();
        QueryConfigRealtime queryConfigRealtime = null;
        List<QueryConfigRealtime> list = null;
        try{
            list = myApp.daoSession.getQueryConfigRealtimeDao().queryBuilder().limit(1).list();
            if(list != null && !list.isEmpty()){
                queryConfigRealtime = list.get(0);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryOne异常", e);
            throw e;
        } finally {
            Log.d(TAG, "queryOne耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }

    public QueryConfigRealtime queryLast() {
        long startTimeInMills = System.currentTimeMillis();
        QueryConfigRealtime queryConfigRealtime = null;
        try{
            queryConfigRealtime = myApp.daoSession.getQueryConfigRealtimeDao().queryBuilder().unique();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryLast 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryLast 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return queryConfigRealtime;
    }

    public void truncate(){
        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.daoSession.getQueryConfigRealtimeDao().deleteAll();
            myApp.database.execSQL("update sqlite_sequence set seq=0 where name='QUERY_CONFIG_REALTIME'");
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate异常", e);
            throw e;

        } finally {
            Log.d(TAG, "truncate耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}
