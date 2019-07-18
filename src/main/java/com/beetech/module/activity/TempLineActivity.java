package com.beetech.module.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.beetech.module.R;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.dao.ReadDataSDDao;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 动态加载数据的折线图
 */
public class TempLineActivity extends Activity {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-HH-MM dd HH:mm");

    private static final String TAG = TempLineActivity.class.getSimpleName();
    private LineChartView chart;
 
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private Axis axisX;
    private Axis axisY;
    LineChartValueFormatter chartValueFormatter = new SimpleLineChartValueFormatter(1); // 定义格式，小数点等等信息

    private ProgressDialog progressDialog;
    private boolean isBiss;
    private String sensorId;
    private ReadDataSDDao readDataSDDao;
    private List<ReadDataResponse> readDataResponseList;
    Double tempMax= null;
    Double tempMin= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_temp_line);

        chart = (LineChartView) findViewById(R.id.chart);
        readDataSDDao = new ReadDataSDDao(this);
        sensorId = getIntent().getStringExtra("sensorId");

        readDataResponseList = readDataSDDao.query(sensorId, 20, 0);

        if(readDataResponseList == null || readDataResponseList.isEmpty()){
            Toast.makeText(this, TAG+", 无数据", Toast.LENGTH_SHORT).show();
            return;
        }

        drawLine();
    }

    public void queryData(){
        Date sensorDataTimeFirst = readDataResponseList.get(readDataResponseList.size()-1).getSensorDataTime();
        readDataResponseList = readDataSDDao.query(sensorId, sensorDataTimeFirst, 20, 0);
    }

    private void addDataPoint() {
        int count = readDataResponseList.size();
        LineChartData data = chart.getLineChartData();
        Line line = data.getLines().get(0);
        List<PointValue> values = line.getValues();
        List<AxisValue> mAxisXValues = axisX.getValues();
        int startIndex = values.size();
 
        for (int i = 0; i < count; i++) {
            int newIndex = -startIndex - i;
            ReadDataResponse readDataResponse = readDataResponseList.get(i);
            double temp = readDataResponse.getTemp();
            Date time=readDataResponse.getSensorDataTime();
            if(tempMax == null || tempMax < temp){
                tempMax = temp;
            }
            if(tempMin == null || tempMin > temp){
                tempMin = temp;
            }

            AxisValue axisValue=new AxisValue(newIndex);
            axisValue.setLabel(dateFormat.format(time));
            mAxisXValues.add(axisValue);

            PointValue pointValue = new PointValue(newIndex, Double.valueOf(temp).floatValue());
            pointValue.setLabel(temp+"");
            values.add(pointValue);
        }
        line.setFormatter(chartValueFormatter);
        line.setValues(values);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData lineData = new LineChartData(lines);
        lineData.setAxisXBottom(axisX);
        lineData.setAxisYLeft(axisY);
        lineData.setValueLabelBackgroundColor(ChartUtils.COLORS[0]);
        lineData.setValueLabelBackgroundEnabled(true);
        chart.setLineChartData(lineData);

        //根据点的横坐标实时变换X坐标轴的视图范围
        Viewport v = new Viewport(chart.getMaximumViewport());
        v.top = tempMax != null ? tempMax.intValue() + 1 : 75;
        v.bottom = tempMin != null ? tempMin.intValue() - 1 : -25;
        chart.setMaximumViewport(v);

        v.left = -startIndex+1;//X轴左边界，变化
        v.right = -startIndex+6;//X轴右边界，变化
        chart.setCurrentViewport(v);

        final float firstXValue = values.get(values.size()-1).getX();
 
        chart.setViewportChangeListener(new ViewportChangeListener() {
            @Override
            public void onViewportChanged(Viewport viewport) {
            if (!isBiss && viewport.left == firstXValue) {
                isBiss = true;
                loadData();
            }
            }
        });
    }

    private void loadData() {
        if(progressDialog == null){
            try {
                progressDialog = ProgressDialog.show(TempLineActivity.this, "系统提示", "加载中，请稍后...");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(progressDialog != null && !progressDialog.isShowing()){
            progressDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
            queryData();
            if(readDataResponseList == null || readDataResponseList.isEmpty()){
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                try {
                    addDataPoint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isBiss = false;
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                }
            });
            }
        }).start();
    }

    private void drawLine() {
        int numberOfPoints = readDataResponseList.size();
        List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
        List<Line> lines = new ArrayList<Line>();
        int numberOfLines = 1;
        List<PointValue> values = null;
        Line line = new Line();
        LineChartData data = new LineChartData(lines);
        for (int i = 0; i < numberOfLines; ++i) {
 
            values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; j++) {
                int newIndex = j * -1;
                ReadDataResponse readDataResponse= readDataResponseList.get(j);
                double temp = readDataResponse.getTemp();
                Date time = readDataResponse.getSensorDataTime();

                AxisValue axisValue=new AxisValue(newIndex);
                axisValue.setLabel(dateFormat.format(time));
                mAxisXValues.add(axisValue);

                if(tempMax == null || tempMax < temp){
                    tempMax = temp;
                }
                if(tempMin == null || tempMin > temp){
                    tempMin = temp;
                }

                // 布点
                PointValue pointValue = new PointValue(newIndex, Double.valueOf(temp).floatValue() );
                pointValue.setLabel(temp+""); // 这个点的标注信息，String的随便来
                line.setFormatter(chartValueFormatter); // 温度显示小数

                values.add(pointValue);
            }

            line.setPointRadius(2); // 点的大小
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(ValueShape.CIRCLE);
            line.setCubic(true);//曲线是否平滑，即是曲线还是折线
            line.setFilled(false);//是否填充曲线的面积
            line.setHasLabels(true);//曲线的数据坐标是否加上备注
//            line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
            line.setStrokeWidth(1); // 线的粗细
            line.setValues(values);
            lines.add(line);
        }


        data.setLines(lines); // 线的集合放在chart数据中，
        if (hasAxes) {
            axisX = new Axis().setHasLines(false);
            axisY = new Axis().setHasLines(false);
            if (hasAxesNames) {
                axisX.setValues(mAxisXValues).setName(sensorId);
//                axisY.setName("温度℃");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
 
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
 
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        data.setAxisXBottom(axisX); // x 轴在底部
        data.setAxisYLeft(axisY); // y 轴在左，也可以右边
        data.setValueLabelBackgroundColor(ChartUtils.COLORS[0]); // 设置折线上点的背景颜色，默认是有一个小方块，而且背景色和点的颜色一样, 如果想要原来的效果可以不用这两句话，我的显示的是透明的
        data.setValueLabelBackgroundEnabled(true);
        chart.setLineChartData(data);
 
        Viewport v = new Viewport(chart.getMaximumViewport());
        v.top = tempMax != null ? tempMax.intValue() + 1 : 75;
        v.bottom = tempMin != null ? tempMin.intValue() - 1 : -25;
        chart.setMaximumViewport(v);

        v.left = -5;
        v.right = 0;
        chart.setCurrentViewport(v);

        final float firstXValue = values.get(values.size() - 1).getX();
        chart.setViewportChangeListener(new ViewportChangeListener() {
            @Override
            public void onViewportChanged(Viewport viewport) {
            if (!isBiss && viewport.left == firstXValue) {
                isBiss = true;
                loadData();
            }
            }
        });
    }


}
