package com.example.workoutplannerapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WgerApi {
    @GET("api/v2/exerciseinfo/?language=2&limit=20")
    Call<ExerciseResponse> getExercises();
}
