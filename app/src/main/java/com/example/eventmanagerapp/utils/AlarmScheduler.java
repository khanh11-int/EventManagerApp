package com.example.eventmanagerapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.example.eventmanagerapp.utils.AlarmReceiver;

public class AlarmScheduler {

    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public boolean scheduleAlarm(int eventId, String title, long triggerAtMillis) {
        if (alarmManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return false;
            }
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("event_id", eventId);
        intent.putExtra("title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        }

        return true;
    }

    public void cancelAlarm(int eventId) {
        if (alarmManager == null) {
            return;
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true; // Trên Android < 12 không cần quyền này
    }

    public void openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public boolean rescheduleAlarm(int eventId, String title, long triggerAtMillis) {
        cancelAlarm(eventId);
        return scheduleAlarm(eventId, title, triggerAtMillis);
    }
}