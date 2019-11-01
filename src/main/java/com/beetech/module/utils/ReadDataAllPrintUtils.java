package com.beetech.module.utils;

import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.constant.Constant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import java.text.DecimalFormat;

public class ReadDataAllPrintUtils {

    public static String toPrintStr(List<ReadDataRealtime> readDataRealtimeList, List<List<ReadDataResponse>> dataListAll, PrintSetVo printSetVo, QueryConfigRealtime queryConfigRealtime) {
        DecimalFormat tempFormat = new DecimalFormat("0.0");// 保留一位小数整数补.0

        String space = " ";
        int colSize = printSetVo.getColSize();
        boolean isPrintStats = printSetVo.isPrintStats();
        Double tempDataMax = null;
        Double tempDataMin = null;
        if(dataListAll == null || dataListAll.isEmpty()){
            return null;
        }
        List<String> timeStrList = new LinkedList<>();
        Map<String, ReadDataResponse> timeReadDataResponseMap = new TreeMap<>();
        Map<String, Double> sensorId_avgTempMap = new HashMap<>();
        for (int i = 0; i< dataListAll.size();i++) {
            List<ReadDataResponse> dataList = dataListAll.get(i);
            int size = dataList.size();
            double tempSum = 0.0;
            String sensorId = "";
            for (ReadDataResponse readDataResponse : dataList) {
                sensorId = readDataResponse.getSensorId();
                Date sensorDataTime = readDataResponse.getSensorDataTime();
                double temp = readDataResponse.getTemp();
                tempSum += temp;
                String timeInMin = DateUtils.parseDateToString(sensorDataTime, DateUtils.C_YYYY_MM_DD_HH_MM);
                timeReadDataResponseMap.put(sensorId+timeInMin, readDataResponse);
                if(!timeStrList.contains(timeInMin)){
                    timeStrList.add(timeInMin);
                }
            }
            if(size > 0){
                try {
                    Double tempAvgDouble = tempSum / size;
                    double tempAvg = Double.valueOf(tempFormat.format(tempAvgDouble));
                    sensorId_avgTempMap.put(sensorId, tempAvg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        List<ReadDataResponse> firstDataList = dataListAll.get(0);
        ReadDataResponse firstTempDataVo = firstDataList.get(0);
        ReadDataResponse lastTempDataVo = firstDataList.get(firstDataList.size()-1);

        List<String> lineList = new ArrayList<>();

        Set<String> dateStrSet = new HashSet<>();
        int col = 1;
        StringBuffer lineStringBuffer = new StringBuffer();

        Map<String, ReadDataResponse> lastReadDataResponseMap = new HashMap<>();
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
                    titleTStr += space + " T" + (i+1);
                }
                titleStr += titleTStr;

                for (int i = 0; i < colSize - 1; i++) {
                    titleStr += space+space+space + titleStr;
                }
                lineList.add(titleStr);
                dateStrSet.add(dateStr);
            }

            lineStringBuffer.append(timeStr.substring(11));

            for (ReadDataRealtime readDataRealtime : readDataRealtimeList){
                String sensorId = readDataRealtime.getSensorId();
                ReadDataResponse readDataResponse = timeReadDataResponseMap.get(sensorId + timeStr);
                if(readDataResponse == null){
                    ReadDataResponse lastReadDataResponse = lastReadDataResponseMap.get(sensorId);
                    if(lastReadDataResponse != null){
                        double temp = lastReadDataResponse.getTemp();
                        lineStringBuffer.append(space).append(temp);

                        if(tempDataMax == null || tempDataMax < temp){
                            tempDataMax = temp;
                        }
                        if(tempDataMin == null || tempDataMin > temp){
                            tempDataMin = temp;
                        }

                    } else {
                        Double temp = sensorId_avgTempMap.get(sensorId);
                        if(temp != null){
                            lineStringBuffer.append(space).append(temp);

                            if(tempDataMax == null || tempDataMax < temp){
                                tempDataMax = temp;
                            }
                            if(tempDataMin == null || tempDataMin > temp){
                                tempDataMin = temp;
                            }
                        } else {
                            lineStringBuffer.append(space).append(" - ");
                        }
                    }
                } else {
                    double temp = readDataResponse.getTemp();
                    lineStringBuffer.append(space).append(temp);
                    lastReadDataResponseMap.put(sensorId, readDataResponse);

                    if(tempDataMax == null || tempDataMax < temp){
                        tempDataMax = temp;
                    }
                    if(tempDataMin == null || tempDataMin > temp){
                        tempDataMin = temp;
                    }
                }
            }

            if (col == colSize) {
                String line = lineStringBuffer.toString();
                if (!"".equals(line)) {
                    lineList.add(line);
                }
                lineStringBuffer = new StringBuffer();
                col = 1;
            } else {
                lineStringBuffer.append(" ");
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
        String plateNumber = printSetVo.getPlateNumber();
        sb.append("车牌号:").append(plateNumber).append("\n");
        if(isPrintStats) {
            sb.append("最高:").append(tempDataMax).append("℃\n");
            sb.append("最低:").append(tempDataMin).append("℃\n");
        }
        sb.append("IMEI:").append(Constant.imei).append("\n");
        sb.append("-------------------------------\n");

        if (lineList != null && !lineList.isEmpty()) {
            for (String dataLine : lineList) {
                sb.append(dataLine).append("\n");
            }
        }

        String beginTimeStr = DateUtils.parseDateToString(firstTempDataVo.getSensorDataTime(), DateUtils.C_YYYY_MM_DD_HH_MM);
        String endTimeStr = DateUtils.parseDateToString(lastTempDataVo.getSensorDataTime(), DateUtils.C_YYYY_MM_DD_HH_MM);
        sb.append("-------------------------------\n");
        sb.append("有效开始时间：").append(beginTimeStr).append("\n");
        sb.append("有效结束时间：").append(endTimeStr).append("\n");
        sb.append("\n\n\n\n");
        //=========================
        return sb.toString();
    }

    public static String toPrintStrOver(List<ReadDataRealtime> readDataRealtimeList, List<List<ReadDataResponse>> dataListAll, PrintSetVo printSetVo, QueryConfigRealtime queryConfigRealtime) {
        DecimalFormat tempFormat = new DecimalFormat("0.0");// 保留一位小数整数补.0
        String space = " ";
        int colSize = 1;
        int printTimeInterval = printSetVo.getPrintTimeInterval();
        boolean isPrintStats = printSetVo.isPrintStats();
        Double tempDataMax = null;
        Double tempDataMin = null;

        if(dataListAll == null || dataListAll.isEmpty()){
            return null;
        }
        List<String> timeStrList = new LinkedList<>();
        Map<String, ReadDataResponse> timeReadDataResponseMap = new TreeMap<>();
        Map<String, Double> sensorId_avgTempMap = new HashMap<>();
        for (int i = 0; i< dataListAll.size();i++) {
            List<ReadDataResponse> dataList = dataListAll.get(i);
            int size = dataList.size();
            double tempSum = 0.0;
            String sensorId = "";
            for (ReadDataResponse readDataResponse : dataList) {
                sensorId = readDataResponse.getSensorId();
                Date sensorDataTime = readDataResponse.getSensorDataTime();
                double temp = readDataResponse.getTemp();
                tempSum += temp;
                String timeInMin = DateUtils.parseDateToString(sensorDataTime, DateUtils.C_YYYY_MM_DD_HH_MM);
                timeReadDataResponseMap.put(sensorId+timeInMin, readDataResponse);
                if(!timeStrList.contains(timeInMin)){
                    timeStrList.add(timeInMin);
                }
            }
            if(size > 0){
                try {
                    Double tempAvgDouble = tempSum / size;
                    double tempAvg = Double.valueOf(tempFormat.format(tempAvgDouble));
                    sensorId_avgTempMap.put(sensorId, tempAvg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        List<ReadDataResponse> firstDataList = dataListAll.get(0);
        ReadDataResponse firstTempDataVo = firstDataList.get(0);
        ReadDataResponse lastTempDataVo = firstDataList.get(firstDataList.size()-1);

        List<String> lineList = new ArrayList<>();

        Set<String> dateStrSet = new HashSet<>();
        int col = 1;
        StringBuffer lineStringBuffer = new StringBuffer();


        boolean isOver = false; //是否超温数据
        boolean isIntervalDiv = false; //是否能被存储间隔整除
        for (String timeStr : timeStrList) {
            isOver = false;
            isIntervalDiv = false;
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
                    titleTStr += space + " T" + (i+1);
                }
                titleStr += titleTStr;

                for (int i = 0; i < colSize - 1; i++) {
                    titleStr += space+space+space + titleStr;
                }
                lineList.add(titleStr);
                dateStrSet.add(dateStr);
            }

            lineStringBuffer.append(timeStr.substring(11));

            for (ReadDataRealtime readDataRealtime : readDataRealtimeList){
                String sensorId = readDataRealtime.getSensorId();
                Double tempLower = readDataRealtime.getTempLower();
                Double tempHight = readDataRealtime.getTempHight();

                ReadDataResponse readDataResponse = timeReadDataResponseMap.get(sensorId + timeStr);

                if(readDataResponse == null){
                    lineStringBuffer.append(space).append(" --- ");
                } else {

                    Double temp = readDataResponse.getTemp();
                    Date sensorDataTime = readDataResponse.getSensorDataTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sensorDataTime);
                    int minute = cal.get(Calendar.MINUTE);
                    isIntervalDiv = minute % printTimeInterval  == 0;
                    if(tempLower != 0 && tempHight != 0 && (temp > tempHight || temp < tempLower)){
                        isOver = true;
                    }
                    lineStringBuffer.append(space).append(temp);
                    if(tempDataMax == null || tempDataMax < temp){
                        tempDataMax = temp;
                    }
                    if(tempDataMin == null || tempDataMin > temp){
                        tempDataMin = temp;
                    }
                }
            }
//            lineStringBuffer.append(space).append(isIntervalDiv).append(space).append(isOver);

            if (col == colSize) {
                String line = lineStringBuffer.toString();
                if (!"".equals(line)) {
                    lineList.add(line);
                }
                lineStringBuffer = new StringBuffer();
                col = 1;
            } else {
                lineStringBuffer.append(" ");
                col++;
            }
        }

        String line = lineStringBuffer.toString();
        if (!"".equals(line)) {// 不是间隔数据且不是超温数据不打印
            lineList.add(line);
        }

        //=========================
        StringBuffer sb = new StringBuffer();
        sb.append("冷链记录确认单\n");
        sb.append("-------------------------------\n");
        String plateNumber = printSetVo.getPlateNumber();
        sb.append("车牌号:").append(plateNumber).append("\n");
        if(isPrintStats) {
            sb.append("最高:").append(tempDataMax).append("℃\n");
            sb.append("最低:").append(tempDataMin).append("℃\n");
        }
        sb.append("IMEI:").append(Constant.imei).append("\n");
        sb.append("-------------------------------\n");

        if (lineList != null && !lineList.isEmpty()) {
            for (String dataLine : lineList) {
                sb.append(dataLine).append("\n");
            }
        }

        String beginTimeStr = DateUtils.parseDateToString(firstTempDataVo.getSensorDataTime(), DateUtils.C_YYYY_MM_DD_HH_MM);
        String endTimeStr = DateUtils.parseDateToString(lastTempDataVo.getSensorDataTime(), DateUtils.C_YYYY_MM_DD_HH_MM);
        sb.append("-------------------------------\n");
        sb.append("有效开始时间：").append(beginTimeStr).append("\n");
        sb.append("有效结束时间：").append(endTimeStr).append("\n");
        sb.append("\n\n\n\n");
        //=========================
        return sb.toString();
    }

    public static List<ReadDataResponse> filterDataList(List<ReadDataResponse> dataList, int timeInterval){
        List<ReadDataResponse> retList = new LinkedList<>();
        if(timeInterval <= 0){
            retList = dataList;

        } else {
            int dataListSize = dataList.size();
            for (int i = 0; i< dataListSize; i++){
                ReadDataResponse readDataResponse = dataList.get(i);
                Date sensorDataTime = readDataResponse.getSensorDataTime();
                Calendar cal = Calendar.getInstance();
                cal.setTime(sensorDataTime);

                int min = cal.get(Calendar.MINUTE);
                if(min % timeInterval == 0){
                    retList.add(readDataResponse);
                }
            }
        }

        return retList;
    }

    public static void main(String[] args) {
//        List<List<ReadDataResponse>> dataListAll = new ArrayList<>();
//        List<ReadDataResponse> dataList = new ArrayList<>();
//        ReadDataResponse data1 = new ReadDataResponse();
//        data1.setTemp(25.6);
//        data1.setRh(39.6);
//        data1.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:05:00", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
//        dataList.add(data1);
//
//        List<ReadDataResponse> dataList1 = new ArrayList<>();
//        ReadDataResponse data2 = new ReadDataResponse();
//        data2.setTemp(15.6);
//        data2.setRh(37.6);
//        data2.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:05:00", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
//        dataList1.add(data2);
//
//        List<ReadDataResponse> dataList2 = new ArrayList<>();
//        ReadDataResponse data3 = new ReadDataResponse();
//        data3.setTemp(21.6);
//        data3.setRh(38.6);
//        data3.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:05:00", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
//        dataList2.add(data3);
//
//        dataListAll.add(dataList);
//        dataListAll.add(dataList1);
//        dataListAll.add(dataList2);
//
//        PrintSetVo printSetVo = new PrintSetVo();
//        printSetVo.setRhFlag(0);
//        QueryConfigRealtime queryConfigRealtime = new QueryConfigRealtime();
//        queryConfigRealtime.setDevNum("19030001");
//        queryConfigRealtime.setDevName("公司大节点测试");
//        String printStr = toPrintStr(dataListAll, printSetVo, queryConfigRealtime);
//        System.out.println(printStr);
    }

}
