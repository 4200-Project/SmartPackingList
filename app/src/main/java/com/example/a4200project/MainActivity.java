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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private Button btnCreateList;
    private FloatingActionButton fabSettings;
    private ListView listView;

    // Temporary data for demonstration
    private ArrayList<String> packingLists;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        btnCreateList = findViewById(R.id.btnCreateList);
        fabSettings = findViewById(R.id.fabSettings); // Floating Action Button for Settings
        listView = findViewById(R.id.listView);

        // Temporary data for demonstration
        packingLists = new ArrayList<>();
        packingLists.add("Beach Trip to Bali");
        packingLists.add("Hiking in the Rockies");
        packingLists.add("Business Trip to New York");

        // Set up the adapter for the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, packingLists);
        listView.setAdapter(adapter);

        // Set up button click listener for creating a new list
        btnCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CreateListActivity
                Intent intent = new Intent(MainActivity.this, CreateListActivity.class);
                startActivity(intent);
            }
        });

        // Set up Floating Action Button click listener for Settings
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Set up item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Navigate to ListDetailsActivity with the selected list name
                String selectedList = packingLists.get(position);
                Intent intent = new Intent(MainActivity.this, ListDetailsActivity.class);
                intent.putExtra("LIST_NAME", selectedList); // Pass data to the next activity
                startActivity(intent);
            }
        });
    }
}
