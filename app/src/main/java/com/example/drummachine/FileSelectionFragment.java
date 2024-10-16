package com.example.drummachine;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.drummachine.utils.FileManager;

import java.io.File;

public class FileSelectionFragment extends Fragment {

    private static final String ARG_DIRECTORY_NAME = "directory_name";
    private ListView listView;
    private OnFileSelectedListener listener;
    private FileManager fileManager;
    private String directoryName;

    public interface OnFileSelectedListener {
        void onFileSelected(String filePath);
    }

    public static FileSelectionFragment newInstance(String directoryName) {
        FileSelectionFragment fragment = new FileSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIRECTORY_NAME, directoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFileSelectedListener) {
            listener = (OnFileSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFileSelectedListener");
        }
        // Create a FileManager instance here
        fileManager = new FileManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_selection, container, false);
        listView = view.findViewById(R.id.list_view);

        if (getArguments() != null) {
            directoryName = getArguments().getString(ARG_DIRECTORY_NAME);
        }

        loadFiles();

        return view;
    }



    private void loadFiles() {
        // Use the directory name to load the appropriate files
        File[] files = fileManager.getInternalFiles(directoryName);

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFile = files[position].getAbsolutePath();
            listener.onFileSelected(selectedFile);
        });
    }
}

