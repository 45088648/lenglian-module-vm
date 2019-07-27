package com.beetech.module.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.greendao.dao.ReadDataResponseDao;

import java.util.Date;
import java.util.List;

public class ReadDataSDDao {
    private final static String TAG = ReadDataSDDao.class.getSimpleName();

    private MyApplication myApp;
    public ReadDataSDDao(Context context){
        myApp = (MyApplication)context.getApplicationContext();
    }

    public void save(ReadDataResponse readDataResponse){
        long startTimeInMills = System.currentTimeMillis();
        try {
            if(readDataResponse == null) {
                return;
            }
            myApp.daoSession.getReadDataResponseDao().insertInTx(readDataResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "保存save异常", e);
            throw e;

        } finally {
            Log.d(TAG, "保存save耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public List<ReadDataResponse> queryAll(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .orderDesc(ReadDataResponseDao.Properties._id)
                    .limit(count)
                    .offset(startPosition)
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

    public List<ReadDataResponse> query(String sensorId) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties.SensorId.eq(sensorId))
                    .orderDesc(ReadDataResponseDao.Properties._id)
                    .list();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "query 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "query 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }

    public List<ReadDataResponse> query(String sensorId, int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties.SensorId.eq(sensorId))
                    .orderDesc(ReadDataResponseDao.Properties._id)
                    .limit(count)
                    .offset(startPosition)
                    .list();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "query 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "query 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }

    public List<ReadDataResponse> query(String sensorId, Date sensorDataTimeEnd, int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties.SensorId.eq(sensorId))
                    .where(ReadDataResponseDao.Properties.SensorDataTime.lt(sensorDataTimeEnd))
                    .orderDesc(ReadDataResponseDao.Properties.SensorDataTime)
                    .limit(count)
                    .offset(startPosition)
                    .list();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "query 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "query 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }

    public List<ReadDataResponse> queryBySensorId(String sensorId, Date sensorDataTimeBegin, Date sensorDataTimeEnd, int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            if(!TextUtils.isEmpty(sensorId)){
                list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                        .where(ReadDataResponseDao.Properties.SensorId.eq(sensorId))
                        .where(ReadDataResponseDao.Properties.SensorDataTime.ge(sensorDataTimeBegin))
                        .where(ReadDataResponseDao.Properties.SensorDataTime.le(sensorDataTimeEnd))
                        .orderAsc(ReadDataResponseDao.Properties.SensorDataTime)
                        .limit(count)
                        .offset(startPosition)
                        .list();
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "查询queryBySensorId异常", e);
            throw e;

        } finally {
            Log.d(TAG, "查询queryBySensorId耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }

    public List<ReadDataResponse> queryAll(Date sensorDataTimeBegin, Date sensorDataTimeEnd, int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties.SensorDataTime.gt(sensorDataTimeBegin))
                    .where(ReadDataResponseDao.Properties.SensorDataTime.lt(sensorDataTimeEnd))
                    .orderAsc(ReadDataResponseDao.Properties.SensorDataTime)
                    .limit(count)
                    .offset(startPosition)
                    .list();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "查询queryBySensorId异常", e);
            throw e;

        } finally {
            Log.d(TAG, "查询queryBySensorId耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }

    public List<ReadDataResponse> queryForSend(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties.SendFlag.eq(0))
                    .orderAsc(ReadDataResponseDao.Properties._id)
                    .limit(count)
                    .offset(startPosition)
                    .list();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryForSend 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryForSend 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }

    public List<ReadDataResponse> queryForSendNoResponse(int count, Date writeTimeEnd, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ReadDataResponse> list = null;
        try{
            list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties.SendFlag.gt(0))
                    .where(ReadDataResponseDao.Properties.ResponseFlag.eq(0))
                    .where(ReadDataResponseDao.Properties.WriteTime.lt(writeTimeEnd))
                    .orderAsc(ReadDataResponseDao.Properties._id)
                    .limit(count)
                    .offset(startPosition)
                    .list();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryForSendNoResponse 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryForSendNoResponse 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;

    }

    public long queryCount() {
        long count = 0;
        long startTimeInMills = System.currentTimeMillis();

        try{
            count = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .count();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryCount 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryCount 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }

        return count;
    }

    public long queryCountNotSend() {
        long count = 0;
        long startTimeInMills = System.currentTimeMillis();

        try{
            count = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties.SendFlag.eq(0))
                    .count();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryCount 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryCount 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }

        return count;
    }

    public void updateSendFlagList(List<Long> ids) throws Exception{
        long startTimeInMills = System.currentTimeMillis();
        try{
            if (ids == null || ids.isEmpty()) {
                return;
            }
            List<ReadDataResponse> list = myApp.daoSession.getReadDataResponseDao().queryBuilder()
                    .where(ReadDataResponseDao.Properties._id.in(ids))
                    .list();
            if(list == null || list.isEmpty()){
                return;
            }
            for (ReadDataResponse readDataResponse:list) {
                int sendFlag = readDataResponse.getSendFlag();
                readDataResponse.setSendFlag(sendFlag+1);
                readDataResponse.setWriteTime(new Date());
            }
            myApp.daoSession.getReadDataResponseDao().updateInTx(list);

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateSendFlagList 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "updateSendFlagList 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void updateResponseFlag(Long _id, long timeInMills){
        long startTimeInMills = System.currentTimeMillis();
        try{
            ReadDataResponse readDataResponse = myApp.daoSession.getReadDataResponseDao().load(_id);

            if(readDataResponse == null){
                return;
            }
            readDataResponse.setResponseFlag(readDataResponse.getResponseFlag()+1);
            readDataResponse.setWriteTime(new Date());
            myApp.daoSession.getReadDataResponseDao().updateInTx(readDataResponse);
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateResponseFlag 异常", e);
            throw e;
        } finally {
            Log.d(TAG, "updateResponseFlag 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    /**
     * 删除inputTimeEndInMills前数据
     * @param sensorDataTimeEndInMills 录入时间前
     */
    public void deleteOld(long sensorDataTimeEndInMills) {

        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.database.execSQL("DELETE FROM READ_DATA_RESPONSE where SENSOR_DATA_TIME < "+ sensorDataTimeEndInMills);

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "deleteReadDataOld 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "deleteReadDataOld 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    /**
     * 删除已发送数据
     */
    public void deleteSent() {

        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.database.execSQL("DELETE FROM READ_DATA_RESPONSE where RESPONSE_FLAG > 0 ");

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "deleteSent 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "deleteSent 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void truncate(){
        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.daoSession.getReadDataResponseDao().deleteAll();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "truncate 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}
