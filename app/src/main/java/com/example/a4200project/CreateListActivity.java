package com.example.a4200project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class CreateListActivity extends AppCompatActivity {

    // UI elements
    private EditText etListName, etTripType, etDestination, etDuration;
    private Button btnSave, btnDeleteList;
    private boolean isEditMode = false;

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
        btnDeleteList = findViewById(R.id.btnDeleteList);

        // Check if we're in Edit Mode
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("EDIT_MODE", false);

        if (isEditMode) {
            // Pre-fill fields
            String listName = intent.getStringExtra("LIST_NAME");
            String tripType = intent.getStringExtra("TRIP_TYPE");
            String destination = intent.getStringExtra("DESTINATION");
            String duration = intent.getStringExtra("DURATION");

            etListName.setText(listName);
            etTripType.setText(tripType);
            etDestination.setText(destination);
            etDuration.setText(duration);

            // Show Delete button
            btnDeleteList.setVisibility(View.VISIBLE);
        }

        // Save button click
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // For now, just navigate back to MainActivity
                // In a real app, you'd update your data store.
                Intent mainIntent = new Intent(CreateListActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        // Delete button click (only visible in edit mode)
        btnDeleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirm deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateListActivity.this);
                builder.setTitle("Delete List");
                builder.setMessage("Are you sure you want to delete this list?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // For now, just go back to MainActivity
                        // In a real app, you'd remove it from your data source
                        Intent mainIntent = new Intent(CreateListActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });
    }
}
