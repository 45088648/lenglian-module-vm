package com.beetech.module.client;

import android.content.Context;
import android.util.Log;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * 监听服务器断线原因
 */
public class HeartBeatListener implements IoServiceListener {
    private final static String TAG = HeartBeatListener.class.getSimpleName();
    public NioSocketConnector connector;
    private Context context;

    public HeartBeatListener(NioSocketConnector connector, Context context) {
        this.connector = connector;
        this.context = context;
    }

    @Override
    public void serviceActivated(IoService arg0) throws Exception {
    }

    @Override
    public void serviceDeactivated(IoService arg0) throws Exception {
    }

    @Override
    public void serviceIdle(IoService arg0, IdleStatus arg1) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession arg0) throws Exception {
        Log.d(TAG, "sessionClosed");
    }

    @Override
    public void sessionCreated(IoSession arg0) throws Exception {
    }

    @Override
    public void sessionDestroyed(IoSession arg0) {
        ClientConnectManager.getInstance(context).rePeatConnect();
    }
}
