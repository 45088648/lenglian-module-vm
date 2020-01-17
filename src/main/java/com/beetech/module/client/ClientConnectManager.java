package com.beetech.module.client;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.utils.NetworkUtils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ClientConnectManager {
    private final static String TAG = ClientConnectManager.class.getSimpleName();

    //0 已连接，1 连接中
    public static ThreadLocal<Integer> state = new ThreadLocal<Integer>(){
        // 重写这个方法，可以修改“线程变量”的初始值，默认是null
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    private static ClientConnectManager instance;
    public static ClientConnectManager getInstance(Context context) {
        if (null == instance) {
            instance = new ClientConnectManager(context);
        }
        return instance;
    }

    private ClientConnectManager(Context context) {
        this.context = context;
        this.appLogSDDao = new AppLogSDDao(context);
        this.myApp = (MyApplication)context.getApplicationContext();
    }

    private MyApplication myApp;
    private Context context;
    private AppLogSDDao appLogSDDao;

    public void connect() {
        if(!NetworkUtils.isNetworkAvailable(myApp)){
            Log.d(TAG, " 网络不可用，"+ ConnectUtils.HOST +", "+ConnectUtils.PORT);
            return;
        }
        Log.d(TAG, "state="+state.get());
        if(state.get()==1){
            Log.d(TAG, " VT网关连接中，"+ConnectUtils.HOST +", "+ConnectUtils.PORT);
            return;
        }
        final String threadName = Thread.currentThread().getName();
        Log.d(TAG, "创建VT网关连接 开始");
        long start = SystemClock.currentThreadTimeMillis();
        NioSocketConnector mSocketConnector = getNioSocketConnector();
        if(mSocketConnector == null) {
            Log.e(TAG, "mSocketConnector is null");
            return;
        }
        //配置服务器地址
        InetSocketAddress mSocketAddress = new InetSocketAddress(ConnectUtils.HOST, ConnectUtils.PORT);
        //发起连接
        ConnectFuture mFuture = mSocketConnector.connect(mSocketAddress);
        state.set(1);
        mFuture.awaitUninterruptibly();
        IoSession mSession = mFuture.getSession();
        Log.d(TAG, "创建网关连接 成功" + mSession.toString());
        long end = SystemClock.currentThreadTimeMillis();
        Log.d(TAG, "创建网关连接耗时：" + (end - start) + "毫秒");
        appLogSDDao.save(threadName+", 网关连接成功，"+ConnectUtils.HOST+", "+ConnectUtils.PORT);
        myApp.session = mSession;
    }

    public NioSocketConnector getNioSocketConnector(){
        NioSocketConnector mSocketConnector = null;
        try {
            if (mSocketConnector == null) {
                mSocketConnector = new NioSocketConnector();
                mSocketConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, ConnectUtils.IDLE_TIME);

                mSocketConnector.setConnectTimeoutMillis(30000); //设置连接超时
                TextLineCodecFactory codecFactory = new TextLineCodecFactory(Charset.forName("GBK"), "\0", "\0");
                codecFactory.setDecoderMaxLineLength(1024*1024);
                codecFactory.setEncoderMaxLineLength(1024*1024);
                mSocketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));

                // 获取过滤器链
                DefaultIoFilterChainBuilder filterChain = mSocketConnector.getFilterChain();

                //设置 handler 处理业务逻辑
                mSocketConnector.setHandler(new MyHandler(context));
                mSocketConnector.addListener(new HeartBeatListener(mSocketConnector, context));
                SocketSessionConfig socketSessionConfig = mSocketConnector.getSessionConfig();
                socketSessionConfig.setReceiveBufferSize(1024*1024);    // 设置接收缓冲区的大小
                socketSessionConfig.setSendBufferSize(1024*1024);   // 设置输出缓冲区的大小
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "NioSocketConnector 创建失败"+e.getMessage(), e);
        }
        return mSocketConnector;
    }
}
