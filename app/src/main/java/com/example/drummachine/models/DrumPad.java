package com.example.drummachine.models;

public class DrumPad {
    private String label;       // Label for the drum pad
    private int soundId;        // Resource ID for predefined sound
    private String soundPath;   // File path or URI for user-uploaded sound

    // Constructor for predefined sounds (resource-based)
    public DrumPad(String label, int soundId) {
        this.label = label;
        this.soundId = soundId;
        this.soundPath = null;  // No path when using predefined sound
    }

    // Constructor for user-uploaded sounds
    public DrumPad(String label, String soundPath) {
        this.label = label;
        this.soundId = -1;      // Invalid ID, since we are using a file path
        this.soundPath = soundPath;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSoundId() {
        return soundId;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
        this.soundPath = null;  // Reset path since we're using a resource ID
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
        this.soundId = -1;  // Reset soundId since we're using a file path
    }
}

