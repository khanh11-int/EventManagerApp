package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.local.UserDao;
import com.example.eventmanagerapp.domain.model.User;
import com.example.eventmanagerapp.utils.SessionManager;

public class RegisterUseCase {

    private final UserDao userDao;
    private final SessionManager sessionManager;

    public RegisterUseCase(Context context) {
        this.userDao = new UserDao(context);
        this.sessionManager = new SessionManager(context);
    }

    public Result execute(String username, String password, String confirmPassword, String fullName) {
        String error = validateInput(username, password, confirmPassword);
        if (error != null) {
            return Result.error(error);
        }

        if (userDao.isUsernameExists(username.trim())) {
            return Result.error("Tên đăng nhập đã được sử dụng");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setFullName(fullName != null ? fullName.trim() : "");
        user.setCreatedAt(System.currentTimeMillis());

        long userId = userDao.insert(user);

        if (userId <= 0) {
            return Result.error("Không thể tạo tài khoản");
        }

        user.setId((int) userId);

        sessionManager.createLoginSession(user.getId(), user.getUsername());

        return Result.success(user);
    }

    private String validateInput(String username, String password, String confirmPassword) {
        if (username == null || username.trim().isEmpty()) {
            return "Vui lòng nhập tên đăng nhập";
        }

        if (username.trim().length() < 3) {
            return "Tên đăng nhập phải có ít nhất 3 ký tự";
        }

        if (password == null || password.isEmpty()) {
            return "Vui lòng nhập mật khẩu";
        }

        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp";
        }

        return null;
    }

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