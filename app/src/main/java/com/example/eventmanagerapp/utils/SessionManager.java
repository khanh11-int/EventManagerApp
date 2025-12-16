package com.example.eventmanagerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager - Quản lý phiên đăng nhập
 */
public class SessionManager {

    private static final String PREF_NAME = "EventManagerSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Lưu session khi đăng nhập thành công
     */
    public void createLoginSession(int userId, String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    /**
     * Xoá session khi đăng xuất
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Check xem user đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy user ID của user hiện tại
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Lấy username của user hiện tại
     */
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }
}