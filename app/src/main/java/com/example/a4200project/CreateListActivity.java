package com.example.a4200project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import java.util.Calendar;

public class CreateListActivity extends AppCompatActivity {
    private EditText etListName, etTripType, etDestination, etDuration, etTripDate;
    private Button btnSave, btnDeleteList;
    private AppDatabase db;
    private boolean isEditMode = false;
    private String originalListName;
    private int originalListId;

    // We'll store the chosen date here as a timestamp
    private long departureTimestamp = 0L; // default 0 means no date chosen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        // Initialize UI elements
        etListName = findViewById(R.id.etListName);
        etTripType = findViewById(R.id.etTripType);
        etDestination = findViewById(R.id.etDestination);
        etDuration = findViewById(R.id.etDuration);
        etTripDate = findViewById(R.id.etDepartureDate); // The "Tap to pick date" field

        btnSave = findViewById(R.id.btnSave);
        btnDeleteList = findViewById(R.id.btnDeleteList);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "packingitems")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // For simplicity, but not recommended for production
                .build();

        // Check if we're in edit mode
        isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        if (isEditMode) {
            // Load existing data from intent
            originalListName = getIntent().getStringExtra("LIST_NAME");
            originalListId = getIntent().getIntExtra("LIST_ID", -1);
            String tripType = getIntent().getStringExtra("TRIP_TYPE");
            String destination = getIntent().getStringExtra("DESTINATION");
            String duration = getIntent().getStringExtra("DURATION");

            etListName.setText(originalListName);
            etTripType.setText(tripType);
            etDestination.setText(destination);
            etDuration.setText(duration);

            // Load existing departure date from DB, if it exists
            PackingList existingList = db.packingListDao().getById(originalListId);
            if (existingList != null) {
                departureTimestamp = existingList.getDepartureDate();
                if (departureTimestamp != 0) {
                    // Show date in EditText as day/month/year
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(departureTimestamp);

                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                    etTripDate.setText(dayOfMonth + "/" + month + "/" + year);
                }
            }

            // Show delete button in edit mode
            btnDeleteList.setVisibility(View.VISIBLE);
            btnSave.setText("Update List");
        }

        // Date picking logic
        etTripDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up save button click listener
        btnSave.setOnClickListener(v -> saveList());

        // Set up delete button click listener
        btnDeleteList.setOnClickListener(v -> deleteList());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();

        if (departureTimestamp != 0) {
            // If we already have a date selected, open to that date
            calendar.setTimeInMillis(departureTimestamp);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year1, int month1, int dayOfMonth) -> {
                    // User selected this date
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year1, month1, dayOfMonth, 0, 0, 0);
                    departureTimestamp = chosen.getTimeInMillis();

                    // Show user in the edit text
                    int displayMonth = month1 + 1; // months are 0-based
                    etTripDate.setText(dayOfMonth + "/" + displayMonth + "/" + year1);
                },
                year, month, day
        );

        // Optionally prevent picking past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
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
                    existingList.setDepartureDate(departureTimestamp); // store date
                    db.packingListDao().update(existingList);
                }
            } else {
                // Create a new packing list
                PackingList newList = new PackingList(listName, tripType, destination, duration, departureTimestamp);
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
