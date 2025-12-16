package com.example.eventmanagerapp.domain.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for User domain model
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void testUserCreation_WithDefaultConstructor() {
        assertNotNull(user);
    }

    @Test
    public void testUserCreation_WithParameterizedConstructor() {
        long createdAt = System.currentTimeMillis();
        User user = new User(1, "testuser", "password123", "Test User", createdAt);

        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("Test User", user.getFullName());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    public void testSetAndGetId() {
        user.setId(10);
        assertEquals(10, user.getId());
    }

    @Test
    public void testSetAndGetUsername() {
        user.setUsername("john_doe");
        assertEquals("john_doe", user.getUsername());
    }

    @Test
    public void testSetAndGetPassword() {
        user.setPassword("secure_password");
        assertEquals("secure_password", user.getPassword());
    }

    @Test
    public void testSetAndGetFullName() {
        user.setFullName("John Doe");
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    public void testSetAndGetCreatedAt() {
        long timestamp = System.currentTimeMillis();
        user.setCreatedAt(timestamp);
        assertEquals(timestamp, user.getCreatedAt());
    }

    @Test
    public void testToString_ContainsBasicInfo() {
        user.setId(1);
        user.setUsername("testuser");

        String result = user.toString();

        assertTrue(result.contains("1"));
        assertTrue(result.contains("testuser"));
    }

    @Test
    public void testNullUsername() {
        user.setUsername(null);
        assertNull(user.getUsername());
    }

    @Test
    public void testEmptyUsername() {
        user.setUsername("");
        assertEquals("", user.getUsername());
    }

    @Test
    public void testNullPassword() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    public void testEmptyPassword() {
        user.setPassword("");
        assertEquals("", user.getPassword());
    }

    @Test
    public void testNullFullName() {
        user.setFullName(null);
        assertNull(user.getFullName());
    }

    @Test
    public void testEmptyFullName() {
        user.setFullName("");
        assertEquals("", user.getFullName());
    }

    @Test
    public void testNegativeId() {
        user.setId(-1);
        assertEquals(-1, user.getId());
    }

    @Test
    public void testZeroCreatedAt() {
        user.setCreatedAt(0);
        assertEquals(0, user.getCreatedAt());
    }

    @Test
    public void testSpecialCharactersInUsername() {
        user.setUsername("user@#$%123");
        assertEquals("user@#$%123", user.getUsername());
    }

    @Test
    public void testSpecialCharactersInPassword() {
        user.setPassword("P@ssw0rd!");
        assertEquals("P@ssw0rd!", user.getPassword());
    }

    @Test
    public void testVietnameseCharactersInFullName() {
        user.setFullName("Nguyễn Văn A");
        assertEquals("Nguyễn Văn A", user.getFullName());
    }
}
