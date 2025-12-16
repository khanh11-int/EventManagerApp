package com.example.eventmanagerapp.domain.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for Event domain model
 */
public class EventTest {

    private Event event;

    @Before
    public void setUp() {
        event = new Event();
    }

    @Test
    public void testEventCreation_WithDefaultConstructor() {
        assertNotNull(event);
    }

    @Test
    public void testEventCreation_WithParameterizedConstructor() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 3600000; // +1 hour

        Event event = new Event(1, 100, "Meeting", "Important meeting",
                startTime, endTime, 15);

        assertEquals(1, event.getId());
        assertEquals(100, event.getUserId());
        assertEquals("Meeting", event.getTitle());
        assertEquals("Important meeting", event.getNote());
        assertEquals(startTime, event.getStartTime());
        assertEquals(endTime, event.getEndTime());
        assertEquals(15, event.getRemindBefore());
    }

    @Test
    public void testSetAndGetId() {
        event.setId(5);
        assertEquals(5, event.getId());
    }

    @Test
    public void testSetAndGetUserId() {
        event.setUserId(100);
        assertEquals(100, event.getUserId());
    }

    @Test
    public void testSetAndGetTitle() {
        event.setTitle("Test Event");
        assertEquals("Test Event", event.getTitle());
    }

    @Test
    public void testSetAndGetNote() {
        event.setNote("This is a test note");
        assertEquals("This is a test note", event.getNote());
    }

    @Test
    public void testSetAndGetStartTime() {
        long startTime = System.currentTimeMillis();
        event.setStartTime(startTime);
        assertEquals(startTime, event.getStartTime());
    }

    @Test
    public void testSetAndGetEndTime() {
        long endTime = System.currentTimeMillis();
        event.setEndTime(endTime);
        assertEquals(endTime, event.getEndTime());
    }

    @Test
    public void testSetAndGetRemindBefore() {
        event.setRemindBefore(30);
        assertEquals(30, event.getRemindBefore());
    }

    @Test
    public void testToString_ContainsBasicInfo() {
        event.setId(1);
        event.setUserId(100);
        event.setTitle("Test Event");

        String result = event.toString();

        assertTrue(result.contains("1"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("Test Event"));
    }

    @Test
    public void testNullTitle() {
        event.setTitle(null);
        assertNull(event.getTitle());
    }

    @Test
    public void testEmptyTitle() {
        event.setTitle("");
        assertEquals("", event.getTitle());
    }

    @Test
    public void testNullNote() {
        event.setNote(null);
        assertNull(event.getNote());
    }

    @Test
    public void testZeroRemindBefore() {
        event.setRemindBefore(0);
        assertEquals(0, event.getRemindBefore());
    }

    @Test
    public void testNegativeId() {
        event.setId(-1);
        assertEquals(-1, event.getId());
    }

    @Test
    public void testNegativeUserId() {
        event.setUserId(-1);
        assertEquals(-1, event.getUserId());
    }

    @Test
    public void testTimeRange_StartEqualsEnd() {
        long time = System.currentTimeMillis();
        event.setStartTime(time);
        event.setEndTime(time);
        assertEquals(event.getStartTime(), event.getEndTime());
    }

    @Test
    public void testTimeRange_EndBeforeStart() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime - 3600000; // -1 hour
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        assertTrue(event.getEndTime() < event.getStartTime());
    }
}
