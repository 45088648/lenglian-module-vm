package com.beetech.module.dao;

import android.content.Context;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ModuleBuf;
import com.beetech.module.greendao.dao.ModuleBufDao;

import java.util.List;

public class ModuleBufSDDao {
    private final static String TAG = ModuleBufSDDao.class.getSimpleName();

    private MyApplication myApp;
    public ModuleBufSDDao(Context context) {
        myApp = (MyApplication)context.getApplicationContext();
    }

    public void save(byte[] buf, int type, int cmd, boolean result){
        long startTimeInMills = System.currentTimeMillis();
        try{
            ModuleBuf moduleBuf = new ModuleBuf(buf, type, cmd, result);
            myApp.getDaoSession().getModuleBufDao().insertInTx(moduleBuf);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save异常", e);
            throw e;

        } finally {
            Log.d(TAG, "save耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }

    }

    public List<ModuleBuf> queryAll(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ModuleBuf> list = null;
        try{
            list = myApp.getDaoSession().getModuleBufDao().queryBuilder()
                    .orderDesc(ModuleBufDao.Properties._id)
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


    public List<ModuleBuf> queryForSend(int count, int startPosition) {
        long startTimeInMills = System.currentTimeMillis();
        List<ModuleBuf> list = null;
        try{
            list = myApp.getDaoSession().getModuleBufDao().queryBuilder()
                    .where(ModuleBufDao.Properties.Result.eq(false))
                    .limit(count)
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

    public void updateSendFlag(Long _id, boolean result) {
        long startTimeInMills = System.currentTimeMillis();
        try {

            ModuleBuf moduleBuf = myApp.getDaoSession().getModuleBufDao().load(_id);
            if(moduleBuf == null){
                return;
            }
            moduleBuf.setResult(result);
            myApp.getDaoSession().getModuleBufDao().updateInTx(moduleBuf);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateSendFlag", e);
            throw e;

        } finally {
            Log.d(TAG, "updateSendFlag：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }

    public void truncate(){
        long startTimeInMills = System.currentTimeMillis();
        try{
            myApp.getDaoSession().getModuleBufDao().deleteAll();
            myApp.database.execSQL("update sqlite_sequence set seq=0 where name='MODULE_BUF'");

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "truncate 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "truncate 耗时：" + (System.currentTimeMillis() - startTimeInMills));
        }
    }
}
