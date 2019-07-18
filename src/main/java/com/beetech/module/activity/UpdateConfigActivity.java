package com.beetech.module.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.beetech.module.R;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.code.request.UpdateConfigRequest;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.ByteUtilities;
import com.beetech.module.utils.DeleteHistoryDataUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.rscja.deviceapi.Module;

public class UpdateConfigActivity extends Activity {
    private static final String TAG = UpdateConfigActivity.class.getSimpleName();

    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;

    @ViewInject(R.id.customer_et)
    private EditText customerEt;

    @ViewInject(R.id.pattern_et)
    private EditText patternEt;

    @ViewInject(R.id.bps_et)
    private EditText bpsEt;

    @ViewInject(R.id.channel_et)
    private EditText channelEt;

    @ViewInject(R.id.update_btn)
    private Button updateBtn;

    private MyApplication myApp;
    private QueryConfigRealtime queryConfigRealtime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_update_config);
        ViewUtils.inject(this);
        try {
            myApp = (MyApplication) getApplicationContext();
            tvTitle.setText("编号 " + myApp.gwId + " 网关修改本地配置");

            queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if (queryConfigRealtime != null) {
                String customer = queryConfigRealtime.getCustomer();
                int pattern = queryConfigRealtime.getPattern();
                int bps = queryConfigRealtime.getBps();
                int channel = queryConfigRealtime.getChannel();
                if (!TextUtils.isEmpty(customer)) {
                    customerEt.setText(customer);
                }
                if (pattern == 3 || pattern == 4) {
                    patternEt.setText(String.valueOf(pattern));
                }
                if (bps == 1 || bps == 0) {
                    bpsEt.setText(String.valueOf(bps));
                }

                channelEt.setText(String.valueOf(channel));
            }

            updateBtn.setOnClickListener(new UpdateBtnOnClickListener());

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    class UpdateBtnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new  AlertDialog.Builder(UpdateConfigActivity.this);
            builder.setMessage("确定要修改模块参数吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    new Thread(){
                        @Override
                        public void run() {
                           final boolean sendResult = updateConfig();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UpdateConfigActivity.this, "修改模块配置指令发送"+(sendResult ? "成功" : "失败, 先查询配置后再试"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }.start();

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();


        }
    }

    public boolean updateConfig(){
        boolean sendResult = false;
        try {
            String customer = customerEt.getText().toString();
            String pattern = patternEt.getText().toString();
            String bps = bpsEt.getText().toString();
            String channel = channelEt.getText().toString();

            if (queryConfigRealtime == null) {
                return sendResult;
            }

            UpdateConfigRequest updateConfigRequest = new UpdateConfigRequest(queryConfigRealtime);
            if (!TextUtils.isEmpty(customer)) {
                updateConfigRequest.setCustomer(customer);
            }
            if (!TextUtils.isEmpty(pattern)) {
                updateConfigRequest.setPattern(Integer.valueOf(pattern));
            }
            if (!TextUtils.isEmpty(bps)) {
                updateConfigRequest.setBps(Integer.valueOf(bps));
            }

            if (!TextUtils.isEmpty(channel)) {
                updateConfigRequest.setChannel(Integer.valueOf(channel));
            }
            updateConfigRequest.pack();
            byte[] buf = updateConfigRequest.getBuf();
            Log.d(TAG, "updateConfigRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());

            Module module = myApp.module;
            if (module != null && myApp.initResult) {
                sendResult = module.send(buf);
                myApp.lastWriteTime = System.currentTimeMillis();

                if (Constant.IS_SAVE_MODULE_LOG) {
                    try {
                        myApp.moduleBufSDDao.save(buf, 0, updateConfigRequest.getCmd(), sendResult); // 保存串口通信数据
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "修改模块参数异常", e);
        }

        return sendResult;
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy");
        super.onDestroy();
    }


}
