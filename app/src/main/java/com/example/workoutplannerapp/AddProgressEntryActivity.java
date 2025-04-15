package com.example.workoutplannerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddProgressEntryActivity extends AppCompatActivity {

    private EditText dateEditText, weightEditText;
    private ImageView previewImageView;
    private Button pickImageButton, saveButton;
    private Uri selectedImageUri;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_progress_entry);

        dateEditText = findViewById(R.id.dateEditText);
        weightEditText = findViewById(R.id.weightEditText);
        previewImageView = findViewById(R.id.previewImageView);
        pickImageButton = findViewById(R.id.pickImageButton);
        saveButton = findViewById(R.id.saveButton);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        dateEditText.setOnClickListener(v -> showDatePicker());

        pickImageButton.setOnClickListener(v -> openGallery());

        saveButton.setOnClickListener(v -> saveProgressEntry());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
            dateEditText.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        previewImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    private void saveProgressEntry() {
        String date = dateEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();

        if (date.isEmpty() || weight.isEmpty() || selectedImageUri == null || currentUser == null) {
            Toast.makeText(this, "All fields and image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference imageRef = storage.getReference().child("progressImages/" + currentUser.getUid() + "/" + System.currentTimeMillis());
        imageRef.putFile(selectedImageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    Map<String, Object> progressData = new HashMap<>();
                    progressData.put("date", date);
                    progressData.put("weight", weight);
                    progressData.put("imageUrl", uri.toString());

                    db.collection("users")
                            .document(currentUser.getUid())
                            .collection("progress")
                            .add(progressData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Progress saved", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to save progress", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }
}