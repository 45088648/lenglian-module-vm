package com.beetech.module.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import java.util.Calendar;
import android.app.AlertDialog;
import com.beetech.module.R;
import com.beetech.module.widget.time.WheelMain;
import com.beetech.module.widget.time.ScreenInfo;
import com.beetech.module.widget.time.JudgeDate;

public class OnDateEditClickListener implements View.OnClickListener {
        private EditText dateEditText;
        private WheelMain wheelMain;
        private Activity activity;
        public OnDateEditClickListener(Activity activity){
            this.activity = activity;
        }

        @Override
        public void onClick(View view) {

            InputTools.HideKeyboard(view); // 隐藏虚拟键盘
            dateEditText = (EditText) view;

            LayoutInflater inflater = LayoutInflater.from(activity);
            final View timepickerview = inflater.inflate(R.layout.timepicker, null);
            ScreenInfo screenInfo = new ScreenInfo(activity);

            wheelMain = new WheelMain(timepickerview, true);
            wheelMain.screenheight = screenInfo.getHeight();
            String time = dateEditText.getText().toString();
            Calendar calendar = Calendar.getInstance();
            if (JudgeDate.isDate(time, DateUtils.C_YYYY_MM_DD_HH_MM)) {
                calendar.setTime(DateUtils.parseStringToDate(time, DateUtils.C_YYYY_MM_DD_HH_MM));
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            wheelMain.initDateTimePicker(year, month, day, hour, min);

            new AlertDialog.Builder(activity).setTitle("选择时间").setView(timepickerview).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dateEditText.setText(wheelMain.getTime());
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }
    }