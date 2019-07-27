package com.beetech.module.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.beetech.module.application.MyApplication;
import com.beetech.module.code.request.SetTimeRequest;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.dao.ModuleBufSDDao;
import com.rscja.deviceapi.DeviceInfo;
import com.rscja.deviceapi.Module;
import com.rscja.deviceapi.exception.ConfigurationException;

public class ModuleUtils {
    private final static String TAG = ModuleUtils.class.getSimpleName();

    private Context mContext;
    private MyApplication myApp;
    private AppLogSDDao appLogSDDao;
    private ModuleBufSDDao moduleBufSDDao;

    public ModuleUtils(Context mContext){
        this.mContext = mContext;
        this.appLogSDDao = new AppLogSDDao(mContext);
        this.moduleBufSDDao = new ModuleBufSDDao(mContext);
        myApp = (MyApplication) mContext.getApplicationContext();
    }

    public Module getInstance(){
        Module module = null;
        do{
            try {
                module = Module.getInstance();
                Log.d(TAG, "module getInstance = "+module);
                myApp.module = module;
                myApp.createTime = System.currentTimeMillis();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
            if(myApp.module == null){
                Log.d(TAG, "myApp.module is null");
                SystemClock.sleep(1000);
            }
        } while(myApp.module == null);
        return module;
    }

    public boolean init() {
        boolean result = false;
        String resultMsg = "";
        try {
            Module module = getInstance();
            int deviceType = DeviceInfo.getDeviceType();
            Log.d(TAG, "deviceType = "+deviceType);
//            boolean freeResult = module.free();
//            Thread.sleep(1000);
//            Log.d(TAG, "init前free， freeResult = "+freeResult);
            result = module.init(Constant.module, Constant.baudrate); // 设备上电
            Log.d(TAG, "模块上电，module = " + Constant.module + ", baudrate=" + Constant.baudrate + ", init result="+result);

            myApp.initTime = System.currentTimeMillis();
            myApp.initResult = result;
            resultMsg = result ? "成功" : "失败";

            if(result){
                SetTimeRequest setTimeRequest = new SetTimeRequest(myApp.gwId);
                byte[] buf = setTimeRequest.getBuf();
                Log.d(TAG, Thread.currentThread().getName() + ", setTimeRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());
                boolean sendResult = module.send(buf);
                if(Constant.IS_SAVE_MODULE_LOG){
                    moduleBufSDDao.save(buf, 0, setTimeRequest.getCmd(), sendResult);
                }
                resultMsg += ", 授时" + (sendResult ? "成功" : "失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMsg = "异常："+e.getMessage();
        }

        String msgContent = "模块上电初始化 " + resultMsg;
        Log.d(TAG, msgContent);
        appLogSDDao.save(mContext.getClass().getSimpleName()+msgContent);

        Message msg = new Message();
        msg.obj = msgContent;
        handlerToast.sendMessage(msg);

        return result;
    }

    public boolean free(){
        boolean result = false;

        Module module = getInstance();
        String resultMsg = "";
        try {
            result = module.free();
            resultMsg = result ? "成功" : "失败";
            myApp.initResult = false;
        } catch (Exception e) {
            e.printStackTrace();
            resultMsg = "异常："+ e.getMessage();
        }

        String msgContent = "释放模块 " + resultMsg;
        Log.d(TAG, msgContent);
        appLogSDDao.save(mContext.getClass().getSimpleName()+msgContent);

        Message msg = new Message();
        msg.obj = msgContent;
        handlerToast.sendMessage(msg);
        return result;
    }

    private Handler handlerToast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Object toastMsg = msg.obj;
            if(toastMsg != null){
                Toast.makeText(mContext.getApplicationContext(), toastMsg.toString(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
}
