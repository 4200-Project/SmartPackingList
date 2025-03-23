package com.example.a4200project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.room.Room;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private Button btnCreateList;
    private FloatingActionButton fabSettings;
    private ListView listView;

    private AppDatabase db;
    private List<PackingList> packingLists;
    private ArrayAdapter<PackingList> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("DarkMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        btnCreateList = findViewById(R.id.btnCreateList);
        fabSettings = findViewById(R.id.fabSettings);
        listView = findViewById(R.id.listView);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "packingitems")
                .fallbackToDestructiveMigration() // This will recreate tables if no migration found
                .build();

        // Initialize data
        new Thread(() -> {
            PackingListDao packingListDao = db.packingListDao();
            packingLists = packingListDao.getAll();
            
            // Update UI on main thread
            runOnUiThread(() -> {
                // Set up the adapter for the ListView with a custom toString implementation
                adapter = new ArrayAdapter<PackingList>(this, android.R.layout.simple_list_item_1, packingLists) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        PackingList item = getItem(position);
                        text.setText(item.getName());
                        return view;
                    }
                };
                listView.setAdapter(adapter);
            });
        }).start();

        // Set up button click listener for creating a new list
        btnCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateListActivity.class);
                startActivity(intent);
            }
        });

        // Set up Floating Action Button click listener for Settings
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Set up item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackingList selectedList = packingLists.get(position);
                Intent intent = new Intent(MainActivity.this, ListDetailsActivity.class);
                intent.putExtra("LIST_ID", selectedList.getUid());
                intent.putExtra("LIST_NAME", selectedList.getName());
                intent.putExtra("TRIP_TYPE", selectedList.getTripType());
                intent.putExtra("DESTINATION", selectedList.getDestination());
                intent.putExtra("DURATION", selectedList.getDuration());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list data when activity resumes
        new Thread(() -> {
            PackingListDao packingListDao = db.packingListDao();
            List<PackingList> newList = packingListDao.getAll();
            
            // Update UI on main thread
            runOnUiThread(() -> {
                packingLists = newList;
                if (adapter == null) {
                    // Create new adapter if it doesn't exist
                    adapter = new ArrayAdapter<PackingList>(this, android.R.layout.simple_list_item_1, packingLists) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView text = (TextView) view.findViewById(android.R.id.text1);
                            PackingList item = getItem(position);
                            text.setText(item.getName());
                            return view;
                        }
                    };
                    listView.setAdapter(adapter);
                } else {
                    // Update existing adapter
                    adapter.clear();
                    adapter.addAll(packingLists);
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
