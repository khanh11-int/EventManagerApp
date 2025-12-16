package com.example.eventmanagerapp.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper - Singleton pattern
 * Quản lý database (tạo, upgrade)
 */
public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "event_manager.db";
    private static final int DATABASE_VERSION = 3; // ✅ Tăng version lên 3

    private static final String TABLE_EVENT = "events";
    private static final String TABLE_USER = "users"; // ✅ Thêm bảng user

    // Singleton instance
    private static AppDatabase instance;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ===== TẠO BẢNG USERS =====
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT, " +
                "created_at INTEGER NOT NULL" +
                ")";
        db.execSQL(createUserTable);

        // ===== TẠO BẢNG EVENTS (có thêm user_id) =====
        String createEventTable = "CREATE TABLE " + TABLE_EVENT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " + // ✅ Thêm user_id
                "title TEXT NOT NULL, " +
                "note TEXT, " +
                "start_time INTEGER NOT NULL, " +
                "end_time INTEGER NOT NULL, " +
                "remind_before INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";
        db.execSQL(createEventTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Tạo bảng users
            String createUserTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "full_name TEXT, " +
                    "created_at INTEGER NOT NULL" +
                    ")";
            db.execSQL(createUserTable);

            // Thêm cột user_id vào bảng events (nếu chưa có)
            try {
                db.execSQL("ALTER TABLE " + TABLE_EVENT + " ADD COLUMN user_id INTEGER DEFAULT 1");
            } catch (Exception e) {
                // Cột đã tồn tại
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}