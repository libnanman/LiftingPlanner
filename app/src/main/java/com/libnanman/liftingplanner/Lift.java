package com.libnanman.liftingplanner;

import java.util.Date;

public class Lift {

    private String name;
    private int max;
    private Date date;
    private String uid;

    public Lift() {}

    public Lift(String name, int max, Date date, String uid) {
        this.name = name;
        this.max = max;
        this.date = date;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public String getUid() { return uid; }

    public void setUid(String uid){ this.uid = uid; }

}
