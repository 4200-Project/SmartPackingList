package com.example.a4200project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListDetailsActivity extends AppCompatActivity {

    private TextView tvListName;
    private ImageButton btnEditList;
    private ListView listViewItems;
    private Button btnAddItem;
    private ArrayList<PackingItem> items;
    private ItemsAdapter adapter;

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
        btnEditList = findViewById(R.id.btnEditList);  // <-- new
        listViewItems = findViewById(R.id.listViewItems);
        btnAddItem = findViewById(R.id.btnAddItem);

        // Get list name from intent
        String listName = getIntent().getStringExtra("LIST_NAME");
        tvListName.setText(listName);

        items = new ArrayList<>();
        items.add(new PackingItem("Sunscreen", false));
        items.add(new PackingItem("Swimsuit", false));
        items.add(new PackingItem("Flip-flops", false));

        adapter = new ItemsAdapter(this, items);
        listViewItems.setAdapter(adapter);

        // Handle item click (Edit an item)
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog(position);
            }
        });

        // Handle long click (Delete an item)
        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });

        // Handle Add Item Button Click
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });

        // Handle Edit List Button
        btnEditList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start CreateListActivity in "edit mode"
                Intent intent = new Intent(ListDetailsActivity.this, CreateListActivity.class);
                // Pass current list info
                intent.putExtra("EDIT_MODE", true);
                intent.putExtra("LIST_NAME", listName);
                intent.putExtra("TRIP_TYPE", tripType);
                intent.putExtra("DESTINATION", destination);
                intent.putExtra("DURATION", duration);
                startActivity(intent);
                // Optionally finish this activity if you don't plan on coming back
                // finish();
            }
        });

        btnEditList.setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));

            // Navigate to Edit Activity
            Intent intent = new Intent(ListDetailsActivity.this, CreateListActivity.class);
            intent.putExtra("EDIT_MODE", true);
            startActivity(intent);
        });
    }

    // Show dialog to edit an item
    private void showEditDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item");

        final EditText input = new EditText(this);
        input.setText(items.get(position).getName());
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedName = input.getText().toString().trim();
                if (!updatedName.isEmpty()) {
                    items.get(position).setName(updatedName);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Show confirmation dialog to delete an item
    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                items.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    // Show dialog to add a new item
    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        final EditText input = new EditText(this);
        input.setHint("Enter item name");
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newItem = input.getText().toString();
                if (!newItem.isEmpty()) {
                    items.add(new PackingItem(newItem, false));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}
