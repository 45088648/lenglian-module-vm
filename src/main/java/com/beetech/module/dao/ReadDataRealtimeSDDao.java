package com.beetech.module.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.bean.vt.NodeParamData;
import com.beetech.module.bean.vt.NodeParamResponseBean;
import com.beetech.module.bean.vt.NodeParamResponseBeanData;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.greendao.dao.ReadDataRealtimeDao;

import java.util.Date;
import java.util.List;

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
                String th = npd.getTh();
                String tl = npd.getTl();
                String hh = npd.getHh();
                String hl = npd.getHl();
                String sc = npd.getSc();
                ReadDataRealtime readDataRealtime = myApp.daoSession.getReadDataRealtimeDao().queryBuilder()
                        .where(ReadDataRealtimeDao.Properties.SensorId.eq(sensorId)).unique();

                if(readDataRealtime == null){
                    continue;
                }
                readDataRealtime.setDevName(npd.getName());
                readDataRealtime.setDevGroupName(npd.getGn());
                if(!TextUtils.isEmpty(th)) {
                    readDataRealtime.setTempHight(Double.valueOf(th));
                }
                if(!TextUtils.isEmpty(tl)) {
                    readDataRealtime.setTempLower(Double.valueOf(npd.getTl()));
                }
                if(!TextUtils.isEmpty(hh)){
                    readDataRealtime.setRhHight(Double.valueOf(hh));
                }
                if(!TextUtils.isEmpty(hl)) {
                    readDataRealtime.setRhLower(Double.valueOf(hl));
                }
                if(!TextUtils.isEmpty(sc)) {
                    readDataRealtime.setDevSendCycle(Integer.valueOf(sc));
                }
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
            myApp.database.execSQL("update sqlite_sequence set seq=0 where name='READ_DATA_REALTIME'");

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate异常", e);
            throw e;

        } finally {
            Log.d(TAG, "truncate耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}
