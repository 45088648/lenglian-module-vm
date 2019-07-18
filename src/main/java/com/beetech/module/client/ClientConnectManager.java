package com.beetech.module.client;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.dao.AppLogSDDao;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ClientConnectManager {
    private final static String TAG = ClientConnectManager.class.getSimpleName();

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
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                NioSocketConnector mSocketConnector = getNioSocketConnector();

                //配置服务器地址
                InetSocketAddress mSocketAddress = new InetSocketAddress(ConnectUtils.HOST, ConnectUtils.PORT);
                //发起连接
                ConnectFuture mFuture = mSocketConnector.connect(mSocketAddress);
                mFuture.awaitUninterruptibly();
                IoSession mSession = mFuture.getSession();
                Log.d("", "======连接成功" + mSession.toString());

                e.onNext(mSession);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull Object o) {
                IoSession mSession = (IoSession) o;
                Log.d(TAG, "======VT网关连接成功====" + mSession.isConnected());
                appLogSDDao.save("VT网关连接成功，"+ConnectUtils.HOST+", "+ConnectUtils.PORT);
                myApp.session = mSession;
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }


    public void rePeatConnect() {
        final boolean[] isRepeat = {false};
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                // 执行到这里表示Session会话关闭了，需要进行重连,我们设置每隔30s重连一次
                int count = 0;// 记录尝试重连的次数
                NioSocketConnector mSocketConnector = null;
                while (!isRepeat[0]) {
                    try {
                        count++;
                        mSocketConnector = getNioSocketConnector();

                        //配置服务器地址
                        InetSocketAddress mSocketAddress = new InetSocketAddress(ConnectUtils.HOST, ConnectUtils.PORT);
                        //发起连接
                        ConnectFuture mFuture = mSocketConnector.connect(mSocketAddress);
                        mFuture.awaitUninterruptibly();
                        IoSession mSession = mFuture.getSession();
                        if (mSession.isConnected()) {
                            isRepeat[0] = true;
                            Log.d(TAG, "======连接成功" + mSession.toString());
                            e.onNext(mSession);
                            e.onComplete();
                            break;
                        }
                    } catch (Exception e1) {
                        StringBuffer contentSb = new StringBuffer();
                        if (count == ConnectUtils.REPEAT_TIME) {
                            contentSb.append(ConnectUtils.stringNowTime()).append(" : 断线重连").append(ConnectUtils.REPEAT_TIME + "次之后仍然未成功,结束重连.....");
                            Log.d(TAG,contentSb.toString());
                            break;
                        } else {
                            try {
                                Thread.sleep(1000 * 30);

                                contentSb.append("VT网关连接断线重连，").append(",30s后进行第").append((count + 1)).append("次重连.....，e=").append(e1.getMessage());
                                Log.d(TAG, contentSb.toString());
                                appLogSDDao.save(contentSb.toString());

                            } catch (InterruptedException e12) {
                                Log.e(TAG, "rePeatConnect e12" + e12);
                            }
                        }
                    }

                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull Object o) {
                IoSession mSession = (IoSession) o;
                Log.d(TAG, "======连接成功====" + mSession.isConnected());
                appLogSDDao.save("VT网关断线重连成功，"+ConnectUtils.HOST+", "+ConnectUtils.PORT);
                myApp.session = mSession;
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public NioSocketConnector getNioSocketConnector(){
        NioSocketConnector mSocketConnector = null;
        if (mSocketConnector == null) {
            mSocketConnector = new NioSocketConnector();
            mSocketConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, ConnectUtils.IDLE_TIME);

            TextLineCodecFactory codecFactory = new TextLineCodecFactory(Charset.forName("GBK"), "\0", "\0");
            codecFactory.setDecoderMaxLineLength(1024*1024);
            codecFactory.setEncoderMaxLineLength(1024*1024);
            mSocketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));

            // 获取过滤器链
            DefaultIoFilterChainBuilder filterChain = mSocketConnector.getFilterChain();

            //设置 handler 处理业务逻辑
            mSocketConnector.setHandler(new MyHandler(context));
            mSocketConnector.addListener(new HeartBeatListener(mSocketConnector, context));

            mSocketConnector.getSessionConfig().setReceiveBufferSize(10240);	// 设置接收缓冲区的大小
            mSocketConnector.getSessionConfig().setSendBufferSize(10240);// 设置输出缓冲区的大小
        }

        return mSocketConnector;
    }
}
