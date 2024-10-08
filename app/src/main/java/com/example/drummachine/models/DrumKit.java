package com.example.drummachine.models;

import com.example.drummachine.models.DrumPad;

import java.util.ArrayList;
import java.util.List;

public class DrumKit {
    private List<DrumPad> drumPads;
    private String kitName;  // To uniquely identify the kit

    public DrumKit(String kitName) {
        this.kitName = kitName;
        drumPads = new ArrayList<>();
        // Initialize with 8 empty drum pads
        for (int i = 0; i < 8; i++) {
            drumPads.add(new DrumPad("Pad " + (i + 1), null));  // No sound associated yet
        }
    }

    // Add or update the sound for a specific drum pad
    public void updateDrumPad(int index, String soundPath) {
        if (index >= 0 && index < drumPads.size()) {
            DrumPad pad = drumPads.get(index);
            pad.setSoundPath(soundPath);
        }
    }

    // Getters for drum pads and kit name
    public List<DrumPad> getDrumPads() {
        return drumPads;
    }

    public String getKitName() {
        return kitName;
    }

    // Save drum kit logic could be placed here (e.g., saving to internal storage)
}

