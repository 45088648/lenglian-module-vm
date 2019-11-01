package com.beetech.module.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beetech.module.R;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.service.BlueToothService;
import com.beetech.module.utils.DateUtils;
import com.beetech.module.utils.EncryStrUtils;
import com.beetech.module.utils.PrintSetVo;
import com.beetech.module.utils.ReadDataAllPrintUtils;
import com.beetech.module.widget.RadioGroupEx;
import com.beetech.module.widget.time.OnDateEditClickListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class QueryDataAllActivity extends PrintActivity implements CheckBox.OnCheckedChangeListener{
    private static final String TAG = QueryDataAllActivity.class.getSimpleName();

    @ViewInject(R.id.tv_status)
    private TextView tvStatus;
    private Thread tv_update;

    @ViewInject(R.id.time_begin_et)
    private EditText timeBeginEt;

    @ViewInject(R.id.time_end_et)
    private EditText timeEndEt;

    @ViewInject(R.id.print_btn)
    private Button printBtn;

    @ViewInject(R.id.query_btn)
    private Button queryBtn;

    @ViewInject(R.id.requery_btn)
    private Button requeryBtn;

    @ViewInject(R.id.query_condition_ll)
    private LinearLayout queryConditionLl;

    @ViewInject(R.id.query_result_ll)
    private LinearLayout queryResultLl;

    @ViewInject(R.id.sensorId_select_rge)
    private RadioGroupEx sensorIdSelectRge;
    private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();

    @ViewInject(R.id.print_str_tv)
    private TextView printStrTv;

    @ViewInject(R.id.printTimeInterval_et)
    private EditText printTimeIntervalEt;

    @ViewInject(R.id.isContainOver_cb)
    private CheckBox isContainOverCb;
    private boolean isContainOver;

    @ViewInject(R.id.isContainStats_cb)
    private CheckBox isContainStatsCb;
    private boolean isContainStats;

    @ViewInject(R.id.plateNumber_et)
    private EditText plateNumberEt;

    private MyApplication myApp;

    public BlueToothService blueToothService;// 蓝牙打印服务对象
    private List<ReadDataResponse> dataList;
    private String printStr;
    private QueryConfigRealtime queryConfigRealtime;
    private ProgressDialog progressDialog;

    private Handler blueOutHandler;
    private String timeBeginStr;
    private String timeEndStr;

    private PrintHandler printHandler;
    private Handler mToastHandler;

    //打印参数
    private int printTimeInterval = 5; // 打印间隔，单位：分钟
    private List sensorIdListSelect = new LinkedList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_query_data_all);
        ViewUtils.inject(this);

        myApp = (MyApplication) getApplicationContext();

        if(blueToothService == null){
            blueToothService = new BlueToothService(this);
        }

        timeBeginEt.setOnClickListener(new OnDateEditClickListener(this));
        timeEndEt.setOnClickListener(new OnDateEditClickListener(this));

        QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
        String devName = "";
        if(queryConfigRealtime != null) {
            devName = queryConfigRealtime.getDevName();
        }
        //默认最近1个小时范围
        Calendar cal = Calendar.getInstance();
        Date endTime = null;
        if(myApp.endMonitorTime == null) {
            endTime = cal.getTime();
        } else {
            Calendar cal_endMonitor = Calendar.getInstance();
            cal_endMonitor.setTime(myApp.endMonitorTime);
            endTime = cal_endMonitor.getTime();
        }
        timeEndEt.setText(DateUtils.parseDateToString(endTime, DateUtils.C_YYYY_MM_DD_HH_MM));

        Date beginTime = null;
        if(myApp.beginMonitorTime == null){
            cal.add(Calendar.HOUR_OF_DAY, -1);
            beginTime = cal.getTime();
        } else {
            beginTime = myApp.beginMonitorTime;
        }
        timeBeginEt.setText(DateUtils.parseDateToString(beginTime, DateUtils.C_YYYY_MM_DD_HH_MM));

        blueOutHandler = new Handler();
        printHandler = new PrintHandler();
        tv_update = new TvUpdateThread();
        tv_update.start();

        mToastHandler = new ToastHandler();

        List<ReadDataRealtime> readDataRealtimeList = myApp.readDataRealtimeSDDao.queryAll();
        if(readDataRealtimeList != null && !readDataRealtimeList.isEmpty()){
            sensorIdListSelect.clear();
            int size = readDataRealtimeList.size();
            for (int i = 0; i< size; i++){
                ReadDataRealtime rdr = readDataRealtimeList.get(i);
                String sensorId = rdr.getSensorId();
                sensorIdListSelect.add(sensorId);

                CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.checkbox, null);
                checkBox.setText(sensorId);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener(this);
                checkBoxs.add(checkBox);
                sensorIdSelectRge.addView(checkBox, i);
            }
        }

        plateNumberEt.setText(devName);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String sensorId = buttonView.getText().toString();
        if(isChecked){
            if(!sensorIdListSelect.contains(sensorId)){
                sensorIdListSelect.add(sensorId);
            }
        } else {
            sensorIdListSelect.remove(sensorId);
        }
    }

    private void getPrintSet() {
        Editable printTimeIntervalEtText = printTimeIntervalEt.getText();
        if(printTimeIntervalEtText != null) {
            String printTimeIntervalStr = printTimeIntervalEtText.toString();
            if(!TextUtils.isEmpty(printTimeIntervalStr) && TextUtils.isDigitsOnly(printTimeIntervalStr)){
                int printTimeIntervalInt = Integer.valueOf(printTimeIntervalStr);
                if(printTimeIntervalInt > 0){
                    printTimeInterval = printTimeIntervalInt;
                }
            }
        }
        isContainOver = isContainOverCb.isChecked();
        isContainStats = isContainStatsCb.isChecked();
    }

    class TvUpdateThread extends Thread {

        private boolean tvFlag = true;

        public void run() {
            while (tvFlag) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tvStatus.post(new Runnable() {
                    @Override
                    public void run() {
                        if (blueToothService != null) {
                            Resources rs = getResources();
                            if (blueToothService.getState() == BlueToothService.STATE_CONNECTED) {
                                tvStatus.setText(rs.getString(R.string.str_connected));

                                printBtn.setText(rs.getString(R.string.str_print));
                                printBtn.setVisibility(View.VISIBLE);
                            } else if (blueToothService.getState() == BlueToothService.STATE_CONNECTING) {
                                tvStatus.setText(rs.getString(R.string.str_connecting));
                                printBtn.setVisibility(View.INVISIBLE);
                            } else {
                                tvStatus.setText(rs.getString(R.string.str_disconnected));
                                printBtn.setText(rs.getString(R.string.str_toconnect));
                                printBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }

    }

    @OnClick(R.id.query_btn)
    public void queryBtn_onClick(View v) {
        try {
            timeBeginStr = timeBeginEt.getText().toString();
            if (TextUtils.isEmpty(timeBeginStr)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryDataAllActivity.this, "请选择起始时间", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            timeEndStr = timeEndEt.getText().toString();
            if (TextUtils.isEmpty(timeEndStr)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryDataAllActivity.this, "请选择终止时间", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            Date timeBegin = DateUtils.parseStringToDate(timeBeginStr, DateUtils.C_YYYY_MM_DD_HH_MM);
            Date timeEnd = DateUtils.parseStringToDate(timeEndStr, DateUtils.C_YYYY_MM_DD_HH_MM);
            Log.d(TAG, "timeBeginStr = " + timeBeginStr + ", timeEndStr = " + timeEndStr);
            final int crossDay = 30;
            if (timeEnd.getTime() - timeBegin.getTime() > 1000L * 60 * 60 * 24 * crossDay) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryDataAllActivity.this, "时间范围不能超过" + crossDay + "天", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if (queryConfigRealtime == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryDataAllActivity.this, "设备本地配置信息不存在", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            getPrintSet();
            Log.d(TAG, "printTimeInterval=" + printTimeInterval);

            List<ReadDataRealtime> readDataRealtimeList = myApp.readDataRealtimeSDDao.queryAll();
            List<ReadDataRealtime> readDataRealtimeListSelect = new LinkedList<>();
            List<List<ReadDataResponse>> dataListAll = new LinkedList<>();
            PrintSetVo printSetVo = new PrintSetVo();
            if (readDataRealtimeList.size() <= 2) {
                printSetVo.setColSize(2);
            }
            printSetVo.setPrintTimeInterval(printTimeInterval);
            printSetVo.setPrintStats(isContainStats);
            printSetVo.setPlateNumber(plateNumberEt.getText().toString());
            for (ReadDataRealtime readDataRealtime : readDataRealtimeList) {
                String sensorId = readDataRealtime.getSensorId();
                if(sensorIdListSelect.isEmpty() || sensorIdListSelect.contains(sensorId)){
                    readDataRealtimeListSelect.add(readDataRealtime);
                } else {
                    continue;
                }
                List<ReadDataResponse> dataList = myApp.readDataSDDao.queryBySensorId(sensorId, timeBegin, timeEnd, Integer.MAX_VALUE, 0);
                List<ReadDataResponse> filterDataList = null;
                if(isContainOver){
                    filterDataList = dataList;
                } else {
                    filterDataList = ReadDataAllPrintUtils.filterDataList(dataList, printTimeInterval);
                }
                if(filterDataList == null || filterDataList.isEmpty()){
                    Toast.makeText(QueryDataAllActivity.this, "数据为空，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                dataListAll.add(filterDataList);
            }
            if(dataListAll != null && !dataListAll.isEmpty()){
                if(isContainOver){
                    printStr = ReadDataAllPrintUtils.toPrintStrOver(readDataRealtimeListSelect, dataListAll, printSetVo, queryConfigRealtime);
                } else {
                    printStr = ReadDataAllPrintUtils.toPrintStr(readDataRealtimeListSelect, dataListAll, printSetVo, queryConfigRealtime);
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryDataAllActivity.this, "数据为空，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            printStrTv.setText(printStr);

            queryConditionLl.setVisibility(View.GONE);
            queryResultLl.setVisibility(View.VISIBLE);

        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "查询异常", e);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QueryDataAllActivity.this, "查询异常，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @OnClick(R.id.print_btn)
    public void printBtn_onClick(View v) {
        if(TextUtils.isEmpty(printStr)){
            mToastHandler.sendEmptyMessage(3);
            return;
        }
        new AlertDialog.Builder(QueryDataAllActivity.this).setTitle("打印确认").setMessage("确认打印该数据吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toPrint();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                blueToothService.stop();
            }

        }).show();
    }

    @OnClick(R.id.requery_btn)
    public void requeryBtn_onClick(View v) {
        queryConditionLl.setVisibility(View.VISIBLE);
        queryResultLl.setVisibility(View.GONE);
    }

    public void toPrint() {
        printHandler.sendEmptyMessage(1);
    }
    /**
     * 连接打印机印机Handler
     */
    class PrintHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    List<String> mPairedDevices = blueToothService.scanDevices();
                    if(mPairedDevices == null || mPairedDevices.isEmpty()){
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.print_shuoming), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (blueToothService.isConnected()) {
                        this.sendEmptyMessage(2);
                    } else {
                        blueToothService.reConnect();
                    }
                    break;
                case 2:

                    // 打印
                    print();
                    break;
                default:
                    break;
            }

        }
    }

    class ToastHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    Toast.makeText(getApplicationContext(), "打印数据中，请稍后", Toast.LENGTH_SHORT).show();
                    if(progressDialog == null){
                        try {
                            progressDialog = ProgressDialog.show(QueryDataAllActivity.this, "系统提示", "打印数据中，请稍后...");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                case 2: {
                    Toast.makeText(getApplicationContext(), "打印完成，断开连接", Toast.LENGTH_SHORT).show();
                    blueToothService.stop();
                    if(progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    break;
                }

                case 3: {
                    Toast.makeText(getApplicationContext(), "无打印数据，请先查询数据", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    public void print(){
        if(TextUtils.isEmpty(printStr)){
            mToastHandler.sendEmptyMessage(3);
            return;
        }
        mToastHandler.sendEmptyMessage(1);
        int pageSizePrint = 300;// 分批
        int printStrLen = printStr.length();
        int pageCount = printStrLen / pageSizePrint;
        if (printStrLen % pageSizePrint != 0) {
            pageCount += 1;
            if (printStrLen < pageSizePrint) {
                pageSizePrint = printStr.length();
            }
        }

        for (int page = 0; page < pageCount; page++) {
            int start = pageSizePrint * page;
            int end = pageSizePrint * (page + 1);
            String onePrintStr = "";
            if(end > printStrLen){
                onePrintStr = printStr.substring(start);
            } else {
                onePrintStr = printStr.substring(start, end);
            }

            blueOutHandler.postDelayed(new BlueOutRunnable(onePrintStr, page, pageCount), 1000 * page);
        }
    }

    /**
     * 分页打印类
     */
    class BlueOutRunnable implements Runnable{
        public String printStr;
        private int page;
        private int pageCount;

        public BlueOutRunnable(String printStr, int page, int pageCount){
            this.printStr = printStr;
            this.page = page;
            this.pageCount = pageCount;
        }

        @Override
        public void run() {
            Log.d(TAG, "pageCount="+pageCount+", page = "+page+", printStr="+printStr);

            if ("encrypt".equals(EncryStrUtils.printType)) {
                // 加密后打印
                byte[] out = EncryStrUtils.encryptStr(printStr);
                blueToothService.write(out);
            } else {
                blueToothService.bluePrint(printStr);
            }
            if(page + 1 == pageCount){
                mToastHandler.sendEmptyMessage(2);

            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy");
        super.onDestroy();
        if(blueToothService != null){
            blueToothService.stop();
        }
    }

    private Handler handlerToast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Object toastMsg = msg.obj;
            if(toastMsg != null){
                Toast.makeText(QueryDataAllActivity.this, toastMsg.toString(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

}
