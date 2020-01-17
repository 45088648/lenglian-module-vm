package com.beetech.module.client;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.vt.NodeParamResponseBean;
import com.beetech.module.bean.vt.SysResponseBean;
import com.beetech.module.bean.vt.VtResponseBean;
import com.beetech.module.constant.Constant;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class MyHandler extends IoHandlerAdapter {

    private final String TAG = MyHandler.class.getSimpleName();

    private Context mContext;
    private MyApplication myApp;

    public MyHandler(Context context) {
        this.mContext = context;
        myApp = (MyApplication) context.getApplicationContext();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        Log.d(TAG, "exceptionCaught："+cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        Log.d(TAG,  "messageReceived：" + message.toString());

        String msg = message.toString();
        if(msg != null && !msg.isEmpty()){
            //更新传感器数据
            try {
                VtResponseBean vtResponseBean = JSON.parseObject(msg, VtResponseBean.class);
                if(vtResponseBean != null){

                    //保存日志
                    if(Constant.IS_SAVE_SOCKET_LOG) {
                        try {
                            myApp.vtSocketLogSDDao.save(msg, 1, vtResponseBean.getId(), Thread.currentThread().getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    String cmd = vtResponseBean.getCmd();
                    boolean success = vtResponseBean.getSuccess();
                    Long id = vtResponseBean.getId();

                    if("SHTRF".equals(cmd) && success){
                        myApp.readDataSDDao.updateResponseFlag(id, System.currentTimeMillis());
                        Log.d(TAG, "shtrf, received, id="+id);
                    }

                    if("GPSDATA".equals(cmd) && success){
                        myApp.gpsDataSDDao.updateSendFlag(id, 1);
                        Log.d(TAG, "gpsData, received, id="+id);
                    }

                    if("NODEPARAM".equals(cmd) && success){
                        NodeParamResponseBean nodeParamResponseBean = JSON.parseObject(msg, NodeParamResponseBean.class);
                        myApp.readDataRealtimeSDDao.updateRealtime(nodeParamResponseBean);
                    }

                    if("SYS".equals(cmd) && success){
                        SysResponseBean sysResponseBean = JSON.parseObject(msg, SysResponseBean.class);
                        myApp.queryConfigRealtimeSDDao.update(sysResponseBean);
                    }

                    if("STATE".equals(cmd) && success){
                        if(Constant.IS_UP_MODULE_LOG){
                            if(id < 999000000){
                                myApp.moduleBufSDDao.updateSendFlag(id, true);
                                Log.d(TAG, "STATE moduleBuf, received, id="+id);
                            }
                        }

                        if(Constant.IS_UP_APP_LOG){
                            if(id > 999000000) {
                                long appLogId = id - 999000000;
                                myApp.appLogSDDao.updateSendFlag(appLogId, 1);
                                Log.d(TAG, "STATE appLog, received, id=" + id);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Log.d(TAG, "messageSent：" + message.toString());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Log.d(TAG, "sessionClosed："+session);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        Log.d(TAG, "sessionCreated："+session);
        session.getConfig().setBothIdleTime(ConnectUtils.IDLE_TIME);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        Log.d(TAG, "sessionIdle："+session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        Log.d(TAG, "sessionOpened："+session);
    }
}
