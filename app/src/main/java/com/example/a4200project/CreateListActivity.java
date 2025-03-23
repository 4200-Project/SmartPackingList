package com.example.a4200project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class CreateListActivity extends AppCompatActivity {
    private EditText etListName;
    private EditText etTripType;
    private EditText etDestination;
    private EditText etDuration;
    private Button btnSave;
    private Button btnDeleteList;
    private AppDatabase db;
    private boolean isEditMode = false;
    private String originalListName;
    private int originalListId;

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

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "packingitems")
                .allowMainThreadQueries() // For simplicity, but not recommended for production
                .build();

        // Check if we're in edit mode
        isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        if (isEditMode) {
            // Load existing data
            originalListName = getIntent().getStringExtra("LIST_NAME");
            originalListId = getIntent().getIntExtra("LIST_ID", -1);
            String tripType = getIntent().getStringExtra("TRIP_TYPE");
            String destination = getIntent().getStringExtra("DESTINATION");
            String duration = getIntent().getStringExtra("DURATION");

            etListName.setText(originalListName);
            etTripType.setText(tripType);
            etDestination.setText(destination);
            etDuration.setText(duration);

            // Show delete button in edit mode
            btnDeleteList.setVisibility(View.VISIBLE);
            btnSave.setText("Update List");
        }

        // Set up save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveList();
            }
        });

        // Set up delete button click listener
        btnDeleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteList();
            }
        });
    }

    private void saveList() {
        String listName = etListName.getText().toString().trim();
        String tripType = etTripType.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();

        if (listName.isEmpty()) {
            etListName.setError("List name is required");
            return;
        }

        try {
            if (isEditMode) {
                // Update existing list
                PackingList existingList = db.packingListDao().getById(originalListId);
                if (existingList != null) {
                    existingList.setName(listName);
                    existingList.setTripType(tripType);
                    existingList.setDestination(destination);
                    existingList.setDuration(duration);
                    db.packingListDao().update(existingList);
                }
            } else {
                // Create a new packing list
                PackingList newList = new PackingList(listName, tripType, destination, duration);
                db.packingListDao().insert(newList);
            }
            
            Toast.makeText(this, isEditMode ? "List updated successfully" : "List created successfully", 
                         Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving list: " + e.getMessage(), 
                         Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteList() {
        try {
            PackingList listToDelete = db.packingListDao().getById(originalListId);
            if (listToDelete != null) {
                db.packingListDao().delete(listToDelete);
                Toast.makeText(this, "List deleted successfully", Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error deleting list: " + e.getMessage(), 
                         Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
