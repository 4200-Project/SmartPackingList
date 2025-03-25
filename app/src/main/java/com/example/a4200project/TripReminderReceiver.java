package com.example.a4200project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class TripReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Here we build and send the notification
        String listName = intent.getStringExtra("LIST_NAME");
        sendTripNotification(context, listName);
    }

    private void sendTripNotification(Context context, String listName) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("tripReminderChannel", "Trip Reminder", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "tripReminderChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Packing Reminder")
                .setContentText("Don’t forget to pack! Your trip \"" + listName + "\" is coming up!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show notification
        manager.notify(1001, builder.build());
    }
}