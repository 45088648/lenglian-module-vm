package com.beetech.module.bean.vt;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.constant.Constant;

public class SysRequestBean extends VtRequestBean{
    private SysRequestBody body;

    public SysRequestBean() {
        setCmd("SYS");
    }
    public SysRequestBean(QueryConfigRealtime queryConfigRealtime, MyApplication myApp) {
        this();
        body = new SysRequestBody();
        body.setImei(queryConfigRealtime.getImei());
        body.setNum(queryConfigRealtime.getDevNum());
        body.setIccid(Constant.iccid);
        body.setTime(Constant.dateFormat.format(queryConfigRealtime.getBeginMonitorTime()));
        body.setType(1);
        body.setVar(Constant.verName);
    }

    public SysRequestBody getBody() {
        return body;
    }

    public void setBody(SysRequestBody body) {
        this.body = body;
    }
}
