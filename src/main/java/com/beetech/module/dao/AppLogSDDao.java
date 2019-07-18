package com.beetech.module.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.AppLog;
import com.beetech.module.greendao.dao.AppLogDao;
import java.util.List;

public class AppLogSDDao {
    private final static String TAG = AppLogSDDao.class.getSimpleName();

    private MyApplication myApp;
    public AppLogSDDao(Context context) {
        myApp = (MyApplication)context.getApplicationContext();
    }

    public void save(String content){
        long startTimeInMills = System.currentTimeMillis();
        try {
            if (TextUtils.isEmpty(content)) {
                return;
            }
            AppLog appLog = new AppLog(content);
            myApp.getDaoSession().getAppLogDao().insertInTx(appLog);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save异常", e);
            throw e;

        } finally {
            Log.d(TAG, "save耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public List<AppLog> queryAll(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<AppLog> list = null;
        try{
            list = myApp.getDaoSession().getAppLogDao().queryBuilder()
                    .orderDesc(AppLogDao.Properties._id)
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

    public void truncate(){
        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.getDaoSession().getAppLogDao().deleteAll();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate异常", e);
            throw e;
        } finally {
            Log.d(TAG, "truncate耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}