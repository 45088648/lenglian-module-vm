package com.beetech.module.gps;

import java.util.Date;

public class GpsContent{

    double altitude;
    double lng;

    int state;
    int angle;

    boolean valid;
    double lat;
    float speed;
    int usedCnt;
    int viewCnt;
    long dateTime;
    Date gpsTime;

    public Date getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            if (angle < 0 || angle > 359){
                angle = 0;
            }
            this.angle = angle;
        }

        public double getAltitude() {
            return altitude;
        }

        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public int getUsedCnt() {
            return usedCnt;
        }

        public void setUsedCnt(int usedCnt) {
            this.usedCnt = usedCnt;
        }

        public int getViewCnt() {
            return viewCnt;
        }

        public void setViewCnt(int viewCnt) {
            this.viewCnt = viewCnt;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public long getDateTime() {
            return dateTime;
        }

        public void setDateTime(long dateTime) {
            this.dateTime = dateTime;
        }
    }