package com.example.eventmanagerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

/**
 * Unit tests for SessionManager
 * Uses Robolectric for Android context testing
 */
@RunWith(RobolectricTestRunner.class)
public class SessionManagerTest {

    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        sessionManager = new SessionManager(context);
        sharedPreferences = context.getSharedPreferences("EventManagerSession", Context.MODE_PRIVATE);
    }

    @Test
    public void testCreateLoginSession() {
        sessionManager.createLoginSession(100, "testuser");

        assertTrue(sessionManager.isLoggedIn());
        assertEquals(100, sessionManager.getUserId());
        assertEquals("testuser", sessionManager.getUsername());
    }

    @Test
    public void testLogout() {
        // First login
        sessionManager.createLoginSession(100, "testuser");
        assertTrue(sessionManager.isLoggedIn());

        // Then logout
        sessionManager.logout();

        assertFalse(sessionManager.isLoggedIn());
        assertEquals(-1, sessionManager.getUserId());
        assertNull(sessionManager.getUsername());
    }

    @Test
    public void testIsLoggedIn_Default() {
        // Fresh SessionManager should not be logged in
        assertFalse(sessionManager.isLoggedIn());
    }

    @Test
    public void testGetUserId_Default() {
        // Default user ID should be -1
        assertEquals(-1, sessionManager.getUserId());
    }

    @Test
    public void testGetUsername_Default() {
        // Default username should be null
        assertNull(sessionManager.getUsername());
    }

    @Test
    public void testCreateLoginSession_OverwritePrevious() {
        // First login
        sessionManager.createLoginSession(100, "user1");
        assertEquals(100, sessionManager.getUserId());
        assertEquals("user1", sessionManager.getUsername());

        // Second login (overwrite)
        sessionManager.createLoginSession(200, "user2");
        assertEquals(200, sessionManager.getUserId());
        assertEquals("user2", sessionManager.getUsername());
    }

    @Test
    public void testPersistence() {
        // Login
        sessionManager.createLoginSession(100, "testuser");

        // Create new SessionManager instance (simulating app restart)
        SessionManager newSessionManager = new SessionManager(context);

        // Should still be logged in
        assertTrue(newSessionManager.isLoggedIn());
        assertEquals(100, newSessionManager.getUserId());
        assertEquals("testuser", newSessionManager.getUsername());
    }

    @Test
    public void testLogout_ClearsAllData() {
        sessionManager.createLoginSession(100, "testuser");
        sessionManager.logout();

        // All data should be cleared
        assertFalse(sharedPreferences.getBoolean("is_logged_in", false));
        assertEquals(-1, sharedPreferences.getInt("user_id", -1));
        assertNull(sharedPreferences.getString("username", null));
    }

    @Test
    public void testCreateLoginSession_WithZeroUserId() {
        sessionManager.createLoginSession(0, "testuser");

        assertTrue(sessionManager.isLoggedIn());
        assertEquals(0, sessionManager.getUserId());
    }

    @Test
    public void testCreateLoginSession_WithNegativeUserId() {
        sessionManager.createLoginSession(-5, "testuser");

        assertTrue(sessionManager.isLoggedIn());
        assertEquals(-5, sessionManager.getUserId());
    }

    @Test
    public void testCreateLoginSession_WithEmptyUsername() {
        sessionManager.createLoginSession(100, "");

        assertTrue(sessionManager.isLoggedIn());
        assertEquals("", sessionManager.getUsername());
    }

    @Test
    public void testCreateLoginSession_WithSpecialCharacters() {
        sessionManager.createLoginSession(100, "user@#$123");

        assertEquals("user@#$123", sessionManager.getUsername());
    }

    @Test
    public void testMultipleLogoutCalls() {
        sessionManager.createLoginSession(100, "testuser");
        
        sessionManager.logout();
        assertFalse(sessionManager.isLoggedIn());
        
        // Second logout should not cause issues
        sessionManager.logout();
        assertFalse(sessionManager.isLoggedIn());
    }
}
