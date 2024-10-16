package com.example.drummachine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drummachine.adapters.DrumPadAdapter;
import com.example.drummachine.controllers.DrumPadController;
import com.example.drummachine.models.DrumKit;
import com.example.drummachine.utils.FileManager;

import org.json.JSONException;

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

        String drumKitJson = getIntent().getStringExtra("DRUM_KIT");
        if (drumKitJson != null) {
            try {
                currentDrumKit = DrumKit.fromJson(drumKitJson);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            currentDrumKit = new DrumKit("Default Kit");
        }

        // Initialize SoundPool and RecyclerView
        initializeSoundPool();
        initializeRecyclerView();

        findViewById(R.id.import_sound_files).setOnClickListener(v -> openFilePicker());
        findViewById(R.id.new_drum_kitButton).setOnClickListener(v -> startNewDrumKit());
        findViewById(R.id.save_drum_kitButton).setOnClickListener(v -> showSaveDialog());
        findViewById(R.id.load_drum_kitButton).setOnClickListener(v -> showFileSelectionDialogForDrumKits());



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

    private void startNewDrumKit() {
        // Release the SoundPool resources
        if (soundPool != null) {
            soundPool.release();
        }

        // Start a new DrumMachineActivity
        Intent intent = new Intent(this, DrumMachineActivity.class);
        startActivity(intent);

        // Finish the current activity
        finish();
    }

    public void saveDrumKit(String filename) {
        fileManager.saveDrumKitToInternalStorage(filename, currentDrumKit);
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Drum Kit");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter file name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString();
                // Call your save method here with the fileName
                saveDrumKit(fileName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showFileSelectionDialogForDrumKits() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Drum Kit");
        File[] files = fileManager.getInternalFiles("drum_kits");
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        builder.setItems(fileNames, (dialog, which) -> {
            String selectedFileName = files[which].getName();
            try {
                loadDrumKit(selectedFileName);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        builder.create().show();
    }

    public void loadDrumKit(String filename) throws JSONException {
        DrumKit loadedDrumKit = fileManager.loadDrumKitFromInternalStorage(filename);
        if (loadedDrumKit != null) {
            Intent intent = new Intent(this, DrumMachineActivity.class);
            intent.putExtra("DRUM_KIT", loadedDrumKit.toJson()); // Pass the loaded drum kit data
            startActivity(intent);
            finish(); // Optionally finish the current activity
        }
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


