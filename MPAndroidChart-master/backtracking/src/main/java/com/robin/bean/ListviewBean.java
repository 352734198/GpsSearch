package com.robin.bean;

/**
 * Created by Robin on 2016/6/11.
 */
public class ListviewBean {
    private String startTime;
    private String endTime;
    private int dbvalue;
    private String distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getDbvalue() {
        return dbvalue;
    }

    public void setDbvalue(int dbvalue) {
        this.dbvalue = dbvalue;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {

        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
