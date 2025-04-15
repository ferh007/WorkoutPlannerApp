package com.example.workoutplannerapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText, currentWeightEditText, targetWeightEditText;
    private Button saveButton, selectPhotoButton;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.nameEditText);
        currentWeightEditText = findViewById(R.id.currentWeightEditText);
        targetWeightEditText = findViewById(R.id.targetWeightEditText);
        saveButton = findViewById(R.id.saveButton);
        selectPhotoButton = findViewById(R.id.selectPhotoButton);
        profileImageView = findViewById(R.id.profileImageView);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing data if available
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        nameEditText.setText(document.getString("name"));
                        currentWeightEditText.setText(document.getString("currentWeight"));
                        targetWeightEditText.setText(document.getString("targetWeight"));
                    }
                });

        selectPhotoButton.setOnClickListener(v -> openGallery());

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String userCurrentWeight = currentWeightEditText.getText().toString().trim();
            String userTargetWeight = targetWeightEditText.getText().toString().trim();

            Map<String, Object> profileData = new HashMap<>();
            if (!name.isEmpty()) profileData.put("name", name);
            if (!userCurrentWeight.isEmpty()) profileData.put("currentWeight", userCurrentWeight);
            if (!userTargetWeight.isEmpty()) profileData.put("targetWeight", userTargetWeight);

            if (selectedImageUri != null) {
                StorageReference ref = storage.getReference().child("profileImages/" + currentUser.getUid());
                ref.putFile(selectedImageUri)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) throw task.getException();
                            return ref.getDownloadUrl();
                        })
                        .addOnSuccessListener(uri -> {
                            profileData.put("profileImageUrl", uri.toString());
                            saveProfile(profileData);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            Log.e("UPLOAD_FAIL", "Upload error: ", e);
                        });
            } else {
                saveProfile(profileData);
            }
        });
    }

    private void saveProfile(Map<String, Object> data) {
        db.collection("users").document(currentUser.getUid())
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, UserProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
                    Log.e("SAVE_FAIL", "Firestore save error: ", e);
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            profileImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
