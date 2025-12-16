package com.example.eventmanagerapp.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.eventmanagerapp.domain.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventDao {

    private final AppDatabase dbHelper;

    // Tên cột
    private static final String COL_ID = "id";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_TITLE = "title";
    private static final String COL_NOTE = "note";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_END_TIME = "end_time";
    private static final String COL_REMIND = "remind_before";
    private static final String TABLE_EVENT = "events";

    public EventDao(Context context) {
        this.dbHelper = AppDatabase.getInstance(context);
    }

    public long insert(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, event.getUserId()); 
        values.put(COL_TITLE, event.getTitle());
        values.put(COL_NOTE, event.getNote());
        values.put(COL_START_TIME, event.getStartTime());
        values.put(COL_END_TIME, event.getEndTime());
        values.put(COL_REMIND, event.getRemindBefore());

        long id = db.insert(TABLE_EVENT, null, values);
        db.close();
        return id;
    }

    public int update(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, event.getUserId());
        values.put(COL_TITLE, event.getTitle());
        values.put(COL_NOTE, event.getNote());
        values.put(COL_START_TIME, event.getStartTime());
        values.put(COL_END_TIME, event.getEndTime());
        values.put(COL_REMIND, event.getRemindBefore());

        int rows = db.update(
                TABLE_EVENT,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(event.getId())}
        );

        db.close();
        return rows;
    }

    public int delete(int eventId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                TABLE_EVENT,
                COL_ID + "=?",
                new String[]{String.valueOf(eventId)}
        );
        db.close();
        return rows;
    }

    public Event getById(int eventId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EVENT,
                null,
                COL_ID + "=?",
                new String[]{String.valueOf(eventId)},
                null, null, null
        );

        Event event = null;
        if (cursor != null && cursor.moveToFirst()) {
            event = cursorToEvent(cursor);
            cursor.close();
        }

        db.close();
        return event;
    }

    public List<Event> getAllByUserId(int userId) {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EVENT,
                null,
                COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                COL_START_TIME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToEvent(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<Event> getAll() {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EVENT,
                null, null, null, null, null,
                COL_START_TIME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToEvent(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public void deleteAllByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_EVENT, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_EVENT, null, null);
        db.close();
    }

    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        event.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
        event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        event.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE)));
        event.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_START_TIME)));
        event.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_END_TIME)));
        event.setRemindBefore(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REMIND)));
        return event;
    }
}