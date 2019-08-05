package com.beetech.module.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.beetech.module.R;
import com.beetech.module.activity.MainActivity;
import com.beetech.module.activity.RealtimeMonitorActivity;
import com.beetech.module.activity.SynthActivity;
import com.beetech.module.activity.UpdateConfigActivity;
import com.beetech.module.application.MyApplication;
import com.beetech.module.dao.BaseSDDaoUtils;
import com.beetech.module.dao.ReadDataRealtimeSDDao;
import com.beetech.module.dao.VtSocketLogSDDao;
import com.beetech.module.utils.AppStateUtils;
import com.beetech.module.utils.NodeParamUtils;
import com.beetech.module.utils.RestartUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class RunStateFragment extends Fragment {
    private static final String TAG = RunStateFragment.class.getSimpleName();

    private MainActivity mContext;
    MyApplication myApp;

    @ViewInject(R.id.stateTv)
    TextView stateTv;

    @ViewInject(R.id.btnRefresh)
    Button btnRefresh;

    @ViewInject(R.id.btnQueryConfig)
    Button btnQueryConfig;

    @ViewInject(R.id.btnUpdateConfig)
    Button btnUpdateConfig;

    @ViewInject(R.id.btnNodeParam)
    Button btnNodeParam;

    @ViewInject(R.id.btnUpdateSSParam)
    Button btnUpdateSSParam;

    @ViewInject(R.id.btnRefreshNode)
    Button btnRefreshNode;

    @ViewInject(R.id.btnTruncateLog)
    Button btnTruncateLog;

    @ViewInject(R.id.btnTruncateAll)
    Button btnTruncateAll;

    @ViewInject(R.id.btnDeleteHistoryData)
    Button btnDeleteHistoryData;

    @ViewInject(R.id.btnModuleFree)
    private Button btnModuleFree;

    @ViewInject(R.id.btnModuleInit)
    private Button btnModuleInit;

    @ViewInject(R.id.btnSetTime)
    private Button btnSetTime;
    @ViewInject(R.id.btnSetDataBeginTime)
    private Button btnSetDataBeginTime;

    @ViewInject(R.id.btnSynth)
    private Button btnSynth;
    @ViewInject(R.id.btnRebootApp)
    private Button btnRebootApp;
    @ViewInject(R.id.btnRet)
    private Button btnRet;

    private ReadDataRealtimeSDDao readDataRealtimeSDDao;
    private VtSocketLogSDDao vtSocketLogSDDao;
    private int refreshInterval = 1000*10; //刷新数据线程启动间隔
    private BaseSDDaoUtils baseSDDaoUtils;
    public ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.run_state_fragment, container, false);
        ViewUtils.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        myApp = (MyApplication)mContext.getApplicationContext();
        baseSDDaoUtils = new BaseSDDaoUtils(mContext);
        readDataRealtimeSDDao = new ReadDataRealtimeSDDao(mContext);
        vtSocketLogSDDao = new VtSocketLogSDDao(mContext);

        // 启动刷新运行状态
        handlerRefresh.removeCallbacks(runnableRefresh);
        handlerRefresh.postDelayed(runnableRefresh, 0);
    }

    @OnClick(R.id.btnRefresh)
    public void btnRefresh_onClick(View v) {
        handlerRefresh.removeCallbacks(runnableRefresh);
        handlerRefresh.postDelayed(runnableRefresh, 0);
    }

    @OnClick(R.id.btnQueryConfig)
    public void btnQueryConfig_onClick(View v) {
        Message msg = new Message();
        msg.what = 1;
        myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
    }

    @OnClick(R.id.btnUpdateConfig)
    public void btnUpdateConfig_onClick(View v) {
        try {
            Intent intent = new Intent(getContext(), UpdateConfigActivity.class);
            startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnNodeParam)
    public void btnNodeParam_onClick(View v) {
        try {
            int ret = NodeParamUtils.requestNodeParam(getContext());
            switch (ret){
                case -2:
                    Toast.makeText(mContext, "网关连接断开，请稍后再试", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mContext, "无监测点", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(mContext, "已发送获取节点参数请求", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnUpdateSSParam)
    public void btnUpdateSSParam_onClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要修改SS时间阈值吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try {
                    Message msg = new Message();
                    msg.what = 0x9c;
                    myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @OnClick(R.id.btnRefreshNode)
    public void btnRefreshNode_onClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要刷新标签节点吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try {
                    myApp.readDataRealtimeSDDao.truncate();

                    Message msg = new Message();
                    msg.obj = "清空标签监测节点";
                    handlerToast.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "刷新标签异常", e);
                } finally {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @OnClick(R.id.btnTruncateLog)
    public void btnTruncateLog_onClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要清空全部日志数据吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                TruncateLogAsyncTask task = new TruncateLogAsyncTask();
                task.execute();
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


    class TruncateLogAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {

            if(progressDialog == null){
                try {
                    progressDialog = ProgressDialog.show(getContext(), "系统提示", "加载中，请稍后...");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            baseSDDaoUtils.trancateLog();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @OnClick(R.id.btnTruncateAll)
    public void btnTruncateAll_onClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要清空全部数据吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                TruncateAllAsyncTask task = new TruncateAllAsyncTask();
                task.execute();
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

    @OnClick(R.id.btnDeleteHistoryData)
    public void btnDeleteHistoryData_onClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要删除模块历史数据吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.what = 3;
                myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
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

    class TruncateAllAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            if(progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            baseSDDaoUtils.trancateAll();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @OnClick(R.id.btnModuleFree)
    public void btnModuleFree_OnClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要 模块释放 吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.what = -1;
                myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);

                myApp.manualStopModuleFlag = 1;
                myApp.appLogSDDao.save("模块释放手动");
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

    @OnClick(R.id.btnModuleInit)
    public void btnModuleInit_OnClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要 模块上电 吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.what = 0;
                myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);

                myApp.manualStopModuleFlag = 0;
                myApp.appLogSDDao.save("模块上电手动");
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

    @OnClick(R.id.btnSetTime)
    public void btnSetTime_OnClick(View v) {
        Message msg = new Message();
        msg.what = 4;
        myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
    }

    @OnClick(R.id.btnSetDataBeginTime)
    public void btnSetDataBeginTime_OnClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要设置模块数据开始时间吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.what = 9;
                myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
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

    @OnClick(R.id.btnSynth)
    public void btnSynth_OnClick(View v) {
        startActivity(new Intent(mContext, SynthActivity.class));
    }

    @OnClick(R.id.btnRet)
    public void btnRet_OnClick(View v) {
        startActivity(new Intent(mContext, RealtimeMonitorActivity.class));
    }

    @OnClick(R.id.btnRebootApp)
    public void btnRebootApp_OnClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要重启应用吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "RestartUtils.restartApplication");
                        try {
                            RestartUtils.restartApplication(mContext);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "RestartUtils.restartApplication 异常", e);
                        }
                    }
                }).start();
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

    @Override
    public void onResume() {
        super.onResume();
    }

    //定时刷新运行状态
    private Handler handlerRefresh = new Handler(){};
    Runnable runnableRefresh = new Runnable() {
        @Override
        public void run() {
            try {
                refreshState();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handlerRefresh.postDelayed(this, refreshInterval);
        }
    };

    public void refreshState(){
        try {
            StringBuffer stateSb = AppStateUtils.getState(myApp);
            stateTv.setText(stateSb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
