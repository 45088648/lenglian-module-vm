package com.beetech.module.dao;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.GpsDataBean;
import com.beetech.module.greendao.dao.GpsDataBeanDao;
import java.util.List;

public class GpsDataSDDao {
    private final static String TAG = GpsDataSDDao.class.getSimpleName();

    private MyApplication myApp;
    public GpsDataSDDao(Context context) {
        myApp = (MyApplication)context.getApplicationContext();
    }

    public void saveToDB(GpsDataBean gpsDataBean) {
        long startTimeInMills = System.currentTimeMillis();
        try {
            myApp.getDaoSession().getGpsDataBeanDao().insertInTx(gpsDataBean);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save异常", e);
            throw e;

        } finally {
            Log.d(TAG, "save耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void save(GpsDataBean gpsDataBean){
        long startTimeInMills = System.currentTimeMillis();
        try {
            myApp.getDaoSession().getGpsDataBeanDao().insertInTx(gpsDataBean);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save异常", e);
            throw e;

        } finally {
            Log.d(TAG, "save耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void updateSendFlag(Long _id, int sendFlag) {
        long startTimeInMills = System.currentTimeMillis();
        try {

            GpsDataBean gpsDataBean = myApp.getDaoSession().getGpsDataBeanDao().load(_id);
            if(gpsDataBean == null){
                return;
            }
            gpsDataBean.setSendFlag(sendFlag);
            myApp.getDaoSession().getGpsDataBeanDao().updateInTx(gpsDataBean);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateSendFlag", e);
            throw e;

        } finally {
            Log.d(TAG, "updateSendFlag：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public List<GpsDataBean> queryAll(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<GpsDataBean> list = null;
        try{
            list = myApp.getDaoSession().getGpsDataBeanDao().queryBuilder()
                    .orderDesc(GpsDataBeanDao.Properties._id)
                    .limit(count)
                    .offset(startPosition)
                    .list();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryAll异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryAll耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
        return list;
    }


    public List<GpsDataBean> queryForSend(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<GpsDataBean> list = null;
        try{
            list = myApp.getDaoSession().getGpsDataBeanDao().queryBuilder()
                    .where(GpsDataBeanDao.Properties.SendFlag.eq(0))
                    .limit(count)
                    .list();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryForSend异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryForSend耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }

        return list;
    }

    public GpsDataBean queryById(Long _id) {
        long startTimeInMills = System.currentTimeMillis();
        GpsDataBean gpsDataBean = null;
        try{
//            myApp.myReadDataSendEntityDaoSession.clear();
            gpsDataBean = myApp.getDaoSession().getGpsDataBeanDao().load(_id);
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryById异常", e);
            throw e;

        } finally {
            Log.d(TAG, "queryById耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }

        return gpsDataBean;
    }

    public void truncate(){
        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.getDaoSession().getGpsDataBeanDao().deleteAll();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate异常", e);
            throw e;
        } finally {
            Log.d(TAG, "truncate耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    /**
     * 删除inputTimeEndInMills前数据
     * @param dataTimeEndInMills 录入时间前
     */
    public void deleteOld(long dataTimeEndInMills) {

        long startTimeInMills = System.currentTimeMillis();
        try{
            String sql = "DELETE FROM GPS_DATA_BEAN where DATA_TIME < "+ dataTimeEndInMills;
            Log.d(TAG, "SQL = "+sql);
            myApp.getDatabase().execSQL(sql);

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "deleteOld异常", e);
            throw e;

        } finally {
            Log.d(TAG, "deleteOld耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    /**
     * 删除已发送数据
     */
    public void deleteSent() {

        long startTimeInMills = System.currentTimeMillis();
        try{
            String sql = "DELETE FROM GPS_DATA_BEAN where SEND_FLAG > 0 ";
            Log.d(TAG, "SQL = "+sql);
            myApp.getDatabase().execSQL(sql);

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "deleteSent异常", e);
            throw e;

        } finally {
            Log.d(TAG, "deleteSent耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}
