package com.example.drummachine.controllers;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.example.drummachine.models.DrumKit;
import com.example.drummachine.models.DrumPad;

public class DrumPadController {
    private static final String TAG = "DrumPadController"; // Tag for logging

    private static DrumPadController instance;
    private SoundPool soundPool;

    private DrumPadController(SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    public static synchronized DrumPadController getInstance(Context context) {
        if (instance == null) {
            instance = new DrumPadController(createSoundPool());
            Log.e(TAG, "New instance created with new soundPool");

        }
        return instance;
    }

    public static void resetInstance() {
        instance = null; // Allow a new instance to be created
    }

    // Method to play or load sound
    public void playOrLoadSound(DrumPad drumPad) {
        if (soundPool != null) {
            if (drumPad.getSoundId() != -1) {
                Log.d(TAG, "Playing sound ID: " + drumPad.getSoundId());
                soundPool.play(drumPad.getSoundId(), 1.0f, 1.0f, 1, 0, 1.0f);
            } else {
                Log.d(TAG, "Sound ID is -1, loading sound");
                loadSound(drumPad);
            }
        } else {
            Log.e(TAG, "SoundPool is not initialized - can't play or load sound");
        }
    }

    // Method to loop sound
    public void loopSound(DrumPad drumPad) {
        if (drumPad.getSoundId() != -1) {
            soundPool.play(drumPad.getSoundId(), 1.0f, 1.0f, 1, -1, 1.0f);
        }
    }

    // Method to load sound
    private void loadSound(DrumPad drumPad) {
        if (soundPool != null) {
            int soundId = soundPool.load(drumPad.getSoundPath(), 1);
            if (soundId != 0) { // Check if sound loaded successfully
                drumPad.setSoundId(soundId);
            } else {
                Log.e(TAG, "Failed to load sound: " + drumPad.getSoundPath());
            }
        } else {
            Log.e(TAG, "SoundPool is not initialized");
        }
    }

    public void unloadSound(DrumPad drumPad) {
        if (soundPool != null && drumPad.getSoundId() != -1) {
            soundPool.unload(drumPad.getSoundId());
            drumPad.setSoundId(-1);
        }
    }

    // Method to swap sound
    public void swapSound(int index, String soundPath, DrumKit drumKit) {
        DrumPad drumPad = drumKit.getDrumPads().get(index);

        if (drumPad.getSoundId() != -1) {
            unloadSound(drumPad);
        }

        drumKit.updateDrumPad(index, soundPath);

        loadSound(drumPad);
    }

    private static SoundPool createSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        return new SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build();
    }

    public void reinitializeSoundPool() {
        Log.d(TAG, "Releasing and creating a new SoundPool instance");
        releaseSoundPool();
        soundPool = createSoundPool();
    }

    public void releaseSoundPool() {

        if (soundPool != null) {
            soundPool.release();
            Log.d(TAG, "Releasing SoundPool instance");
            soundPool = null; // Set to null to indicate it's released
        }
    }

    public void reloadSounds(DrumKit drumKit) {

        for (DrumPad drumPad : drumKit.getDrumPads()) {
            if (drumPad.getSoundPath() != null && !drumPad.getSoundPath().isEmpty()) {
                Log.d(TAG, "Reloading sound for pad: " + drumPad.getSoundPath());

                loadSound(drumPad); // Load sound and set soundId
            }
        }
    }
}
