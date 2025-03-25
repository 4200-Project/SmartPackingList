package com.example.a4200project;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import java.util.Calendar;

public class CreateListActivity extends AppCompatActivity {
    private static final String POST_NOTIFICATIONS_PERMISSION = "android.permission.POST_NOTIFICATIONS";
    private EditText etListName, etTripType, etDestination, etDuration, etTripDate;
    private Button btnSave, btnDeleteList;
    private AppDatabase db;
    private boolean isEditMode = false;
    private String originalListName;
    private int originalListId;

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
        etTripDate = findViewById(R.id.etDepartureDate);

        btnSave = findViewById(R.id.btnSave);
        btnDeleteList = findViewById(R.id.btnDeleteList);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "packingitems")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
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

        etTripDate.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> saveList());

        btnDeleteList.setOnClickListener(v -> deleteList());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();

        if (departureTimestamp != 0) {
            calendar.setTimeInMillis(departureTimestamp);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year1, int month1, int dayOfMonth) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year1, month1, dayOfMonth, 0, 0, 0);
                    departureTimestamp = chosen.getTimeInMillis();

                    int displayMonth = month1 + 1; // months are 0-based
                    etTripDate.setText(dayOfMonth + "/" + displayMonth + "/" + year1);
                },
                year, month, day
        );

        // Prevent picking past dates
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
                    scheduleTripReminder(existingList);
                }
            } else {
                // Create a new packing list
                PackingList newList = new PackingList(listName, tripType, destination, duration, departureTimestamp);
                db.packingListDao().insert(newList);
                scheduleTripReminder(newList);
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

    private void scheduleTripReminder(PackingList list) {
        // Check SharedPreferences for notifications
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("Notifications", true);
        if (!notificationsEnabled) {
            return; // user turned off notifications
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(POST_NOTIFICATIONS_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { POST_NOTIFICATIONS_PERMISSION }, 123);
            }
        }

        long departureTimestamp = list.departureDate;
        long oneDayMs = 24L * 60 * 60 * 1000; // 24h in ms
        long dayBefore = departureTimestamp - oneDayMs;

        long now = System.currentTimeMillis();
        long triggerTime = dayBefore;
        if (triggerTime < now) {
            // For testing: 10 seconds from now
            triggerTime = now + 10_000;
        }

        // Prepare broadcast
        Intent intent = new Intent(this, TripReminderReceiver.class);
        intent.putExtra("LIST_NAME", list.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                list.getUid(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean canScheduleExactAlarms = alarmManager.canScheduleExactAlarms();
            if (!canScheduleExactAlarms) {
                openExactAlarmSettings();
            }
        }
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void openExactAlarmSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Fallback: If no system Activity can handle it, show a message
            Toast.makeText(this, "Cannot open exact alarm settings on this device.", Toast.LENGTH_SHORT).show();
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
