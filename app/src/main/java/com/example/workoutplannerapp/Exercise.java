package com.example.workoutplannerapp;

import java.util.List;

public class Exercise {
    private int id;
    private List<Translation> translations;
    private List<ExerciseImage> images;

    public int getId() {
        return id;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public List<ExerciseImage> getImages() {
        return images;
    }

    public String getName() {
        for (Translation t : translations) {
            if (t.getLanguage() == 2) return t.getName();
        }
        return "No name";
    }

    public String getDescription() {
        for (Translation t : translations) {
            if (t.getLanguage() == 2) return t.getDescription();
        }
        return "No description available.";
    }

    public String getImageUrl() {
        return (images != null && !images.isEmpty()) ? images.get(0).getImage() : null;
    }
}
