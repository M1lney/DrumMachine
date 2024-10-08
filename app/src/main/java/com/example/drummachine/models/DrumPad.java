package com.example.drummachine.models;

public class DrumPad {
    private String label;
    private String soundPath; // Path to the sound file
    private int soundId; // Sound ID for SoundPool

    public DrumPad(String label, String soundPath) {
        this.label = label;
        this.soundPath = soundPath;
        this.soundId = -1; // Initial value indicating sound is not loaded
    }

    // Getters and setters
    public String getLabel() {
        return label;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }
}



