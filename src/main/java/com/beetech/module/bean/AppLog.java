package com.beetech.module.bean;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

@Entity(indexes = {
        @Index(value = "sendFlag")
})
public class AppLog {
    @Id(autoincrement = true)
    private Long _id;
    private Date inputTime;
    private String content;
    @JSONField(serialize=false)
    private int sendFlag;

    public AppLog(){}

    public AppLog(String content){
        this.content = content;
        inputTime = new Date();
    }

    @Generated(hash = 1880252904)
    public AppLog(Long _id, Date inputTime, String content, int sendFlag) {
        this._id = _id;
        this.inputTime = inputTime;
        this.content = content;
        this.sendFlag = sendFlag;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Date getInputTime() {
        return inputTime;
    }

    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(int sendFlag) {
        this.sendFlag = sendFlag;
    }
}
