package com.beetech.module.utils;

import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.code.response.ReadDataResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReadDataPrintUtils {

    public static DecimalFormat tempFormat = new DecimalFormat("0.0");// 保留一位小数整数补.0
    public static SimpleDateFormat dateFromat = new SimpleDateFormat(DateUtils.C_YYYY_MM_DD_HH_MM_SS);
    public static SimpleDateFormat dateFromat_mm = new SimpleDateFormat(DateUtils.C_YYYY_MM_DD_HH_MM);

    public static String toPrintStr(List<ReadDataResponse> dataList, int colSize, PrintSetVo printSetVo, ReadDataRealtime readDataRealtime) {
        String space = " ";
        int rhFlag = printSetVo.getRhFlag();
        Double tempDataMax = null;
        Double tempDataMin = null;
        Double tempDataSum = 0.0;
        Double tempDataAvg = null;
        Double rhMax = null;
        Double rhMin = null;
        Double rhSum = 0.0;
        Double rhAvg = null;
        if(dataList == null || dataList.isEmpty()){
            return null;
        }

        ReadDataResponse firstTempDataVo = dataList.get(0);
        ReadDataResponse lastTempDataVo = dataList.get(dataList.size()-1);
        String devNum = readDataRealtime.getSensorId();
        String devName = readDataRealtime.getDevName();
        double tempLower = readDataRealtime.getTempLower();
        double tempHight = readDataRealtime.getTempHight();
        double rhLower = readDataRealtime.getRhLower();
        double rhHight = readDataRealtime.getRhHight();

        List<String> timeStrList = new ArrayList<>();
        Map<String, ReadDataResponse> time_dataMap = new HashMap<>();

        for (ReadDataResponse readData : dataList) {
            String devTempDatetimeInMinute = dateFromat_mm.format(readData.getSensorDataTime());
            time_dataMap.put(devTempDatetimeInMinute, readData);

            if(!timeStrList.contains(devTempDatetimeInMinute)){
                timeStrList.add(devTempDatetimeInMinute);
            }

            Double tempDataDouble = readData.getTemp();
            if(tempDataMax == null || tempDataMax < tempDataDouble){
                tempDataMax = tempDataDouble;
            }
            if(tempDataMin == null || tempDataMin > tempDataDouble){
                tempDataMin = tempDataDouble;
            }
            tempDataSum += tempDataDouble;

            if(rhFlag == 0){
                double rh = readData.getRh();
                Double rhDouble = Double.valueOf(rh);
                if(rhMax == null || rhMax < rhMax){
                    rhMax = rhDouble;
                }
                if(rhMin == null || rhMin > rhDouble){
                    rhMin = rhDouble;
                }
                rhSum += rhDouble;
            }
        }

        List<String> lineList = new ArrayList<>();

        Set<String> dateStrSet = new HashSet<>();
        int col = 1;
        StringBuffer lineStringBuffer = new StringBuffer();
        for (String timeStr : timeStrList) {
            ReadDataResponse readData = time_dataMap.get(timeStr);
            String devTempDataStr = tempFormat.format(readData.getTemp());
            String devTempDatetimeStr = dateFromat.format(readData.getSensorDataTime());

            String dateStr = devTempDatetimeStr.substring(0, 10);
            if (!dateStrSet.contains(dateStr)) {
                if(!dateStrSet.isEmpty()){
                    lineList.add("");
                }
                String line = lineStringBuffer.toString();
                if(!"".equals(line)){
                    lineList.add(line);
                }
                col = 1;

                lineStringBuffer = new StringBuffer();
                lineList.add("日期: "+dateStr);

                String titleStr = "时间 "+space+"温度";
                if(rhFlag == 0){
                    titleStr += space+"湿度";
                }

                for(int i = 0; i <  colSize - 1; i++){
                    titleStr += space + titleStr;
                }
                lineList.add(titleStr);
                dateStrSet.add(dateStr);
            }

            lineStringBuffer.append(timeStr.substring(11)).append(space).append(devTempDataStr);

            if(rhFlag == 0){
                String rhStr = tempFormat.format(readData.getRh());
                lineStringBuffer.append(space).append(rhStr);
            }
            lineStringBuffer.append(space);

            if(col == colSize){
                String line = lineStringBuffer.toString();
                if(!"".equals(line)){
                    lineList.add(line);
                }
                lineStringBuffer = new StringBuffer();
                col = 1;
            } else {
                col++;
            }

        }

        String line = lineStringBuffer.toString();
        if(!"".equals(line)){
            lineList.add(line);
        }

        //=========================
        StringBuffer sb = new StringBuffer();
        sb.append("冷链记录确认单\n");
        sb.append("-------------------------------\n");
        sb.append("设备号:").append(devNum).append("\n");
        if(devName != null && !"".equals(devName)){
            sb.append("标识名:").append(devName).append("\n");
        }
        if(tempLower != 0.0 && tempHight != 0.0){
            sb.append("温度阈值:").append(tempLower).append("℃~").append(tempHight).append( "℃\n");
        }
        if(rhFlag == 0 && rhLower != 0.0 && rhHight != 0.0){
            sb.append("湿度阈值:").append(rhLower).append("%~").append(rhHight).append("%\n");
        }
        int printStats = printSetVo.getPrintStats();
        if(printStats == 0){
            int size = dataList.size();
            tempDataAvg = tempDataSum/size;
            rhAvg = rhSum/size;
            sb.append("-------------------------------\n");
            sb.append("最大值").append(space).append("最小值").append(space).append("平均值\n");
            String tempDataMaxStr = tempFormat.format(tempDataMax);
            String tempDataMinStr = tempFormat.format(tempDataMin);
            String tempDataAvgStr = tempFormat.format(tempDataAvg);
            sb.append(tempDataMaxStr).append("℃").append(space).append(tempDataMinStr).append("℃").append(space).append(tempDataAvgStr).append("℃\n");

            if(rhFlag == 0 && rhMax != null){
                String rhMaxStr = tempFormat.format(rhMax);
                String rhMinStr = tempFormat.format(rhMin);
                String rhAvgStr = tempFormat.format(rhAvg);
                sb.append(rhMaxStr).append("% ").append(space).append(rhMinStr).append("% ").append(space).append(rhAvgStr).append("% \n");
            }
        }
        sb.append("-------------------------------\n");

        if(lineList != null && !lineList.isEmpty()){
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
        List<ReadDataResponse> dataList = new ArrayList<>();
        ReadDataResponse data1 = new ReadDataResponse();
        data1.setTemp(25.6);
        data1.setRh(39.6);
        data1.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:05:14", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
        dataList.add(data1);

        ReadDataResponse data2 = new ReadDataResponse();
        data2.setTemp(15.6);
        data2.setRh(37.6);
        data2.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:06:14", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
        dataList.add(data2);

        ReadDataResponse data3 = new ReadDataResponse();
        data3.setTemp(21.6);
        data3.setRh(38.6);
        data3.setSensorDataTime(DateUtils.parseStringToDate("2019-5-17 10:07:14", DateUtils.C_YYYY_MM_DD_HH_MM_SS));
        dataList.add(data3);

        int colSize = 2;
        PrintSetVo printSetVo = new PrintSetVo();
        printSetVo.setRhFlag(0);
        ReadDataRealtime readDataRealtime = new ReadDataRealtime();
        readDataRealtime.setSensorId("19030001");
        readDataRealtime.setDevName("公司大节点测试");
        String printStr = toPrintStr(dataList, 2, printSetVo, readDataRealtime);
        System.out.println(printStr);
    }

}
