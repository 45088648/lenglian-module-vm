package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;

import com.beetech.module.application.MyApplication;

public class ModuleFreeUtils {
    private final static String TAG = ModuleFreeUtils.class.getSimpleName();

    public static void moduleFree(Context context){
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication) context.getApplicationContext();
            ModuleUtils moduleUtils = new ModuleUtils(myApp);
            Log.d(TAG, "========== myApp.manualStopModuleFlag = "+myApp.manualStopModuleFlag);
            moduleUtils.free();
            myApp.manualStopModuleFlag = 1;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "moduleFree 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "moduleFree 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }

}
