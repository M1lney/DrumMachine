package com.example.drummachine.adapters;

import android.content.Context;
import android.media.SoundPool;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drummachine.R;
import com.example.drummachine.models.DrumPad;

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
        holder.padButton.setOnClickListener(v -> {
            soundPool.play(drumPad.getSoundId(), 1.0f, 1.0f, 1, 0, 1.0f);
        });
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
