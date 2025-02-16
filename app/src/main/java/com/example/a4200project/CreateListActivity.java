package com.example.a4200project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class CreateListActivity extends AppCompatActivity {

    // UI elements
    private EditText etListName, etTripType, etDestination, etDuration;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        // Initialize UI elements
        etListName = findViewById(R.id.etListName);
        etTripType = findViewById(R.id.etTripType);
        etDestination = findViewById(R.id.etDestination);
        etDuration = findViewById(R.id.etDuration);
        btnSave = findViewById(R.id.btnSave);

        // Set up button click listener for saving the list
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String listName = etListName.getText().toString();
                String tripType = etTripType.getText().toString();
                String destination = etDestination.getText().toString();
                String duration = etDuration.getText().toString();

                // For now, just navigate back to MainActivity
                Intent intent = new Intent(CreateListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}