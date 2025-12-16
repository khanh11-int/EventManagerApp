package com.example.eventmanagerapp.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.eventmanagerapp.domain.model.User;

public class UserDao {

    private final AppDatabase dbHelper;
    private static final String TABLE_USER = "users";

    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_CREATED_AT = "created_at";

    public UserDao(Context context) {
        this.dbHelper = AppDatabase.getInstance(context);
    }

    public long insert(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, user.getUsername());
        values.put(COL_PASSWORD, user.getPassword());
        values.put(COL_FULL_NAME, user.getFullName());
        values.put(COL_CREATED_AT, user.getCreatedAt());

        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    public User findByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USER,
                null,
                COL_USERNAME + "=?",
                new String[]{username},
                null, null, null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        db.close();
        return user;
    }

    public User getById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USER,
                null,
                COL_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        db.close();
        return user;
    }

    public boolean isUsernameExists(String username) {
        return findByUsername(username) != null;
    }

    /**
     * Delete all users (for testing purposes)
     */
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.close();
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)));
        user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)));
        user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
        return user;
    }
}