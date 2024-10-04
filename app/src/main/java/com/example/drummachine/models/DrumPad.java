package com.example.drummachine.models;

public class DrumPad {
    private String label;
    private int soundId;  // Resource ID or Uri for custom sound

    public DrumPad(String label, int soundId) {
        this.label = label;
        this.soundId = soundId;
    }

    public String getLabel() {
        return label;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }
}
