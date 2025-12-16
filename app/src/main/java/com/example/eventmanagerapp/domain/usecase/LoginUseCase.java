package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.local.UserDao;
import com.example.eventmanagerapp.domain.model.User;
import com.example.eventmanagerapp.utils.SessionManager;

/**
 * Use Case - Đăng nhập
 */
public class LoginUseCase {

    private final UserDao userDao;
    private final SessionManager sessionManager;

    public LoginUseCase(Context context) {
        this.userDao = new UserDao(context);
        this.sessionManager = new SessionManager(context);
    }

    /**
     * Thực hiện đăng nhập
     */
    public Result execute(String username, String password) {
        // 1. Validate input
        if (username == null || username.trim().isEmpty()) {
            return Result.error("Vui lòng nhập tên đăng nhập");
        }

        if (password == null || password.trim().isEmpty()) {
            return Result.error("Vui lòng nhập mật khẩu");
        }

        // 2. Tìm user trong database
        User user = userDao.findByUsername(username.trim());

        if (user == null) {
            return Result.error("Tên đăng nhập không tồn tại");
        }

        // 3. Check password
        if (!user.getPassword().equals(password)) {
            return Result.error("Mật khẩu không đúng");
        }

        // 4. Lưu session
        sessionManager.createLoginSession(user.getId(), user.getUsername());

        return Result.success(user);
    }

    /**
     * Result class
     */
    public static class Result {
        private final boolean success;
        private final String errorMessage;
        private final User user;

        private Result(boolean success, String errorMessage, User user) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.user = user;
        }

        public static Result success(User user) {
            return new Result(true, null, user);
        }

        public static Result error(String message) {
            return new Result(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public User getUser() {
            return user;
        }
    }
}