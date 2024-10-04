package com.example.drummachine.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.SoundPool;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drummachine.R;
import com.example.drummachine.models.DrumPad;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DrumPadAdapter extends RecyclerView.Adapter<DrumPadAdapter.DrumPadViewHolder> {

    private List<DrumPad> drumPadList;
    private SoundPool soundPool;
    private Context context;

    public DrumPadAdapter(List<DrumPad> drumPadList, SoundPool soundPool, Context context) {
        this.drumPadList = drumPadList;
        this.soundPool = soundPool;
        this.context = context;
    }

    @NonNull
    @Override
    public DrumPadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drum_pad_item, parent,
                false);
        return new DrumPadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrumPadViewHolder holder, int position) {
        DrumPad drumPad = drumPadList.get(position);
        holder.padButton.setText(drumPad.getLabel());

        // Load sound and handle click
        // Check if the soundId is a resource ID or a path (for user-added sounds)
        if (drumPad.getSoundPath() != null) {
            // Load custom sound from path
            holder.padButton.setOnClickListener(v -> playSoundFromPath(drumPad.getSoundPath()));
        } else {
            // Load sound from resources
            holder.padButton.setOnClickListener(v -> soundPool.play(drumPad.getSoundId(), 1.0f, 1.0f, 1, 0, 1.0f));
        }
    }

    private void playSoundFromPath(String soundPath) {
        try {
            // Get Uri from the file path
            Uri soundUri = Uri.parse(soundPath);
            ContentResolver contentResolver = context.getContentResolver();

            // Get a ParcelFileDescriptor from the Uri
            ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(soundUri, "r");

            if (pfd != null) {
                AssetFileDescriptor afd = new AssetFileDescriptor(pfd, 0, pfd.getStatSize());

                // Load sound into SoundPool
                int soundId = soundPool.load(afd, 1);
                afd.close();

                // Play the loaded sound
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return drumPadList.size();
    }

    public void addDrumPad(DrumPad drumPad) {
        drumPadList.add(drumPad);
        notifyItemInserted(drumPadList.size() - 1);
    }

    public void removeDrumPad(int position) {
        drumPadList.remove(position);
        notifyItemRemoved(position);
    }

    static class DrumPadViewHolder extends RecyclerView.ViewHolder {
        Button padButton;

        public DrumPadViewHolder(@NonNull View itemView) {
            super(itemView);
            padButton = itemView.findViewById(R.id.drum_pad_button);
        }

    }

}
