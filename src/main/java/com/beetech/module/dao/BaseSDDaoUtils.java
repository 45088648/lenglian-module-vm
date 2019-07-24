package com.beetech.module.dao;

import android.content.Context;
import com.beetech.module.application.MyApplication;

public class BaseSDDaoUtils {
    private Context mContext;
    private MyApplication myApp ;

    public BaseSDDaoUtils(Context mContext){
        this.mContext = mContext;
        myApp = (MyApplication)mContext.getApplicationContext();
    }

    public void trancateAll(){
        myApp.readDataSDDao.truncate();
        myApp.gpsDataSDDao.truncate();
        myApp.moduleBufSDDao.truncate();
        myApp.appLogSDDao.truncate();
        myApp.readDataRealtimeSDDao.truncate();
        myApp.queryConfigRealtimeSDDao.truncate();
        myApp.vtSocketLogSDDao.truncate();
    }

    public void trancateLog(){
        myApp.readDataSDDao.truncate();
        myApp.gpsDataSDDao.truncate();
        myApp.moduleBufSDDao.truncate();
        myApp.appLogSDDao.truncate();
        myApp.vtSocketLogSDDao.truncate();
    }

    public void deleteLog(){
        myApp.readDataSDDao.deleteSent();
        myApp.gpsDataSDDao.deleteSent();
        myApp.moduleBufSDDao.truncate();
        myApp.vtSocketLogSDDao.truncate();
    }
}
