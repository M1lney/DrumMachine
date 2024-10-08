package com.example.drummachine.controllers;

import android.media.SoundPool;

import com.example.drummachine.models.DrumKit;
import com.example.drummachine.models.DrumPad;

public class DrumPadController {

    private SoundPool soundPool;

    public DrumPadController(SoundPool soundPool) {
        this.soundPool = soundPool;
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
}
