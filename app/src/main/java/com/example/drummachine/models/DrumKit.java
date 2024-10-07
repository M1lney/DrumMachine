package com.example.drummachine.models;

import com.example.drummachine.models.DrumPad;

import java.util.ArrayList;
import java.util.List;

public class DrumKit {
    private List<DrumPad> drumPads;

    public DrumKit() {
        drumPads = new ArrayList<>();
    }

    // Add a new DrumPad to the kit
    public void addDrumPad(DrumPad drumPad) {
        drumPads.add(drumPad);
    }

    // Retrieve the list of DrumPads
    public List<DrumPad> getDrumPads() {
        return drumPads;
    }

    // Update the sound of a specific drum pad
    public void updateDrumPad(int index, String newLabel, int newSoundId) {
        if (index >= 0 && index < drumPads.size()) {
            DrumPad pad = drumPads.get(index);
            pad.setLabel(newLabel);
            pad.setSoundId(newSoundId);
        }
    }

    // Get a specific DrumPad by its index
    public DrumPad getDrumPad(int index) {
        if (index >= 0 && index < drumPads.size()) {
            return drumPads.get(index);
        }
        return null;
    }
}
