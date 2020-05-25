package com.beetech.module.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
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
import com.beetech.module.activity.UpdateConfigActivity;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.BaseSDDaoUtils;
import com.beetech.module.dao.ReadDataRealtimeSDDao;
import com.beetech.module.dao.VtSocketLogSDDao;
import com.beetech.module.update.view.CommonProgressDialog;
import com.beetech.module.utils.AppStateUtils;
import com.beetech.module.utils.NodeParamUtils;
import com.beetech.module.utils.RestartUtils;
import com.chainway.libs.mylibrary.CwSpecialFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    @ViewInject(R.id.btnStartAlarm)
    private Button btnStartAlarm;

    @ViewInject(R.id.btnStopAlarm)
    private Button btnStopAlarm;

    @ViewInject(R.id.btnSetTime)
    private Button btnSetTime;
    @ViewInject(R.id.btnSetDataBeginTime)
    private Button btnSetDataBeginTime;

    @ViewInject(R.id.btnRebootApp)
    private Button btnRebootApp;
    @ViewInject(R.id.btnRet)
    private Button btnRet;

    @ViewInject(R.id.btnVersionUpdate)
    private Button btnVersionUpdate;

    @ViewInject(R.id.btnShutDownDevice)
    private Button btnShutDownDevice;

    @ViewInject(R.id.btnRebootDevice)
    private Button btnRebootDevice;

    @ViewInject(R.id.btnSetSysLauncher)
    private Button btnSetSysLauncher;

    @ViewInject(R.id.btnSetLauncherClass)
    private Button btnSetLauncherClass;

    private CommonProgressDialog pBar;

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
        if(myApp.monitorState==1){
            Toast.makeText(getContext(), "监控中，请先结束监控再操作", Toast.LENGTH_SHORT).show();
            return;
        }
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
        if(myApp.monitorState==1){
            Toast.makeText(getContext(), "监控中，请先结束监控再操作", Toast.LENGTH_SHORT).show();
            return;
        }
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

    @OnClick(R.id.btnStopAlarm)
    public void btnStopAlarm_OnClick(View v) {
        if(myApp.alarmFlag) {
            myApp.alarmFlag = false;
            try {
                myApp.queryConfigRealtimeSDDao.updateByAlarmFlag(myApp.alarmFlag);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "updateByAlarmFlag 异常, "+e.getMessage());
            }
            Toast.makeText(getContext(), "关闭报警", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "报警已关闭", Toast.LENGTH_SHORT).show();
        }
        myApp.appLogSDDao.save("关闭报警");
    }

    @OnClick(R.id.btnStartAlarm)
    public void btnStartAlarm_OnClick(View v) {
        if(!myApp.alarmFlag) {
            myApp.alarmFlag = true;
            try {
                myApp.queryConfigRealtimeSDDao.updateByAlarmFlag(myApp.alarmFlag);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "updateByAlarmFlag 异常, "+e.getMessage());
            }
            Toast.makeText(getContext(), "开启报警", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "报警已开启", Toast.LENGTH_SHORT).show();
        }
        myApp.appLogSDDao.save("开启报警");
    }

    @OnClick(R.id.btnIsSetDataBeginTimeByBootTrue)
    public void btnIsSetDataBeginTimeByBootTrue_OnClick(View v) {
        if(!myApp.isSetDataBeginTimeByBoot) {
            myApp.isSetDataBeginTimeByBoot = true;
            try {
                Log.d(TAG, "isSetDataBeginTimeByBoot="+myApp.isSetDataBeginTimeByBoot);
                myApp.queryConfigRealtimeSDDao.updateByIsSetDataBeginTimeByBoot(myApp.isSetDataBeginTimeByBoot);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "updateByIsSetDataBeginTimeByBoot 异常, "+e.getMessage());
            }
            Toast.makeText(getContext(), "开启开机设置数据开始时间", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "开机设置数据开始时间已开启", Toast.LENGTH_SHORT).show();
        }

        myApp.appLogSDDao.save("开启开机设置数据开始时间");
    }

    @OnClick(R.id.btnIsSetDataBeginTimeByBootFalse)
    public void btnIsSetDataBeginTimeByBootFalse_OnClick(View v) {
        if(myApp.isSetDataBeginTimeByBoot) {
            myApp.isSetDataBeginTimeByBoot = false;
            try {
                Log.d(TAG, "isSetDataBeginTimeByBoot="+myApp.isSetDataBeginTimeByBoot);
                myApp.queryConfigRealtimeSDDao.updateByIsSetDataBeginTimeByBoot(myApp.isSetDataBeginTimeByBoot);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "updateByIsSetDataBeginTimeByBoot 异常, "+e.getMessage());
            }
            Toast.makeText(getContext(), "关闭开机设置数据开始时间", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "开机设置数据开始时间已关闭", Toast.LENGTH_SHORT).show();
        }

        myApp.appLogSDDao.save("关闭开机设置数据开始时间");
    }

    @OnClick(R.id.btnSetTime)
    public void btnSetTime_OnClick(View v) {
        Message msg = new Message();
        msg.what = 4;
        myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
    }

    @OnClick(R.id.btnSetDataBeginTime)
    public void btnSetDataBeginTime_OnClick(View v) {
        if(myApp.monitorState==1){
            Toast.makeText(getContext(), "监控中，请先结束监控再操作", Toast.LENGTH_SHORT).show();
            return;
        }
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

    @OnClick(R.id.btnVersionUpdate)
    public void btnVersionUpdate_OnClick(View v) {
        Log.d(TAG, "btnVersionUpdate_OnClick");
        try {
            showDialogVersionUpdate(Constant.VERSION_DOWNLOAD_URL);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "btnVersionUpdate_OnClick 异常", e);
        }
    }

    @OnClick(R.id.btnShutDownDevice)
    public void btnShutDownDevice_OnClick(final View v) {
        Log.d(TAG, "btnShutDownDevice_OnClick");
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要关机吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                v.setClickable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "CwSpecialFunc.getInstance().shutDownDevice");
                        try {
                            CwSpecialFunc.getInstance().shutDownDevice();//关机
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "btnShutDownDevice_OnClick 异常", e);
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

    @OnClick(R.id.btnRebootDevice)
    public void btnRebootDevice_OnClick(final View v) {
        Log.d(TAG, "btnRebootDevice_OnClick");

        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要重启吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                v.setClickable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "CwSpecialFunc.getInstance().rebootDevice");
                        try {
                            CwSpecialFunc.getInstance().rebootDevice();//重启设备
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "btnRebootDevice_OnClick 异常", e);
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @OnClick(R.id.btnSetSysLauncher)
    public void btnSetSysLauncher_OnClick(View v) {
        Log.d(TAG, "btnSetSysLauncher_OnClick");
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要恢复系统默认页面吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "CwSpecialFunc.getInstance().setSysLauncher");
                        try {
                            CwSpecialFunc.getInstance().setDownStatusEnable(true);//恢复下拉菜单
                            CwSpecialFunc.getInstance().setHomeKeyEnable(true);   //恢复HOME按键
                            CwSpecialFunc.getInstance().setMenuKeyEnable(true);   //恢复菜单按键
                            CwSpecialFunc.getInstance().setSysLauncher(); //恢复系统默认页面，方法一
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "btnSetSysLauncher_OnClick 异常", e);
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

    @OnClick(R.id.btnSetLauncherClass)
    public void btnSetLauncherClass_OnClick(View v) {
        Log.d(TAG, "btnSetLauncherClass_OnClick");
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setMessage("确定要设置开机启动吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "CwSpecialFunc.getInstance().setLauncherClass");
                        try {
                            CwSpecialFunc.getInstance().setDownStatusEnable(false);//禁止下拉菜单
                            CwSpecialFunc.getInstance().setHomeKeyEnable(false);   //禁止HOME按键
                            CwSpecialFunc.getInstance().setMenuKeyEnable(false);   //禁止菜单按键

                            String packageName = myApp.getPackageName();
                            String className = packageName.concat(".activity.RealtimeMonitorActivity");
                            Log.d(TAG, "packageName = "+packageName+", className = "+className);
                            CwSpecialFunc.getInstance().setLauncherClass(packageName, className);//开机默认启动Activity

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "btnSetLauncherClass_OnClick 异常", e);
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

    /**
     * 升级系统
     */
    private void showDialogVersionUpdate(final String url) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("版本更新")
                .setMessage("确定更新软件版本吗？")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        pBar = new CommonProgressDialog(getContext());
                        pBar.setCanceledOnTouchOutside(false);
                        pBar.setTitle("正在下载");
                        pBar.setCustomTitle(LayoutInflater.from(
                                getContext()).inflate(
                                R.layout.title_dialog, null));
                        pBar.setMessage("正在下载");
                        pBar.setIndeterminate(true);
                        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pBar.setCancelable(true);

                        final DownloadTask downloadTask = new DownloadTask(myApp);
                        downloadTask.execute(url);
                        pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                downloadTask.cancel(true);
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 下载应用
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    file = new File(Environment.getExternalStorageDirectory(), Constant.DOWNLOAD_NAME);

                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "sd卡未挂载", Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            pBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pBar.setIndeterminate(false);
            pBar.setMax(100);
            pBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pBar.dismiss();
            update();
        }
    }

    private void update() {
        //安装应用
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Constant.DOWNLOAD_NAME)),"application/vnd.android.package-archive");
        startActivity(intent);
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
