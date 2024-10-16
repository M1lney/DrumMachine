package com.example.drummachine.controllers;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.drummachine.models.DrumKit;
import com.example.drummachine.models.DrumPad;

public class DrumPadController {

    private static DrumPadController instance;
    private SoundPool soundPool;

    private DrumPadController(SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    public static synchronized DrumPadController getInstance(Context context) {
        if (instance == null) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            SoundPool soundPool = new SoundPool.Builder()
                    .setMaxStreams(8)
                    .setAudioAttributes(audioAttributes)
                    .build();

            instance = new DrumPadController(soundPool);
        }
        return instance;
    }

    // Method to play or load sound
    public void playOrLoadSound(DrumPad drumPad) {
        if (drumPad.getSoundId() != -1) {
            soundPool.play(drumPad.getSoundId(), 1.0f, 1.0f, 1, 0, 1.0f);
        } else {
            loadSound(drumPad);
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
        int soundId = soundPool.load(drumPad.getSoundPath(), 1);
        drumPad.setSoundId(soundId);
    }

    public void unloadSound(DrumPad drumPad) {
        soundPool.unload(drumPad.getSoundId());
        drumPad.setSoundId(-1);
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

    public void releaseSoundPool() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null; // Set to null to indicate it's released
        }
    }

    public void reloadSounds(DrumKit drumKit) {
        for (DrumPad drumPad : drumKit.getDrumPads()) {
            if (drumPad.getSoundPath() != null && !drumPad.getSoundPath().isEmpty()) {
                loadSound(drumPad); // Load sound and set soundId
            }
        }
    }
}
