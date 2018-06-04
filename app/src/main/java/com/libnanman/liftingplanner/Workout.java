package com.libnanman.liftingplanner;

public class Workout {

    private String name;
    private int weight;
    private int reps;
    private int sets;
    private boolean percentMax;
    private boolean complete;

    public Workout() {}

    public Workout(String name, int weight, int reps, int sets, boolean percentMax, boolean complete) {
        this.name = name;
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
        this.percentMax = percentMax;
        this.complete = complete;
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
}
