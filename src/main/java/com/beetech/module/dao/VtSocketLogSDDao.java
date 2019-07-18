package com.beetech.module.dao;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.vt.VtSocketLog;
import com.beetech.module.greendao.dao.VtSocketLogDao;

import java.util.List;

public class VtSocketLogSDDao {
    private final static String TAG = VtSocketLogSDDao.class.getSimpleName();

    private MyApplication myApp;
    public VtSocketLogSDDao(Context context) {
        myApp = (MyApplication)context.getApplicationContext();
    }

    public void save(VtSocketLog vtSocketLog) {
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (vtSocketLog == null) {
                return;
            }
            myApp.daoSession.getVtSocketLogDao().insertInTx(vtSocketLog);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "save 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void save(String text, int type, Long dataId, String threadName){
        long startTimeInMills = System.currentTimeMillis();
        try {
            VtSocketLog vtSocketLog = new VtSocketLog(text, type, dataId, threadName);
            myApp.daoSession.getVtSocketLogDao().insertInTx(vtSocketLog);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " save 异常", e);
            throw e;

        } finally {
            Log.d(TAG, " save 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public List<VtSocketLog> queryAll(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<VtSocketLog> list = null;
        try{
            list = myApp.daoSession.getVtSocketLogDao().queryBuilder()
                    .orderDesc(VtSocketLogDao.Properties._id)
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

    public void truncate(){
        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.daoSession.getVtSocketLogDao().deleteAll();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate 异常", e);
            throw e;
        } finally {
            Log.d(TAG, "truncate 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}
