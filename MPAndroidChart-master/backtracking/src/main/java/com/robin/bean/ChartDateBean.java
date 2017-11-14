package com.robin.bean;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Robin on 2016/6/5.
 */
public class ChartDateBean {
    private String time;
    private Entry entry;
    private int db;
    private int ditstance;


    public int getDitstance() {
        return ditstance;
    }

    public void setDitstance(int ditstance) {
        this.ditstance = ditstance;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }
}
