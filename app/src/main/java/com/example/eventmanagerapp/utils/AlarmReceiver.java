package com.example.eventmanagerapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.eventmanagerapp.presentation.EventDetailActivity;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "event_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        int eventId = intent.getIntExtra("event_id", -1);
        String title = intent.getStringExtra("title");
        if (title == null || title.trim().isEmpty()) title = "Nhắc nhở sự kiện";

        Intent open = new Intent(context, EventDetailActivity.class);
        open.putExtra("event_id", eventId);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(
                context,
                eventId,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Nhắc nhở")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pi);

        nm.notify(eventId, builder.build());
    }
}
