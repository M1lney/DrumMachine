package com.example.drummachine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button drumMachineButton = findViewById(R.id.drumMachineButton);
        drumMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start DrumMachineActivity
                Intent intent = new Intent(MainActivity.this, DrumMachineActivity.class);
                startActivity(intent);
            }
        });
    }
}
