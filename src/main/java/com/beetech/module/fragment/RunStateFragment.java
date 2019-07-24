package com.beetech.module.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.beetech.module.R;
import com.beetech.module.activity.MainActivity;
import com.beetech.module.activity.SynthActivity;
import com.beetech.module.activity.UpdateConfigActivity;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.bean.vt.NodeParamRequestBean;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.BaseSDDaoUtils;
import com.beetech.module.dao.ReadDataRealtimeSDDao;
import com.beetech.module.dao.VtSocketLogSDDao;
import com.beetech.module.utils.AppStateUtils;
import com.beetech.module.utils.DeleteHistoryDataUtils;
import com.beetech.module.utils.ModuleUtils;
import com.beetech.module.utils.NodeParamUtils;
import com.beetech.module.utils.QueryConfigUtils;
import com.beetech.module.utils.SetDataBeginTimeUtils;
import com.beetech.module.utils.SetTimeUtils;
import com.beetech.module.utils.UpdateSSParamUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.apache.mina.core.session.IoSession;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private ModuleUtils moduleUtils;
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
        moduleUtils = new ModuleUtils(mContext);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "QueryConfigUtils.queryConfig");
                try{
                    final boolean sendResult = QueryConfigUtils.queryConfig(getContext());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "发送查询本地配置指令"+(sendResult ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                            SystemClock.sleep(1000);
                            refreshState();
                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "QueryConfigUtils.queryConfig 异常", e);
                }

            }

        }).start();
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
        try {
            int ret = UpdateSSParamUtils.updateSSParam(getContext());
            switch (ret){
                case -2:
                    Toast.makeText(mContext, "串口未初始化或未上电，请稍后再试", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mContext, "无监测点", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(mContext, "已发送修改SS时间阈值指令", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnRefreshNode)
    public void btnRefreshNode_onClick(View v) {
        try {
            int minute = 60;
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1*minute);
            Date updateTimeEnd = cal.getTime();
            myApp.readDataRealtimeSDDao.deleteByUpdateTime(updateTimeEnd);

            Toast.makeText(mContext, "删除 "+updateTimeEnd+"后未上报数据监测节点", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "刷新标签异常", e);
        }
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "DeleteHistoryDataUtils.deleteHistoryData");
                        try {
                            DeleteHistoryDataUtils.deleteHistoryData(myApp);
                            myApp.appLogSDDao.save("删除模块历史数据");

                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "删除模块历史数据完成", Toast.LENGTH_SHORT).show();
                                    SystemClock.sleep(1000);
                                    refreshState();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "DeleteHistoryDataUtils.deleteHistoryData 异常", e);
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
                moduleUtils.free();
                myApp.manualStopModuleFlag = 1;
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
                moduleUtils.init();
                myApp.manualStopModuleFlag = 0;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "SetTimeUtils.setTime");
                try {
                    SetTimeUtils.setTime(myApp);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "更新时间完成", Toast.LENGTH_SHORT).show();
                            SystemClock.sleep(1000);
                            refreshState();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "SetTimeUtils.setTime 异常", e);
                }
            }
        }).start();
    }

    @OnClick(R.id.btnSetDataBeginTime)
    public void btnSetDataBeginTime_OnClick(View v) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要设置模块数据开始时间吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "SetDataBeginTimeUtils.setDataBeginTime");
                        try {
                            SetDataBeginTimeUtils.setDataBeginTime(myApp);
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "设置数据开始时间完成", Toast.LENGTH_SHORT).show();
                                    SystemClock.sleep(1000);
                                    refreshState();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "SetDataBeginTimeUtils.setDataBeginTime 异常", e);
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

    @OnClick(R.id.btnSynth)
    public void btnSynth_OnClick(View v) {
        startActivity(new Intent(mContext, SynthActivity.class));
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

}
