package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.local.UserDao;
import com.example.eventmanagerapp.domain.model.User;
import com.example.eventmanagerapp.utils.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

/**
 * Unit tests for RegisterUseCase
 */
@RunWith(RobolectricTestRunner.class)
public class RegisterUseCaseTest {

    private RegisterUseCase registerUseCase;
    private Context context;
    private UserDao userDao;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        registerUseCase = new RegisterUseCase(context);
        userDao = new UserDao(context);
        sessionManager = new SessionManager(context);
        
        // Clear any existing session
        sessionManager.logout();
        
        // Clear database
        userDao.deleteAll();
    }

    @Test
    public void testExecute_SuccessfulRegistration() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password123", "New User"
        );

        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        assertNotNull(result.getUser());
        assertEquals("newuser", result.getUser().getUsername());
        assertEquals("New User", result.getUser().getFullName());
    }

    @Test
    public void testExecute_EmptyUsername() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "", "password123", "password123", "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập tên đăng nhập", result.getErrorMessage());
    }

    @Test
    public void testExecute_NullUsername() {
        RegisterUseCase.Result result = registerUseCase.execute(
                null, "password123", "password123", "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập tên đăng nhập", result.getErrorMessage());
    }

    @Test
    public void testExecute_UsernameTooShort() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "ab", "password123", "password123", "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Tên đăng nhập phải có ít nhất 3 ký tự", result.getErrorMessage());
    }

    @Test
    public void testExecute_EmptyPassword() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "", "", "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập mật khẩu", result.getErrorMessage());
    }

    @Test
    public void testExecute_NullPassword() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", null, null, "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập mật khẩu", result.getErrorMessage());
    }

    @Test
    public void testExecute_PasswordTooShort() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "12345", "12345", "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Mật khẩu phải có ít nhất 6 ký tự", result.getErrorMessage());
    }

    @Test
    public void testExecute_PasswordsDoNotMatch() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password456", "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Mật khẩu xác nhận không khớp", result.getErrorMessage());
    }

    @Test
    public void testExecute_NullConfirmPassword() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", null, "User"
        );

        assertFalse(result.isSuccess());
        assertEquals("Mật khẩu xác nhận không khớp", result.getErrorMessage());
    }

    @Test
    public void testExecute_UsernameAlreadyExists() {
        // First registration
        registerUseCase.execute("existinguser", "password123", "password123", "User 1");

        // Try to register with same username
        RegisterUseCase.Result result = registerUseCase.execute(
                "existinguser", "password456", "password456", "User 2"
        );

        assertFalse(result.isSuccess());
        assertEquals("Tên đăng nhập đã được sử dụng", result.getErrorMessage());
    }

    @Test
    public void testExecute_UsernameWithWhitespace() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "  newuser  ", "password123", "password123", "User"
        );

        assertTrue(result.isSuccess());
        assertEquals("newuser", result.getUser().getUsername()); // Should be trimmed
    }

    @Test
    public void testExecute_NullFullName() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password123", null
        );

        assertTrue(result.isSuccess());
        assertEquals("", result.getUser().getFullName());
    }

    @Test
    public void testExecute_EmptyFullName() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password123", ""
        );

        assertTrue(result.isSuccess());
        assertEquals("", result.getUser().getFullName());
    }

    @Test
    public void testExecute_FullNameWithWhitespace() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password123", "  Full Name  "
        );

        assertTrue(result.isSuccess());
        assertEquals("Full Name", result.getUser().getFullName()); // Should be trimmed
    }

    @Test
    public void testExecute_AutoLoginAfterRegistration() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password123", "User"
        );

        assertTrue(result.isSuccess());
        
        // Verify session is created
        assertTrue(sessionManager.isLoggedIn());
        assertEquals("newuser", sessionManager.getUsername());
    }

    @Test
    public void testExecute_CreatedAtTimestampSet() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password123", "User"
        );

        assertTrue(result.isSuccess());
        assertTrue(result.getUser().getCreatedAt() > 0);
        assertTrue(result.getUser().getCreatedAt() <= System.currentTimeMillis());
    }

    @Test
    public void testExecute_MinimumValidUsername() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "abc", "password123", "password123", "User"
        );

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_MinimumValidPassword() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "123456", "123456", "User"
        );

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_SpecialCharactersInUsername() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "user@123", "password123", "password123", "User"
        );

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_VietnameseCharactersInFullName() {
        RegisterUseCase.Result result = registerUseCase.execute(
                "newuser", "password123", "password123", "Nguyễn Văn A"
        );

        assertTrue(result.isSuccess());
        assertEquals("Nguyễn Văn A", result.getUser().getFullName());
    }
}
