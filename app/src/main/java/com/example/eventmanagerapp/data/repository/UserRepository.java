package com.example.eventmanagerapp.data.repository;

import android.content.Context;

import com.example.eventmanagerapp.data.local.UserDao;
import com.example.eventmanagerapp.domain.model.User;

public class UserRepository {

    private final UserDao userDao;
    private static UserRepository instance;

    private UserRepository(Context context) {
        this.userDao = new UserDao(context);
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context.getApplicationContext());
        }
        return instance;
    }

    public long createUser(User user) {
        return userDao.insert(user);
    }

    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public User getUserById(int userId) {
        return userDao.getById(userId);
    }

    public boolean isUsernameExists(String username) {
        return userDao.isUsernameExists(username);
    }

    public void clearAll() {
        userDao.deleteAll();
    }
}