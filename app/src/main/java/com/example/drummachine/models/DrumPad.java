package com.example.drummachine.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", label);
        jsonObject.put("soundPath", soundPath != null ? soundPath : JSONObject.NULL);
        Log.d("DrumPad", "Saving DrumPad: label=" + label + ", soundPath=" + soundPath); // Log soundPath being saved

        return jsonObject;
    }

    public static DrumPad fromJson(JSONObject jsonObject) throws JSONException {
        String label = jsonObject.getString("label");
        String soundPath = jsonObject.isNull("soundPath") ? null : jsonObject.getString("soundPath");
        Log.d("DrumPad", "Loading DrumPad: label=" + label + ", soundPath=" + soundPath); // Log soundPath being loaded

        return new DrumPad(label, soundPath);
    }

}



