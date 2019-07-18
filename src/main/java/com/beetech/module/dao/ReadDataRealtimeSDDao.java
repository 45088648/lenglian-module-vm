package com.beetech.module.dao;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.bean.vt.NodeParamData;
import com.beetech.module.bean.vt.NodeParamResponseBean;
import com.beetech.module.bean.vt.NodeParamResponseBeanData;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.greendao.dao.ReadDataRealtimeDao;
import java.util.List;
import java.util.Date;

public class ReadDataRealtimeSDDao {

    private final static String TAG = ReadDataRealtimeSDDao.class.getSimpleName();

    private MyApplication myApp;
    public ReadDataRealtimeSDDao(Context context){
        myApp = (MyApplication)context.getApplicationContext();
    }

    public void save(ReadDataRealtime readDataRealtime){
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (readDataRealtime == null) {
                return;
            }

            myApp.daoSession.getReadDataRealtimeDao().insertInTx(readDataRealtime);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "save 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void updateToDB(ReadDataRealtime readDataRealtime) {
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (readDataRealtime == null) {
                return;
            }

            myApp.daoSession.getReadDataRealtimeDao().updateInTx(readDataRealtime);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateToDB 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "updateToDB 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public List<ReadDataRealtime> queryAll() {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataRealtime> list = null;
        try{
            list = myApp.daoSession.getReadDataRealtimeDao().queryBuilder()
                    .orderAsc(ReadDataRealtimeDao.Properties.SensorId)
                    .list();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryAll 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryAll 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }

        return list;
    }

    public long queryCount() {
        long startTimeInMills = System.currentTimeMillis();
        long count = 0;

        try{
            count = myApp.daoSession.getReadDataRealtimeDao().queryBuilder().count();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryCount 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryCount 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }

        return count;
    }

    public ReadDataRealtime queryLast(String sensorId) {
        long startTimeInMills = System.currentTimeMillis();
        ReadDataRealtime readDataRealtime = null;
        try{
            readDataRealtime = myApp.daoSession.getReadDataRealtimeDao().queryBuilder()
                    .where(ReadDataRealtimeDao.Properties.SensorId.eq(sensorId)).unique();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryLast 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryLast 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return readDataRealtime;
    }


    public ReadDataRealtime queryBySensorId(String sensorId) {
        long startTimeInMills = System.currentTimeMillis();
        ReadDataRealtime readDataRealtime = null;
        try{
            List<ReadDataRealtime> list = myApp.daoSession.getReadDataRealtimeDao().queryBuilder()
                    .where(ReadDataRealtimeDao.Properties.SensorId.eq(sensorId))
                    .list();

            if(list != null && !list.isEmpty()){
                readDataRealtime = list.get(0);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryBySensorId异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryBySensorId耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return readDataRealtime;
    }


    public void updateRealtime(NodeParamResponseBean nodeParamResponseBean){
        long startTimeInMills = System.currentTimeMillis();

        try {
            if (nodeParamResponseBean == null) {
                return;
            }
            NodeParamResponseBeanData data = nodeParamResponseBean.getData();
            if (data == null) {
                return;
            }
            List<NodeParamData> nps = data.getNps();
            if (nps == null || nps.isEmpty()) {
                return;
            }

            for (NodeParamData npd : nps) {
                String sensorId = npd.getNum();
                ReadDataRealtime readDataRealtime = myApp.daoSession.getReadDataRealtimeDao().queryBuilder()
                        .where(ReadDataRealtimeDao.Properties.SensorId.eq(sensorId)).unique();

                if(readDataRealtime == null){
                    continue;
                }
                readDataRealtime.setDevName(npd.getName());
                readDataRealtime.setDevGroupName(npd.getGn());
                readDataRealtime.setTempHight(Double.valueOf(npd.getTh()));
                readDataRealtime.setTempLower(Double.valueOf(npd.getTl()));
                readDataRealtime.setRhHight(Double.valueOf(npd.getHh()));
                readDataRealtime.setRhLower(Double.valueOf(npd.getHl()));
                readDataRealtime.setDevSendCycle(Integer.valueOf(npd.getSc()));
                readDataRealtime.setAlarmFlag(npd.getAf());
                myApp.daoSession.getReadDataRealtimeDao().updateInTx(readDataRealtime);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateRealtime 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "updateRealtime 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void updateRealtime(ReadDataResponse readDataResponse){
        long startTimeInMills = System.currentTimeMillis();

        try {
            if (readDataResponse == null) {
                return;
            }

            String sensorId = readDataResponse.getSensorId();
            ReadDataRealtime readDataRealtime  = myApp.daoSession.getReadDataRealtimeDao().queryBuilder()
                    .where(ReadDataRealtimeDao.Properties.SensorId.eq(sensorId))
                    .unique();

            if (readDataRealtime == null) {
                readDataRealtime = new ReadDataRealtime();
                readDataRealtime.update(readDataResponse);
                readDataRealtime.setInputTime(new Date());
                myApp.daoSession.getReadDataRealtimeDao().insertInTx(readDataRealtime);

            } else {
                readDataRealtime.update(readDataResponse);
                readDataRealtime.setUpdateTime(new Date());
                myApp.daoSession.getReadDataRealtimeDao().updateInTx(readDataRealtime);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateRealtime异常", e);
            throw e;

        } finally {
            Log.d(TAG, "updateRealtime耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }


    /**
     * 删除超过一段时间未有数据更新的设备
     * @param updateTimeEnd
     */
    public void deleteByUpdateTime(Date updateTimeEnd) {
        long startTimeInMills = System.currentTimeMillis();
        try{
            List<ReadDataRealtime> list = myApp.daoSession.getReadDataRealtimeDao().queryBuilder()
                    .where(ReadDataRealtimeDao.Properties.UpdateTime.lt(updateTimeEnd))
                    .list();
            if(list == null || list.isEmpty()){
                return;
            }
            myApp.daoSession.getReadDataRealtimeDao().deleteInTx(list);
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "deleteByUpdateTime异常", e);
            throw e;

        } finally {
            Log.d(TAG, "deleteByUpdateTime耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }


    public void truncate(){
        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.daoSession.getReadDataRealtimeDao().deleteAll();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate异常", e);
            throw e;

        } finally {
            Log.d(TAG, "truncate耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}
