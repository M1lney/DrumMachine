package com.example.drummachine.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import com.example.drummachine.controllers.DrumPadController;
import com.example.drummachine.models.DrumKit;
import com.example.drummachine.models.DrumPad;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private final Context context;
    private static final String DRUM_KITS_FOLDER = "drum_kits";
    private static final String SOUND_FILES_FOLDER = "sound_files";

    public FileManager(Context context) {
        this.context = context;
        createDirectories();
    }

    private void createDirectories() {
        File drumKitsDir = new File(context.getFilesDir(), DRUM_KITS_FOLDER);
        if (!drumKitsDir.exists()) {
            drumKitsDir.mkdir();
        }

        File soundFilesDir = new File(context.getFilesDir(), SOUND_FILES_FOLDER);
        if (!soundFilesDir.exists()) {
            soundFilesDir.mkdir();
        }
    }

    public void saveFileToInternalStorage(Uri fileUri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream inputStream = resolver.openInputStream(fileUri);
            String fileName = getFileName(fileUri);
            File outputFile = new File(new File(context.getFilesDir(), SOUND_FILES_FOLDER), fileName); // Save to sound files folder
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            Toast.makeText(context, "File saved to internal storage: " + fileName, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show();
        }
    }

    public File[] getInternalFiles(String folderName) {
        File internalStorageDir = new File(context.getFilesDir(), folderName);
        return internalStorageDir.listFiles();
    }

    public void saveDrumKitToInternalStorage(String filename, DrumKit drumKit) {
        try {
            String json = drumKit.toJson();
            File outputFile = new File(new File(context.getFilesDir(), DRUM_KITS_FOLDER), filename); // Save to drum kits folder
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(json.getBytes());
            outputStream.close();

            Toast.makeText(context, "Drum kit saved: " + filename, Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save drum kit", Toast.LENGTH_SHORT).show();
        }
    }

    public DrumKit loadDrumKitFromInternalStorage(String filename) {
        File file = new File(new File(context.getFilesDir(), DRUM_KITS_FOLDER), filename);
        if (!file.exists()) {
            Toast.makeText(context, "File does not exist: " + filename, Toast.LENGTH_SHORT).show();
            return null;
        }
        StringBuilder jsonBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String json = jsonBuilder.toString();
            Log.d("FileManager", "Loaded drum kit JSON from " + filename + ": " + json); // Log the loaded JSON

            return DrumKit.fromJson(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to load drum kit", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public File[] getInternalFiles() {
        File internalStorageDir = context.getFilesDir();
        return internalStorageDir.listFiles();
    }

    private List<String> getAvailableDrumKits() {
        File[] savedDrumKits = getInternalFiles(DRUM_KITS_FOLDER); // Get drum kits from the drum kits folder
        List<String> drumKitNames = new ArrayList<>();

        if (savedDrumKits != null) {
            for (File file : savedDrumKits) {
                drumKitNames.add(file.getName()); // Add file names to the list
            }
        }

        return drumKitNames;
    }

    private List<String> getAvailableSoundFiles() {
        File[] savedSoundFiles = getInternalFiles(SOUND_FILES_FOLDER); // Get sound files from the sound files folder
        List<String> soundFileNames = new ArrayList<>();

        if (savedSoundFiles != null) {
            for (File file : savedSoundFiles) {
                soundFileNames.add(file.getName()); // Add file names to the list
            }
        }

        return soundFileNames;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}

