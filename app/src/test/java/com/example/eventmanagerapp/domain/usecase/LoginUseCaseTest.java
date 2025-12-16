package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.local.UserDao;
import com.example.eventmanagerapp.domain.model.User;
import com.example.eventmanagerapp.utils.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoginUseCase
 */
@RunWith(RobolectricTestRunner.class)
public class LoginUseCaseTest {

    private LoginUseCase loginUseCase;
    private Context context;
    private UserDao userDao;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        loginUseCase = new LoginUseCase(context);
        userDao = new UserDao(context);
        sessionManager = new SessionManager(context);
        
        // Clear any existing session
        sessionManager.logout();
        
        // Clear database
        userDao.deleteAll();
    }

    @Test
    public void testExecute_SuccessfulLogin() {
        // Prepare: Create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setFullName("Test User");
        user.setCreatedAt(System.currentTimeMillis());
        long userId = userDao.insert(user);
        user.setId((int) userId);

        // Execute
        LoginUseCase.Result result = loginUseCase.execute("testuser", "password123");

        // Verify
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        assertNotNull(result.getUser());
        assertEquals("testuser", result.getUser().getUsername());
    }

    @Test
    public void testExecute_EmptyUsername() {
        LoginUseCase.Result result = loginUseCase.execute("", "password123");

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập tên đăng nhập", result.getErrorMessage());
        assertNull(result.getUser());
    }

    @Test
    public void testExecute_NullUsername() {
        LoginUseCase.Result result = loginUseCase.execute(null, "password123");

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập tên đăng nhập", result.getErrorMessage());
    }

    @Test
    public void testExecute_EmptyPassword() {
        LoginUseCase.Result result = loginUseCase.execute("testuser", "");

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập mật khẩu", result.getErrorMessage());
    }

    @Test
    public void testExecute_NullPassword() {
        LoginUseCase.Result result = loginUseCase.execute("testuser", null);

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập mật khẩu", result.getErrorMessage());
    }

    @Test
    public void testExecute_UsernameNotExists() {
        LoginUseCase.Result result = loginUseCase.execute("nonexistent", "password123");

        assertFalse(result.isSuccess());
        assertEquals("Tên đăng nhập không tồn tại", result.getErrorMessage());
    }

    @Test
    public void testExecute_WrongPassword() {
        // Prepare: Create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("correctpassword");
        user.setCreatedAt(System.currentTimeMillis());
        userDao.insert(user);

        // Execute with wrong password
        LoginUseCase.Result result = loginUseCase.execute("testuser", "wrongpassword");

        // Verify
        assertFalse(result.isSuccess());
        assertEquals("Mật khẩu không đúng", result.getErrorMessage());
    }

    @Test
    public void testExecute_UsernameWithWhitespace() {
        // Prepare: Create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setCreatedAt(System.currentTimeMillis());
        userDao.insert(user);

        // Execute with whitespace
        LoginUseCase.Result result = loginUseCase.execute("  testuser  ", "password123");

        // Verify
        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_SessionCreated() {
        // Prepare: Create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setCreatedAt(System.currentTimeMillis());
        long userId = userDao.insert(user);

        // Execute login
        LoginUseCase.Result result = loginUseCase.execute("testuser", "password123");

        // Verify session is created
        assertTrue(result.isSuccess());
        assertTrue(sessionManager.isLoggedIn());
        assertEquals("testuser", sessionManager.getUsername());
    }

    @Test
    public void testExecute_CaseSensitiveUsername() {
        // Prepare: Create a user
        User user = new User();
        user.setUsername("TestUser");
        user.setPassword("password123");
        user.setCreatedAt(System.currentTimeMillis());
        userDao.insert(user);

        // Execute with different case
        LoginUseCase.Result result = loginUseCase.execute("testuser", "password123");

        // Verify (should fail if case-sensitive)
        assertFalse(result.isSuccess());
    }
}
