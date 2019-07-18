package com.beetech.module.bean.vt;

/**
 * {"code":200,"v":"1.0","success":true,"id":2643,"cmd":"SHTRF"}
 * {"code":200,"v":"1.0","success":true,"id":120,"cmd":"STATE"}
 */
public class VtResponseBean {
    private Long id;
    private String cmd;
    private String v;
    private String code;
    private boolean success;

    public VtResponseBean(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
