package com.beetech.module.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.location.BDLocation;
import com.beetech.module.gps.GpsContent;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

@Entity(indexes = {
        @Index(value = "dataTime"),
        @Index(value = "sendFlag")
})
public class GpsDataBean {

    @Id(autoincrement = true)
    private Long _id;
    private double lat;
    private double lng;
    private float radius;
    private float direction;
    private float speed;
    @JSONField(serialize=false)
    private double altitude;
    @JSONField(serialize=false)
    private String address;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date dataTime;
    private int locType;
    @JSONField(serialize=false)
    private int sendFlag;

    public GpsDataBean(){
        this.dataTime = new Date();
        this.sendFlag = 0;
    }

    public GpsDataBean(BDLocation location){
        this.dataTime = new Date();
        this.sendFlag = 0;

        setLat(location.getLatitude());
        setLng(location.getLongitude());
        setRadius(location.getRadius());
        setSpeed(location.getSpeed()); // 单位：公里每小时
        setDirection(location.getDirection());// 单位度
        setAltitude(location.getAltitude()); // 单位：米
        setAddress(location.getAddrStr());
        setLocType(location.getLocType());
    }

    public GpsDataBean(GpsContent gpsContent){
        this.dataTime = new Date();
        this.sendFlag = 0;

        setLat(gpsContent.getLat());
        setLng(gpsContent.getLng());
        setSpeed(gpsContent.getSpeed()); // 单位：公里每小时
        setDirection(gpsContent.getAngle());// 单位度
        setAltitude(gpsContent.getAltitude()); // 单位：米
        setLocType(0);
    }

    @Generated(hash = 1537768466)
    public GpsDataBean(Long _id, double lat, double lng, float radius, float direction, float speed, double altitude, String address,
            Date dataTime, int locType, int sendFlag) {
        this._id = _id;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.direction = direction;
        this.speed = speed;
        this.altitude = altitude;
        this.address = address;
        this.dataTime = dataTime;
        this.locType = locType;
        this.sendFlag = sendFlag;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public int getLocType() {
        return locType;
    }

    public void setLocType(int locType) {
        this.locType = locType;
    }

    public int getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(int sendFlag) {
        this.sendFlag = sendFlag;
    }
}
