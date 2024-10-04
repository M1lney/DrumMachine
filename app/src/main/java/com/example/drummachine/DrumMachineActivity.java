package com.example.drummachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drummachine.adapters.DrumPadAdapter;
import com.example.drummachine.models.DrumPad;

import java.util.ArrayList;
import java.util.List;

public class DrumMachineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SoundPool soundPool;
    private DrumPadAdapter adapter;
    private List<DrumPad> drumPadList;
    private int[] soundResources = {R.raw.kick, R.raw.snare, R.raw.hihatclosed};  // Example sound files



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drum_machine);

        // Initialize SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns, adjust as needed

        drumPadList = new ArrayList<>();

        adapter = new DrumPadAdapter(drumPadList, soundPool, this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.add_drum_pad_button).setOnClickListener(v -> showSoundSelectionDialog());

    }

    private void showSoundSelectionDialog() {
        String[] soundLabels = {"Kick", "Snare", "Hi-Hat closed"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a sound")
                .setItems(soundLabels, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Add a new drum pad with the selected sound
                        int soundId = soundPool.load(DrumMachineActivity.this, soundResources[which], 1);
                        DrumPad newPad = new DrumPad(soundLabels[which], soundId);
                        adapter.addDrumPad(newPad);
                    }
                });
        builder.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}
