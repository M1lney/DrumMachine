package com.example.drummachine;

import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.AudioAttributes;

public class MainActivity extends AppCompatActivity {

    SoundPool soundPool;
    int kick, snare, hiHat, openHiHat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSoundPool();
        loadSounds();
        setupButtons();
    }

    private void initializeSoundPool() {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .build();
    }

    private void loadSounds() {
        kick = soundPool.load(this, R.raw.kick, 1);
        snare = soundPool.load(this, R.raw.snare, 1);
        hiHat = soundPool.load(this, R.raw.hihatclosed, 1);
    }

    private void setupButtons() {
        Button kickButton = findViewById(R.id.kickButton);
        kickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(kick, 1, 1, 0, 0, 1);
            }
        });

        Button snareButton = findViewById(R.id.snareButton);
        snareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(snare, 1, 1, 0, 0, 1);
            }
        });

        Button hihatButton = findViewById(R.id.hihatButton);
        hihatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(hiHat, 1, 1, 0, 0, 1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}