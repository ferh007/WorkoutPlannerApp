package com.example.workoutplannerapp;

import com.google.firebase.firestore.DocumentId;

public class ProgressEntry {
    @DocumentId
    private String id;

    private String imageUrl;
    private String date;
    private String weight;

    public ProgressEntry() {
        // Required for Firebase
    }

    public ProgressEntry(String imageUrl, String date, String weight) {
        this.imageUrl = imageUrl;
        this.date = date;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getWeight() {
        return weight;
    }
}
