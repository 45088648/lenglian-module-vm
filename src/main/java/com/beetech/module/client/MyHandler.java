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
        Log.d(TAG, ConnectUtils.stringNowTime() + " : 客户端调用exceptionCaught");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        Log.d(TAG,  "接收到服务器端消息：" + message.toString());

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

                    if("SHTRF".equals(cmd) && success){
                        Long id = vtResponseBean.getId();
                        myApp.readDataSDDao.updateResponseFlag(id, System.currentTimeMillis());
                        Log.d(TAG, "shtrf, received, id="+id);
                    }

                    if("GPSDATA".equals(cmd) && success){
                        Long id = vtResponseBean.getId();
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Log.d(TAG, ConnectUtils.stringNowTime() + " : 客户端调用messageSent" + message.toString());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Log.d(TAG, ConnectUtils.stringNowTime() + " : 客户端调用sessionClosed");
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        Log.d(TAG, ConnectUtils.stringNowTime() + " : 客户端调用sessionCreated");
        session.getConfig().setBothIdleTime(ConnectUtils.IDLE_TIME);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        Log.d(TAG, ConnectUtils.stringNowTime() + " : 客户端调用sessionIdle");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        Log.d(TAG, ConnectUtils.stringNowTime() + " : 客户端调用sessionOpened");
    }
}
