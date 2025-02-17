package com.example.a4200project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode, switchNotifications;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme before setting content view
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("DarkMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI elements
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        // Load saved preferences
        switchDarkMode.setChecked(sharedPreferences.getBoolean("DarkMode", false));
        switchNotifications.setChecked(sharedPreferences.getBoolean("Notifications", true));

        // Dark Mode Toggle
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("DarkMode", isChecked);
                editor.apply();

                // Apply dark mode and restart activity
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Restart the app to fully apply theme changes
                recreate();
                Toast.makeText(SettingsActivity.this, "Dark Mode " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
            }
        });

        // Notifications Toggle
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Notifications", isChecked);
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
