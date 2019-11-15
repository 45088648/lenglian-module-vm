package com.beetech.module.activity;

import android.app.Activity;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beetech.module.R;
import com.beetech.module.adapter.PrinterListViewAdapter;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.service.BlueToothService;
import com.beetech.module.utils.DateUtils;
import com.beetech.module.utils.PrintSetVo;
import com.beetech.module.utils.ReadDataAllPrintUtils;
import com.beetech.module.view.LoadingDialog;
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

public class QueryDataAllActivity extends Activity implements CheckBox.OnCheckedChangeListener{
    private static final String TAG = QueryDataAllActivity.class.getSimpleName();

    @ViewInject(R.id.time_begin_et)
    private EditText timeBeginEt;

    @ViewInject(R.id.time_end_et)
    private EditText timeEndEt;

    @ViewInject(R.id.scanLe_btn)
    private Button scanLeBtn;

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
    private String plateNumber;

    private MyApplication myApp;

    public BlueToothService blueToothService;// 蓝牙打印服务对象
    private List<ReadDataResponse> dataList;

    private String printStr;
    private QueryConfigRealtime queryConfigRealtime;

    private String timeBeginStr;
    private String timeEndStr;

    //打印参数
    private int printTimeInterval = 5; // 打印间隔，单位：分钟
    private List sensorIdListSelect = new LinkedList();

    @ViewInject(R.id.printer_lv)
    private ListView printerLv;
    public List<String> printerList = new ArrayList<>();
    public PrinterListViewAdapter printerListViewAdapter;

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

        //设置ListView的适配器
        printerListViewAdapter = new PrinterListViewAdapter(this, printerList);
        printerLv.setAdapter(printerListViewAdapter);

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
    private LoadingDialog loading;
    @OnClick(R.id.query_btn)
    public void queryBtn_onClick(View v) {
        showLoading();
        new QueryAsyncTask().execute();
    }

    public void showLoading(){
        loading = new LoadingDialog(this,R.style.CustomDialog);
        loading.show();
        new Handler().postDelayed(new Runnable() {//定义延时任务模仿网络请求
            @Override
            public void run() {
                hideLoading();
            }
        }, 30000);
    }

    public void hideLoading(){
        if(loading != null && loading.isShowing()) {
            loading.dismiss();
        }
    }

    class QueryAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            queryBtn.setVisibility(View.GONE);

            timeBeginStr = timeBeginEt.getText().toString();
            timeEndStr = timeEndEt.getText().toString();
            plateNumber = plateNumberEt.getText().toString();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                if (TextUtils.isEmpty(timeBeginStr)) {
                    toast("请选择开始时间");
                    return 0;
                }

                if (TextUtils.isEmpty(timeEndStr)) {
                    toast("请选择结束时间");
                    return 0;
                }
                Date timeBegin = DateUtils.parseStringToDate(timeBeginStr, DateUtils.C_YYYY_MM_DD_HH_MM);
                Date timeEnd = DateUtils.parseStringToDate(timeEndStr, DateUtils.C_YYYY_MM_DD_HH_MM);
                Log.d(TAG, "timeBeginStr = " + timeBeginStr + ", timeEndStr = " + timeEndStr);
                final int crossDay = 30;
                if (timeEnd.getTime() - timeBegin.getTime() > 1000L * 60 * 60 * 24 * crossDay) {
                    toast("时间范围不能超过" + crossDay + "天");
                    return 0;
                }

                queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
                if (queryConfigRealtime == null) {
                    toast("设备本地配置信息不存在");
                    return 0;
                }
                getPrintSet();
                Log.d(TAG, "printTimeInterval=" + printTimeInterval);

                List<ReadDataRealtime> readDataRealtimeList = myApp.readDataRealtimeSDDao.queryAll();
                List<ReadDataRealtime> readDataRealtimeListSelect = new LinkedList<>();
                List<List<ReadDataResponse>> dataListAll = new LinkedList<>();
                List<List<ReadDataResponse>> filterDataListAll = new LinkedList<>();
                PrintSetVo printSetVo = new PrintSetVo();
                if (readDataRealtimeList.size() <= 2) {
                    printSetVo.setColSize(2);
                }
                printSetVo.setPrintTimeInterval(printTimeInterval);
                printSetVo.setPrintStats(isContainStats);
                printSetVo.setPlateNumber(plateNumber);

                Date beginTime = null;
                Date endTime = null;
                for (ReadDataRealtime readDataRealtime : readDataRealtimeList) {
                    String sensorId = readDataRealtime.getSensorId();
                    if(sensorIdListSelect.isEmpty() || sensorIdListSelect.contains(sensorId)){
                        readDataRealtimeListSelect.add(readDataRealtime);
                    } else {
                        continue;
                    }
                    List<ReadDataResponse> dataList = myApp.readDataSDDao.queryBySensorId(sensorId, timeBegin, timeEnd, Integer.MAX_VALUE, 0);
                    if(dataList != null && !dataList.isEmpty()){
                        int size = dataList.size();
                        Date beginTimeFirst = dataList.get(0).getSensorDataTime();
                        Date endTimeLast = dataList.get(size-1).getSensorDataTime();
                        if(beginTime == null || beginTimeFirst.getTime() < beginTime.getTime()){
                            beginTime = beginTimeFirst;
                        }
                        if(endTime == null || endTimeLast.getTime() > endTime.getTime()){
                            endTime = endTimeLast;
                        }
                        dataListAll.add(dataList);
                    }
                }

                if(dataListAll == null || dataListAll.isEmpty()){
                    toast("数据为空，请稍后再试");
                    return 0;
                }

                for (List<ReadDataResponse> dataList : dataListAll) {
                    List<ReadDataResponse> filterDataList = null;
                    if (isContainOver) {
                        filterDataList = dataList;
                    } else {
                        filterDataList = ReadDataAllPrintUtils.filterDataList(dataList, printTimeInterval, beginTime, endTime);
                    }
                    filterDataListAll.add(filterDataList);
                }

                if(isContainOver){
                    printStr = ReadDataAllPrintUtils.toPrintStrOver(readDataRealtimeListSelect, dataListAll, printSetVo, queryConfigRealtime);
                } else {
                    printStr = ReadDataAllPrintUtils.toPrintStr(readDataRealtimeListSelect, filterDataListAll, printSetVo, queryConfigRealtime);
                }

            } catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "查询异常", e);
                toast("查询异常，请稍后再试");
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            queryBtn.setVisibility(View.VISIBLE);

            hideLoading();
            if (result == 1) {
                printStrTv.setText(printStr);

                queryConditionLl.setVisibility(View.GONE);
                queryResultLl.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.scanLe_btn)
    public void scanLeBtn_onClick(View v) {
        if(TextUtils.isEmpty(printStr)){
            toast("无打印数据，请先查询数据");
            return;
        }

        toast( "打印机查找中，请确保开启蓝牙，蓝牙打印机已开机");
        blueToothService.scanDevice();
    }

    @OnClick(R.id.requery_btn)
    public void requeryBtn_onClick(View v) {
        queryConditionLl.setVisibility(View.VISIBLE);
        queryResultLl.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, " onStop");
        super.onStop();

        blueToothService.stopScanLe();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy");
        super.onDestroy();
    }

    private Toast toast = null;
    private Handler handlerToast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Object toastMsg = msg.obj;
            if(toastMsg != null){
                if(toast == null){
                    toast = Toast.makeText(QueryDataAllActivity.this, toastMsg.toString(), Toast.LENGTH_SHORT);
                }else{
                    toast.setText(toastMsg.toString());
                    toast.setDuration(Toast.LENGTH_SHORT);
                }
                toast.show();
            }
            super.handleMessage(msg);
        }
    };

    public void toast(String content){
        Message msg = new Message();
        msg.obj = content;
        handlerToast.sendMessage(msg);
    }

    public PrinterListViewAdapter getPrinterListViewAdapter() {
        return printerListViewAdapter;
    }

    public List<String> getPrinterList() {
        return printerList;
    }

    public BlueToothService getBlueToothService() {
        return blueToothService;
    }

    public String getPrintStr() {
        return printStr;
    }

}
