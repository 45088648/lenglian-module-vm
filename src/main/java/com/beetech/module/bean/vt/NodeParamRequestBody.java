package com.beetech.module.bean.vt;

import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.constant.Constant;
import java.util.ArrayList;
import java.util.List;

public class NodeParamRequestBody {
    /**
     * IMEI /SN
     */
    private String imei;
    /**
     * Sim Card Number
     */
    private String num;

    /**
     * Sim Card iccid
     */
    private String iccid;

    private List<String> nums;
    public NodeParamRequestBody(){
        setImei(Constant.imei);
        setIccid(Constant.iccid);
        setNum(Constant.phoneNumber);
    }
    public NodeParamRequestBody(List<ReadDataRealtime> readDataRealtimeList){
        this();
        if(readDataRealtimeList == null || readDataRealtimeList.isEmpty()){
            return;
        }
        nums = new ArrayList<>();
        for (ReadDataRealtime readDataRealtime :readDataRealtimeList ) {
            nums.add(readDataRealtime.getSensorId());
        }
    }
    public List<String> getNums() {
        return nums;
    }

    public void setNums(List<String> nums) {
        this.nums = nums;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}
