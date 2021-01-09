package com.example.healthapp.entities;

public class Workout {
    private String Activity;
    private String Duration;

    public Workout(){

    }
    public Workout(String name,String duration){
        this.Activity=name;
        this.Duration=duration;
    }

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        this.Activity = activity;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        this.Duration = duration;
    }
}
