package com.example.workoutplannerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ProgressActivity extends AppCompatActivity {

    private RecyclerView progressRecyclerView;
    private Button addProgressButton;
    private ProgressAdapter progressAdapter;
    private ArrayList<ProgressEntry> progressList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        progressRecyclerView = findViewById(R.id.progressRecyclerView);
        addProgressButton = findViewById(R.id.addProgressButton);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        progressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressAdapter = new ProgressAdapter(progressList);
        progressRecyclerView.setAdapter(progressAdapter);

        addProgressButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProgressActivity.this, AddProgressEntryActivity.class);
            startActivity(intent);
        });

        loadProgressTimeline();
    }

    private void loadProgressTimeline() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("progress")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String imageUrl = doc.getString("imageUrl");
                        String date = doc.getString("date");
                        String weight = doc.getString("weight");
                        progressList.add(new ProgressEntry(imageUrl, date, weight));
                    }
                    progressAdapter.notifyDataSetChanged();
                });
    }
}
