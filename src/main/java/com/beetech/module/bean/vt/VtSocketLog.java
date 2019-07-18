package com.beetech.module.bean.vt;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * VT 网关通信日志
 */
@Entity
public class VtSocketLog {

    @Id(autoincrement = true)
    private Long _id;
    private String text;
    private int type; // 0 in, 1 out
    private Date inputTime;
    private Long dataId;
    private String threadName;

    public VtSocketLog(){}
    public VtSocketLog(String text, int type, Long dataId, String threadName){
        this.text = text;
        this.type = type;
        this.dataId = dataId;
        this.inputTime = new Date();
        this.threadName = threadName;
    }
    @Generated(hash = 1370399479)
    public VtSocketLog(Long _id, String text, int type, Date inputTime, Long dataId,
            String threadName) {
        this._id = _id;
        this.text = text;
        this.type = type;
        this.inputTime = inputTime;
        this.dataId = dataId;
        this.threadName = threadName;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getInputTime() {
        return inputTime;
    }

    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
