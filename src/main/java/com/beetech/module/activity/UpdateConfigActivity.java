package com.beetech.module.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
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
import com.beetech.module.code.CommonBase;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class UpdateConfigActivity extends Activity {
    private static final String TAG = UpdateConfigActivity.class.getSimpleName();

    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;

    @ViewInject(R.id.customer_et)
    private EditText customerEt;

    @ViewInject(R.id.debug_et)
    private EditText debugEt;

    @ViewInject(R.id.category_et)
    private EditText categoryEt;

    @ViewInject(R.id.pattern_et)
    private EditText patternEt;

    @ViewInject(R.id.bps_et)
    private EditText bpsEt;

    @ViewInject(R.id.channel_et)
    private EditText channelEt;

    @ViewInject(R.id.txPower_et)
    private EditText txPowerEt;

    @ViewInject(R.id.update_btn)
    private Button updateBtn;

    private MyApplication myApp;
    private QueryConfigRealtime queryConfigRealtime;

    private int forwardFlag = 0;

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
                String customer = queryConfigRealtime.getCustomer().toUpperCase();
                int pattern = queryConfigRealtime.getPattern();
                int bps = queryConfigRealtime.getBps();
                int channel = queryConfigRealtime.getChannel();
                int txPower = queryConfigRealtime.getTxPower();
                forwardFlag = queryConfigRealtime.getForwardFlag();
                int debug = queryConfigRealtime.getDebug();
                int category = queryConfigRealtime.getCategory();
                Log.d(TAG, "customer=" + customer + ", debug=" + debug+ ", category=" + category + ", pattern=" + pattern + ", bps=" + bps + ", channel=" + channel + ", txPower=" + txPower + ", forwardFlag=" + forwardFlag);

                if (!TextUtils.isEmpty(customer)) {
                    customerEt.setText(customer);
                }
                debugEt.setText(String.valueOf(debug));
                categoryEt.setText(String.valueOf(category));

                patternEt.setText(String.valueOf(pattern));
                bpsEt.setText(String.valueOf(bps));
                channelEt.setText(String.valueOf(channel));

                txPowerEt.setText(String.valueOf(txPower));
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
                            try {
                                String debugStr = debugEt.getText().toString();
                                String categoryStr = categoryEt.getText().toString();

                                String patternStr = patternEt.getText().toString();
                                String bpsStr = bpsEt.getText().toString();
                                String channelStr = channelEt.getText().toString();

                                String txPowerStr = txPowerEt.getText().toString();

                                myApp.customer = customerEt.getText().toString();
                                myApp.debug = Integer.valueOf(debugStr);
                                myApp.category = Integer.valueOf(categoryStr);

                                myApp.pattern = Integer.valueOf(patternStr);
                                myApp.bps = Integer.valueOf(bpsStr);
                                myApp.channel = Integer.valueOf(channelStr);

                                myApp.txPower = Integer.valueOf(txPowerStr);
                                myApp.forwardFlag = forwardFlag;

                                Message msg = new Message();
                                msg.what = CommonBase.CMD_UPDATE_CONFIG;
                                myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "修改模块参数异常", e);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UpdateConfigActivity.this, "发送修改模块配置指令完成", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy");
        super.onDestroy();
    }


}
