package com.example.drummachine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.provider.OpenableColumns;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drummachine.adapters.DrumPadAdapter;
import com.example.drummachine.models.DrumKit;
import com.example.drummachine.models.DrumPad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DrumMachineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SoundPool soundPool;
    private DrumPadAdapter adapter;
    private DrumKit currentDrumKit;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    private boolean isSwapMode = false; // Track whether we are in swap mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drum_machine);

        // Initialize SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build();

        setupFilePickerLauncher();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns, adjust as needed

        currentDrumKit = new DrumKit("Default Kit");

        adapter = new DrumPadAdapter(currentDrumKit, soundPool, this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.import_sound_files).setOnClickListener(v -> openFilePicker());

        ToggleButton toggleModeButton = findViewById(R.id.toggle_swap_mode_button);

        toggleModeButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isSwapMode = isChecked;  // If checked, we are in swap mode
        });

    }

    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // Handle multiple files or a folder
                        ClipData clipData = result.getData().getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri uri = clipData.getItemAt(i).getUri();
                                saveFileToInternalStorage(uri);
                            }
                        } else {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                saveFileToInternalStorage(uri);
                            }
                        }
                    }
                }
        );
    }

    public void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");  // Allow only audio files
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple files
        filePickerLauncher.launch(intent);
    }

    private void saveFileToInternalStorage(Uri fileUri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(fileUri);
            String fileName = getFileName(fileUri);
            File outputFile = new File(getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            // Notify the user or update the UI
            Toast.makeText(this, "File saved to internal storage: " + fileName, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    // Check if the column index is valid
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
        // Fallback to the URI path if result is still null
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    public void openInternalFilePicker(DrumPadAdapter adapter, int padPosition) {
        File internalStorageDir = getFilesDir();
        File[] files = internalStorageDir.listFiles();

        if (files != null && files.length > 0) {
            // Show the list of files in a dialog or custom UI for selection
            showFileSelectionDialog(files, adapter, padPosition);
        } else {
            Toast.makeText(this, "No sounds available. Please import sounds first.", Toast.LENGTH_SHORT).show();
        }
    }

    // Show files in a dialog for user to pick
    private void showFileSelectionDialog(File[] files, DrumPadAdapter adapter, int padPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Sound");

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        builder.setItems(fileNames, (dialog, which) -> {
            String selectedFilePath = files[which].getAbsolutePath();
            adapter.updateDrumPad(padPosition, selectedFilePath);  // Directly update the drum pad in the adapter
        });

        builder.create().show();
    }

    public boolean isSwapMode() {
        return isSwapMode;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}

