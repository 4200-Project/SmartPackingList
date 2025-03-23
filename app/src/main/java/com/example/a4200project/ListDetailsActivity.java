package com.example.a4200project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import java.util.ArrayList;
import java.util.List;

public class ListDetailsActivity extends AppCompatActivity {

    private TextView tvListName;
    private ImageButton btnEditList;
    private ListView listViewItems;
    private Button btnAddItem;
    private List<PackingItem> items;
    private ItemsAdapter adapter;
    private AppDatabase db;
    private int listId;
    private String listName;

    // Hard-coded for demonstration
    private String tripType = "Beach";
    private String destination = "Bali";
    private String duration = "5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        // Initialize UI elements
        tvListName = findViewById(R.id.tvListName);
        btnEditList = findViewById(R.id.btnEditList);
        listViewItems = findViewById(R.id.listViewItems);
        btnAddItem = findViewById(R.id.btnAddItem);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "packingitems")
                .allowMainThreadQueries() // For simplicity, but not recommended for production
                .build();

        // Get list details from intent
        listId = getIntent().getIntExtra("LIST_ID", -1);
        listName = getIntent().getStringExtra("LIST_NAME");
        tvListName.setText(listName);

        // Load items for this list
        loadItems();

        // Handle item click (Edit an item)
        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            showEditDialog(position);
        });

        // Handle long click (Delete an item)
        listViewItems.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });

        // Handle Add Item Button Click
        btnAddItem.setOnClickListener(v -> showAddItemDialog());

        // Handle Edit List Button
        btnEditList.setOnClickListener(v -> {
            // Navigate to CreateListActivity in edit mode
            Intent intent = new Intent(ListDetailsActivity.this, CreateListActivity.class);
            intent.putExtra("EDIT_MODE", true);
            intent.putExtra("LIST_ID", listId);
            intent.putExtra("LIST_NAME", listName);
            intent.putExtra("TRIP_TYPE", getIntent().getStringExtra("TRIP_TYPE"));
            intent.putExtra("DESTINATION", getIntent().getStringExtra("DESTINATION"));
            intent.putExtra("DURATION", getIntent().getStringExtra("DURATION"));
            startActivity(intent);
        });
    }

    private void loadItems() {
        items = db.packingItemDao().getAllForList(listId);
        adapter = new ItemsAdapter(this, items);
        listViewItems.setAdapter(adapter);
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        final EditText input = new EditText(this);
        input.setHint("Enter item name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String itemName = input.getText().toString().trim();
            if (!itemName.isEmpty()) {
                PackingItem newItem = new PackingItem(listId, itemName, false);
                db.packingItemDao().insert(newItem);
                loadItems(); // Refresh the list
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item");

        final EditText input = new EditText(this);
        PackingItem item = items.get(position);
        input.setText(item.getName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedName = input.getText().toString().trim();
            if (!updatedName.isEmpty()) {
                item.setName(updatedName);
                db.packingItemDao().update(item);
                loadItems(); // Refresh the list
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            PackingItem item = items.get(position);
            db.packingItemDao().delete(item);
            loadItems(); // Refresh the list
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Check if list still exists
        PackingList currentList = db.packingListDao().getById(listId);
        if (currentList == null) {
            // List was deleted, return to main activity
            Toast.makeText(this, "List has been deleted", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Update list name if it changed
        if (!currentList.getName().equals(listName)) {
            listName = currentList.getName();
            tvListName.setText(listName);
        }

        // Refresh items
        loadItems();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
