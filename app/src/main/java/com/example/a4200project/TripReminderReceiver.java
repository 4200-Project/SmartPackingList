package com.example.a4200project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.util.List;

public class TripReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String tripName = intent.getStringExtra("LIST_NAME");
        int listId = intent.getIntExtra("LIST_ID", -1);

        if (listId == -1 || tripName == null) return;

        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "packingitems")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        List<PackingItem> allItems = db.packingItemDao().getAllForList(listId);

        int packedCount = 0;
        int total = allItems.size();
        for (PackingItem item : allItems) {
            if (item.isChecked()) {
                packedCount++;
            }
        }

        String contentText;
        if (total == 0) {
            contentText = "You haven't added any items yet.";
        } else if (packedCount == total) {
            contentText = "All packed! You're good to go.";
        } else {
            int remaining = total - packedCount;
            contentText = remaining + " item" + (remaining == 1 ? " hasn't" : "s haven't") + " been packed.";
        }

        sendTripNotification(context, "Your trip " + tripName + " is coming up!", contentText);
    }

    private void sendTripNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "tripReminderChannel";
        NotificationChannel channel = new NotificationChannel(channelId, "Trip Reminders", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(1001, builder.build());
    }
}