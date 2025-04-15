package com.example.workoutplannerapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private TextView nameTextView, currentWeightTextView, targetWeightTextView;
    private Button editProfileButton, goToWorkoutButton, goToProgressButton, logoutButton;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameTextView = findViewById(R.id.nameTextView);
        currentWeightTextView = findViewById(R.id.currentWeightTextView);
        targetWeightTextView = findViewById(R.id.targetWeightTextView);
        editProfileButton = findViewById(R.id.editProfileButton);
        goToWorkoutButton = findViewById(R.id.goToWorkoutButton);
        goToProgressButton = findViewById(R.id.goToProgressButton);
        logoutButton = findViewById(R.id.logoutButton);
        profileImageView = findViewById(R.id.profileImageView);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        goToWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, WorkoutActivity.class);
                startActivity(intent);
            }
        });

        goToProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String name = document.getString("name");
                            String currentWeight = document.getString("currentWeight");
                            String targetWeight = document.getString("targetWeight");
                            String profileImageUrl = document.getString("profileImageUrl");

                            nameTextView.setText(name != null ? name : "Not Set");
                            currentWeightTextView.setText("Current Weight: " + (currentWeight != null ? currentWeight : "Not Set") + " kg");
                            targetWeightTextView.setText("Target Weight: " + (targetWeight != null ? targetWeight : "Not Set") + " kg");

                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(profileImageUrl)
                                        .into(profileImageView);
                            }
                        } else {
                            nameTextView.setText("Name: Not Set");
                            currentWeightTextView.setText("Current Weight: Not Set");
                            targetWeightTextView.setText("Target Weight: Not Set");
                        }
                    });
        }
    }
}
