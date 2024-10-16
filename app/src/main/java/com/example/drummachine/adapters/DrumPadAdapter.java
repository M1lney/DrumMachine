package com.example.drummachine.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.SoundPool;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drummachine.DrumMachineActivity;
import com.example.drummachine.R;
import com.example.drummachine.controllers.DrumPadController;
import com.example.drummachine.models.DrumKit;
import com.example.drummachine.models.DrumPad;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DrumPadAdapter extends RecyclerView.Adapter<DrumPadAdapter.DrumPadViewHolder> {

    private DrumKit drumKit;
    private SoundPool soundPool;
    private Context context;
    private DrumPadController controller;

    public DrumPadAdapter(DrumKit drumKit, SoundPool soundPool, Context context, DrumPadController controller) {
        this.drumKit = drumKit;
        this.soundPool = soundPool;
        this.context = context;
        this.controller = controller;
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
        DrumPad drumPad = drumKit.getDrumPads().get(position);
        holder.padButton.setText(drumPad.getLabel());

        // Create a GestureDetector for handling tap and long press
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (((DrumMachineActivity) context).isSwapMode() || drumPad.getSoundPath() == null) {
                    int currentPosition = holder.getBindingAdapterPosition();
                    ((DrumMachineActivity) context).openInternalFilePicker(currentPosition);
                } else {
                    controller.playOrLoadSound(drumPad);
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // Loop the sound on long press
                controller.loopSound(drumPad);
            }
        });

        // Set touch listener to handle gestures
        holder.padButton.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    public void updateDrumPad(int index, String path) {
        controller.swapSound(index, path, drumKit);
    }


    @Override
    public int getItemCount() {
        return drumKit.getDrumPads().size();
    }

    static class DrumPadViewHolder extends RecyclerView.ViewHolder {
        Button padButton;

        public DrumPadViewHolder(@NonNull View itemView) {
            super(itemView);
            padButton = itemView.findViewById(R.id.drum_pad_button);
        }

    }

}
