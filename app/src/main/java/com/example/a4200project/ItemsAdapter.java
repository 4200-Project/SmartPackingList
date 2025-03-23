package com.example.a4200project;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.room.Room;

import java.util.List;

public class ItemsAdapter extends ArrayAdapter<PackingItem> {
    private AppDatabase db;

    public ItemsAdapter(Context context, List<PackingItem> items) {
        super(context, 0, items);
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "packingitems")
                .allowMainThreadQueries()
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PackingItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_list_details, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.checkBoxItem);
        TextView textViewItemName = convertView.findViewById(R.id.textViewItemName);

        textViewItemName.setText(item.getName());

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(item.isChecked());

        updateTextStyle(textViewItemName, item.isChecked());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            db.packingItemDao().update(item);
            updateTextStyle(textViewItemName, isChecked);
        });

        return convertView;
    }

    private void updateTextStyle(TextView textView, boolean isChecked) {
        if (isChecked) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (db != null) {
            db.close();
        }
        super.finalize();
    }
}
