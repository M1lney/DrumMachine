package com.example.drummachine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drummachine.adapters.DrumPadAdapter;
import com.example.drummachine.controllers.DrumPadController;
import com.example.drummachine.models.DrumKit;
import com.example.drummachine.utils.FileManager;

import java.io.File;


public class DrumMachineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SoundPool soundPool;
    private DrumPadAdapter adapter;
    private DrumKit currentDrumKit;

    private ActivityResultLauncher<Intent> filePickerLauncher;
    private FileManager fileManager;

    private boolean isSwapMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drum_machine);

        fileManager = new FileManager(this);

        setupFilePickerLauncher();

        // Initialize SoundPool and RecyclerView
        initializeSoundPool();
        initializeRecyclerView();

        findViewById(R.id.import_sound_files).setOnClickListener(v -> openFilePicker());

        ToggleButton toggleModeButton = findViewById(R.id.toggle_swap_mode_button);
        toggleModeButton.setOnCheckedChangeListener((buttonView, isChecked) -> isSwapMode = isChecked);
    }

    private void initializeSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build();
    }

    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns
        currentDrumKit = new DrumKit("Default Kit");
        DrumPadController controller = new DrumPadController(soundPool);

        adapter = new DrumPadAdapter(currentDrumKit, soundPool, this, controller);
        recyclerView.setAdapter(adapter);
    }

    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ClipData clipData = result.getData().getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri uri = clipData.getItemAt(i).getUri();
                                fileManager.saveFileToInternalStorage(uri);
                            }
                        } else {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                fileManager.saveFileToInternalStorage(uri);
                            }
                        }
                    }
                }
        );
    }

    public void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filePickerLauncher.launch(intent);
    }

    public void openInternalFilePicker(int padPosition) {
        File[] files = fileManager.getInternalFiles();

        if (files != null && files.length > 0) {
            showFileSelectionDialog(files, padPosition);
        } else {
            Toast.makeText(this, "No sounds available. Please import sounds first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileSelectionDialog(File[] files, int padPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Sound");

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        builder.setItems(fileNames, (dialog, which) -> {
            String selectedFilePath = files[which].getAbsolutePath();
            adapter.updateDrumPad(padPosition, selectedFilePath);
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


