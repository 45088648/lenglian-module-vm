package com.beetech.module.utils;

import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.bean.QueryConfigRealtime;;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

public class ReadDataAllPrintUtils {

    public static DecimalFormat tempFormat = new DecimalFormat("0.0");// 保留一位小数整数补.0
    public static SimpleDateFormat dateFromat = new SimpleDateFormat(DateUtils.C_YYYY_MM_DD_HH_MM_SS);
    public static SimpleDateFormat dateFromat_mm = new SimpleDateFormat(DateUtils.C_YYYY_MM_DD_HH_MM);

    public static String toPrintStr(List<List<ReadDataResponse>> dataListAll, PrintSetVo printSetVo, QueryConfigRealtime queryConfigRealtime) {
        String space = " ";
        int colSize = printSetVo.getColSize();
        int rhFlag = printSetVo.getRhFlag();
        Double tempDataMax = null;
        Double tempDataMin = null;
        Double tempDataSum = 0.0;
        Double tempDataAvg = null;
        Double rhMax = null;
        Double rhMin = null;
        Double rhSum = 0.0;
        Double rhAvg = null;
        if(dataListAll == null || dataListAll.isEmpty()){
            return null;
        }
        List<String> timeStrList = new LinkedList<>();
        List<Map<String, Double>> timeTempDataMapList = new LinkedList<>();

        for (int i = 0; i< dataListAll.size();i++) {
            List<ReadDataResponse> dataList = dataListAll.get(i);
            Map<String, Double> timeTempDataMap = new HashMap<>();
            for (ReadDataResponse readDataResponse : dataList) {
                Double temp = readDataResponse.getTemp();
                Date sensorDataTime = readDataResponse.getSensorDataTime();
                String timeInMin = DateUtils.parseDateToString(sensorDataTime, DateUtils.C_YYYY_MM_DD_HH_MM);
                timeTempDataMap.put(timeInMin, temp);
                if(!timeStrList.contains(timeInMin)){
                    timeStrList.add(timeInMin);
                }
            }
            timeTempDataMapList.add(timeTempDataMap);
        }

        List<ReadDataResponse> firstDataList = dataListAll.get(0);
        ReadDataResponse firstTempDataVo = firstDataList.get(0);
        ReadDataResponse lastTempDataVo = firstDataList.get(firstDataList.size()-1);
        String devNum = queryConfigRealtime.getDevNum();
        String devName = queryConfigRealtime.getDevName();

        List<String> lineList = new ArrayList<>();

        Set<String> dateStrSet = new HashSet<>();
        int col = 1;
        StringBuffer lineStringBuffer = new StringBuffer();
        for (String timeStr : timeStrList) {

            String dateStr = timeStr.substring(0, 10);
            if (!dateStrSet.contains(dateStr)) {
                if (!dateStrSet.isEmpty()) {
                    lineList.add("");
                }
                String line = lineStringBuffer.toString();
                if (!"".equals(line)) {
                    lineList.add(line);
                }
                col = 1;

                lineStringBuffer = new StringBuffer();
                lineList.add("日期: " + dateStr);

                String titleStr = "时间 ";
                String titleTStr = "";
                for (int i = 0; i < dataListAll.size(); i++) {
                    titleTStr += space + "T" + (i+1);
                }
                titleStr += titleTStr;

                for (int i = 0; i < colSize - 1; i++) {
                    titleStr += space + titleStr;
                }
                lineList.add(titleStr);
                dateStrSet.add(dateStr);
            }

            lineStringBuffer.append(timeStr.substring(11));
            for (Map<String, Double> timeTempDataMap : timeTempDataMapList) {
                lineStringBuffer.append(space).append(timeTempDataMap.get(timeStr));
            }

            if (col == colSize) {
                String line = lineStringBuffer.toString();
                if (!"".equals(line)) {
                    lineList.add(line);
                }
                lineStringBuffer = new StringBuffer();
                col = 1;
            } else {
                col++;
            }
        }

        String line = lineStringBuffer.toString();
        if (!"".equals(line)) {
            lineList.add(line);
        }

        //=========================
        StringBuffer sb = new StringBuffer();
        sb.append("冷链记录确认单\n");
        sb.append("-------------------------------\n");
//        sb.append("设备号:").append(devNum).append("\n");
//        if(devName != null && !"".equals(devName)){
//            sb.append("标识名:").append(devName).append("\n");
//        }
//        if(tempLower != 0.0 && tempHight != 0.0){
//            sb.append("温度阈值:").append(tempLower).append("℃~").append(tempHight).append( "℃\n");
//        }

        if (lineList != null && !lineList.isEmpty()) {
            for (String dataLine : lineList) {
                sb.append(dataLine).append("\n");
            }
        }

        String beginTimeStr = dateFromat_mm.format(firstTempDataVo.getSensorDataTime());
        String endTimeStr = dateFromat_mm.format(lastTempDataVo.getSensorDataTime());
        sb.append("-------------------------------\n");
        sb.append("有效开始时间：").append(beginTimeStr).append("\n");
        sb.append("有效结束时间：").append(endTimeStr).append("\n");
        sb.append("\n\n\n\n");
        //=========================
        return sb.toString();
    }

    public static void main(String[] args) {
        List<List<ReadDataResponse>> dataListAll = new ArrayList<>();
        List<ReadDataResponse> dataList = new ArrayList<>();
        ReadDataResponse data1 = new ReadDataResponse();
        data1.setTemp(25.6);
        data1.setRh(39.6);
        data1.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:05:00", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
        dataList.add(data1);

        List<ReadDataResponse> dataList1 = new ArrayList<>();
        ReadDataResponse data2 = new ReadDataResponse();
        data2.setTemp(15.6);
        data2.setRh(37.6);
        data2.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:05:00", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
        dataList1.add(data2);

        List<ReadDataResponse> dataList2 = new ArrayList<>();
        ReadDataResponse data3 = new ReadDataResponse();
        data3.setTemp(21.6);
        data3.setRh(38.6);
        data3.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:05:00", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
        dataList2.add(data3);

        dataListAll.add(dataList);
        dataListAll.add(dataList1);
        dataListAll.add(dataList2);

        PrintSetVo printSetVo = new PrintSetVo();
        printSetVo.setRhFlag(0);
        QueryConfigRealtime queryConfigRealtime = new QueryConfigRealtime();
        queryConfigRealtime.setDevNum("19030001");
        queryConfigRealtime.setDevName("公司大节点测试");
        String printStr = toPrintStr(dataListAll, printSetVo, queryConfigRealtime);
        System.out.println(printStr);
    }

}
