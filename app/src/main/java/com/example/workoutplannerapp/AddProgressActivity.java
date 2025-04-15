package com.example.workoutplannerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AddProgressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<ProgressEntry, ProgressViewHolder> adapter;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        recyclerView = findViewById(R.id.progressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.addProgressButton).setOnClickListener(v -> {
            startActivity(new Intent(this, AddProgressEntryActivity.class));
        });

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Query query = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("progress")
                    .orderBy("date", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<ProgressEntry> options = new FirestoreRecyclerOptions.Builder<ProgressEntry>()
                    .setQuery(query, ProgressEntry.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<ProgressEntry, ProgressViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ProgressViewHolder holder, int position, @NonNull ProgressEntry model) {
                    holder.dateTextView.setText(model.getDate());
                    holder.weightTextView.setText("Weight: " + model.getWeight() + " kg");

                    Glide.with(AddProgressActivity.this)
                            .load(model.getImageUrl())
                            .into(holder.imageView);

                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);

                    holder.deleteButton.setOnClickListener(v -> {
                        new AlertDialog.Builder(AddProgressActivity.this)
                                .setTitle("Delete Entry")
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    snapshot.getReference().delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(AddProgressActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(AddProgressActivity.this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });
                }

                @NonNull
                @Override
                public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = getLayoutInflater().inflate(R.layout.item_progress_entry, parent, false);
                    return new ProgressViewHolder(view);
                }
            };

            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, weightTextView;
        ImageView imageView;
        Button deleteButton;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.itemDateText);
            weightTextView = itemView.findViewById(R.id.itemWeightText);
            imageView = itemView.findViewById(R.id.itemImage);
            deleteButton = itemView.findViewById(R.id.deleteButton); // This must match your XML
        }
    }
}
