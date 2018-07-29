package com.libnanman.liftingplanner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Exercise {

    private String name;
    private int weight;
    private int reps;
    private int sets;
    private boolean percentMax;
    private boolean complete;
    private String date;
    private String uid;

//    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");

    public Exercise() {}

    public Exercise(String name, int weight, int reps, int sets, boolean percentMax, boolean complete, String date, String uid) {
        this.name = name;
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
        this.percentMax = percentMax;
        this.complete = complete;
        this.date = date;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public boolean isPercentMax() {
        return percentMax;
    }

    public void setPercentMax(boolean percentMax) {
        this.percentMax = percentMax;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getDate() { return date; }

    public void setDate(String date){ this.date = date; }

    public String getUid() { return uid; }

    public void setUid(String uid){ this.uid = uid; }
}
