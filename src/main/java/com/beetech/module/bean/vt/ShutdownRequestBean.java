package com.beetech.module.bean.vt;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.constant.Constant;

public class ShutdownRequestBean extends VtRequestBean {
    private ShutdownRequestBody body;

    public ShutdownRequestBean() {
        setCmd("SHUTDOWN");
    }
    public ShutdownRequestBean(QueryConfigRealtime queryConfigRealtime, MyApplication myApp) {
        this();
        body = new ShutdownRequestBody();
        body.setImei(queryConfigRealtime.getImei());
        body.setTime(Constant.dateFormat.format(queryConfigRealtime.getEndMonitorTime()));
        body.setBt(myApp.batteryPercent);
    }

    public ShutdownRequestBody getBody() {
        return body;
    }

    public void setBody(ShutdownRequestBody body) {
        this.body = body;
    }
}
