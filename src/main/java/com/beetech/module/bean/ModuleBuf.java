package com.beetech.module.bean;

import com.beetech.module.utils.ByteUtilities;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "inputTime")
})
public class ModuleBuf {

    @Id(autoincrement = true)
    private Long _id;
    private byte[] buf;
    private String bufHex;
    private int type; // 0: 请求; 1: 响应
    private Date inputTime;
    private int cmd;
    private boolean result;

    public ModuleBuf(){}

    public ModuleBuf(byte[] buf, int type, int cmd, boolean result){
        this.buf = buf;
        this.bufHex = ByteUtilities.asHex(buf).toUpperCase();
        this.type = type;
        this.inputTime = new Date();
        this.cmd = cmd;
        this.result = result;
    }

    @Generated(hash = 857339249)
    public ModuleBuf(Long _id, byte[] buf, String bufHex, int type, Date inputTime,
            int cmd, boolean result) {
        this._id = _id;
        this.buf = buf;
        this.bufHex = bufHex;
        this.type = type;
        this.inputTime = inputTime;
        this.cmd = cmd;
        this.result = result;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public String getBufHex() {
        return bufHex;
    }

    public void setBufHex(String bufHex) {
        this.bufHex = bufHex;
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

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return this.result;
    }
}
