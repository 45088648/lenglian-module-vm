package com.beetech.module.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AppLog {
    @Id(autoincrement = true)
    private Long _id;
    private Date inputTime;
    private String content;

    public AppLog(){}

    public AppLog(String content){
        this.content = content;
        inputTime = new Date();
    }

    @Generated(hash = 1847713552)
    public AppLog(Long _id, Date inputTime, String content) {
        this._id = _id;
        this.inputTime = inputTime;
        this.content = content;
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
}
