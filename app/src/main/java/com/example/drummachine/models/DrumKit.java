package com.example.drummachine.models;

import com.example.drummachine.controllers.DrumPadController;
import com.example.drummachine.models.DrumPad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public String toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", kitName);

        JSONArray jsonPads = new JSONArray();
        for (DrumPad pad : drumPads) {
            jsonPads.put(pad.toJson());
        }
        jsonObject.put("drumPads", jsonPads);

        return jsonObject.toString();
    }

    // Deserialize from JSON
    public static DrumKit fromJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        String name = jsonObject.getString("name");
        DrumKit drumKit = new DrumKit(name);

        JSONArray jsonPads = jsonObject.getJSONArray("drumPads");
        drumKit.drumPads.clear();
        for (int i = 0; i < jsonPads.length(); i++) {
            DrumPad pad = DrumPad.fromJson(jsonPads.getJSONObject(i));
            drumKit.drumPads.add(pad);
        }
        return drumKit;
    }

}

