package com.beetech.module.bean.vt;

import com.beetech.module.bean.GpsDataBean;
import com.beetech.module.constant.Constant;

public class GpsDataRequestBeanBody {
    /**
     * IMEI /SN
     */
    private String imei;
    private GpsDataBean data;

    public GpsDataRequestBeanBody(GpsDataBean gpsDataBean) {
        this.imei = Constant.imei;
        this.data = gpsDataBean;
    }
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public GpsDataBean getData() {
        return data;
    }

    public void setData(GpsDataBean data) {
        this.data = data;
    }
}
