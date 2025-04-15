package com.example.workoutplannerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

    private final List<ProgressEntry> progressList;

    public ProgressAdapter(List<ProgressEntry> progressList) {
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_entry, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        ProgressEntry entry = progressList.get(position);
        holder.dateTextView.setText(entry.getDate() != null ? entry.getDate() : "No Date");
        holder.weightTextView.setText("Weight: " + (entry.getWeight() != null ? entry.getWeight() : "N/A") + " kg");

        if (entry.getImageUrl() != null && !entry.getImageUrl().isEmpty()) {
            Glide.with(holder.imageView.getContext())
                    .load(entry.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_default_profile); // Replace with a fallback image if needed
        }
    }

    @Override
    public int getItemCount() {
        return progressList != null ? progressList.size() : 0;
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, weightTextView;
        ImageView imageView;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.itemDateText);
            weightTextView = itemView.findViewById(R.id.itemWeightText);
            imageView = itemView.findViewById(R.id.itemImage);
        }
    }
}
