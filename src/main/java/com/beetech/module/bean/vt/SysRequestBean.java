package com.beetech.module.bean.vt;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.DateUtils;

public class SysRequestBean extends VtRequestBean{
    private SysRequestBody body;

    public SysRequestBean() {
        setCmd("SYS");
    }
    public SysRequestBean(QueryConfigRealtime queryConfigRealtime, MyApplication myApp, int type) {
        this();
        body = new SysRequestBody();
        body.setImei(queryConfigRealtime.getImei());
        body.setNum(queryConfigRealtime.getDevNum());
        body.setIccid(Constant.iccid);
        body.setTime(DateUtils.parseDateToString(queryConfigRealtime.getBeginMonitorTime(), DateUtils.C_YYYYMMDDHHMMSS));
        body.setType(type);
        body.setVar(Constant.verName);
    }

    public SysRequestBody getBody() {
        return body;
    }

    public void setBody(SysRequestBody body) {
        this.body = body;
    }
}
