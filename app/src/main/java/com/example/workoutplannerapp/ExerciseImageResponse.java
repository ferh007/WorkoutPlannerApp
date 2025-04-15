package com.example.workoutplannerapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseImageResponse {
    @SerializedName("results")
    private List<ExerciseImage> results;

    public List<ExerciseImage> getResults() {
        return results;
    }
}
