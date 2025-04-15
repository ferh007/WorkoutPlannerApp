package com.example.workoutplannerapp;

public class Translation {
    private int id;
    private String name;
    private String description;
    private int language;

    public int getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : "(No name)";
    }

    public String getDescription() {
        return description != null ? description : "(No description)";
    }

    public int getLanguage() {
        return language;
    }
}
