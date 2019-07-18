package com.beetech.module.code;

import com.alibaba.fastjson.JSON;
import com.beetech.module.bean.vt.ShtrfRequestBean;
import com.beetech.module.bean.vt.VtRequestBean;
import com.beetech.module.bean.vt.VtResponseBean;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.utils.ByteUtilities;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Test {

    //    把我得到的时间格式处理成小时
    public static int subTimeMonth(Date time){
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int MONTH = cal.get(Calendar.MONTH);

        return MONTH;
    }
    public int subTimeDay(Date time){
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int DAY_OF_MONTH = cal.get(Calendar.DAY_OF_MONTH);
        return DAY_OF_MONTH;
    }
    public static int subTimeHour(Date time){
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);
        return HOUR_OF_DAY;
    }
    /*
     *假如回来的时间是"201703151955"  2017年3月19日19:55
     * 按照我的需求只要24小时内的值
     * 在x轴上这个时间的值应该是15*24+19
     */
    public static int parseHour2Ten(Date time){
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int DAY_OF_MONTH = cal.get(Calendar.DAY_OF_MONTH);
        int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);

        return DAY_OF_MONTH*24+HOUR_OF_DAY;
    }

    public static Date parseDate(String timeStr){
        Date retDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            retDate = dateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }


    public static List<ReadDataResponse> queryDataList(){
        List<ReadDataResponse> readDataResponseList=new ArrayList<>();
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304013000"),20.8));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304023000"),3.5));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304033000"),10.6));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304043000"),-20));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304053000"),-6));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304063000"),5.5));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304073000"),-10.5));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304083000"),-8.7));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304093000"),5.8));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304103000"),20));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304113000"),17));
        readDataResponseList.add(new ReadDataResponse(parseDate("20170304123000"),25.5));
        return readDataResponseList;
    }

    public static void main(String[] args){
        int t =  parseHour2Ten(parseDate("201703151955" ));
        System.out.print(t);

        String bufHex = "ACAC3D0711180403005502A91812271037010CFC1010326102045D031E6105C46311640B94621812271035006718122710350568EB65079B6610020200000000966ACACA";
        byte[] buf = ByteUtilities.asByteArray(bufHex);
        BaseResponse baseResponse = ResponseFactory.unpack(buf);
        ReadDataResponse readDataResponse = (ReadDataResponse)baseResponse;
        ShtrfRequestBean requestBean = new ShtrfRequestBean(readDataResponse);
        String inText = JSON.toJSONString(requestBean);
        System.out.println(inText);

        String responseStr = "{\"code\":200,\"v\":\"1.0\",\"success\":true,\"id\":2643,\"cmd\":\"SHTRF\"}";
        VtResponseBean vtResponseBean = JSON.parseObject(responseStr, VtResponseBean.class);
        System.out.println(vtResponseBean.getId());
    }
}
