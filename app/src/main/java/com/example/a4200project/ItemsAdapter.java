package com.example.a4200project;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ItemsAdapter extends ArrayAdapter<PackingItem> {

    public ItemsAdapter(Context context, List<PackingItem> items) {
        super(context, 0, items);
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

        // Set item name
        textViewItemName.setText(item.getName());

        // Set checkbox state
        checkBox.setChecked(item.isChecked());

        // If checked, strike out the text
        if (item.isChecked()) {
            textViewItemName.setPaintFlags(
                    textViewItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textViewItemName.setPaintFlags(
                    textViewItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Handle user check/uncheck
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            if (isChecked) {
                textViewItemName.setPaintFlags(
                        textViewItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textViewItemName.setPaintFlags(
                        textViewItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        return convertView;
    }
}
