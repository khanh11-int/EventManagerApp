package com.example.eventmanagerapp.data.repository;

import android.content.Context;

import com.example.eventmanagerapp.domain.model.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for EventRepository
 */
@RunWith(RobolectricTestRunner.class)
public class EventRepositoryTest {

    private EventRepository repository;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        repository = EventRepository.getInstance(context);
        
        // Clear database before each test
        repository.clearAll();
    }

    @Test
    public void testGetInstance_Singleton() {
        EventRepository instance1 = EventRepository.getInstance(context);
        EventRepository instance2 = EventRepository.getInstance(context);

        assertSame(instance1, instance2);
    }

    @Test
    public void testCreateEvent_Success() {
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Test Event");
        event.setNote("Test Note");
        event.setStartTime(System.currentTimeMillis());
        event.setEndTime(System.currentTimeMillis() + 3600000);
        event.setRemindBefore(15);

        long eventId = repository.createEvent(event);

        assertTrue(eventId > 0);
    }

    @Test
    public void testGetEventById_Exists() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Test Event");
        event.setNote("Test Note");
        event.setStartTime(System.currentTimeMillis());
        event.setEndTime(System.currentTimeMillis() + 3600000);
        event.setRemindBefore(15);
        long eventId = repository.createEvent(event);

        // Retrieve the event
        Event retrievedEvent = repository.getEventById((int) eventId);

        assertNotNull(retrievedEvent);
        assertEquals((int) eventId, retrievedEvent.getId());
        assertEquals("Test Event", retrievedEvent.getTitle());
        assertEquals("Test Note", retrievedEvent.getNote());
        assertEquals(100, retrievedEvent.getUserId());
    }

    @Test
    public void testGetEventById_NotExists() {
        Event event = repository.getEventById(999);

        assertNull(event);
    }

    @Test
    public void testUpdateEvent_Success() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setNote("Original Note");
        event.setStartTime(System.currentTimeMillis());
        event.setEndTime(System.currentTimeMillis() + 3600000);
        event.setRemindBefore(15);
        long eventId = repository.createEvent(event);
        event.setId((int) eventId);

        // Update the event
        event.setTitle("Updated Title");
        event.setNote("Updated Note");
        event.setRemindBefore(30);
        boolean updated = repository.updateEvent(event);

        assertTrue(updated);

        // Verify the update
        Event updatedEvent = repository.getEventById((int) eventId);
        assertEquals("Updated Title", updatedEvent.getTitle());
        assertEquals("Updated Note", updatedEvent.getNote());
        assertEquals(30, updatedEvent.getRemindBefore());
    }

    @Test
    public void testUpdateEvent_NotExists() {
        Event event = new Event();
        event.setId(999);
        event.setTitle("Title");
        event.setStartTime(System.currentTimeMillis());
        event.setEndTime(System.currentTimeMillis() + 3600000);

        boolean updated = repository.updateEvent(event);

        assertFalse(updated);
    }

    @Test
    public void testDeleteEvent_Success() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Test Event");
        event.setStartTime(System.currentTimeMillis());
        event.setEndTime(System.currentTimeMillis() + 3600000);
        long eventId = repository.createEvent(event);

        // Delete the event
        boolean deleted = repository.deleteEvent((int) eventId);

        assertTrue(deleted);

        // Verify deletion
        Event deletedEvent = repository.getEventById((int) eventId);
        assertNull(deletedEvent);
    }

    @Test
    public void testDeleteEvent_NotExists() {
        boolean deleted = repository.deleteEvent(999);

        assertFalse(deleted);
    }

    @Test
    public void testGetEventsByUserId_EmptyList() {
        List<Event> events = repository.getEventsByUserId(100);

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetEventsByUserId_SingleUser() {
        // Create events for user 100
        for (int i = 1; i <= 3; i++) {
            Event event = new Event();
            event.setUserId(100);
            event.setTitle("Event " + i);
            event.setStartTime(System.currentTimeMillis());
            event.setEndTime(System.currentTimeMillis() + 3600000);
            repository.createEvent(event);
        }

        List<Event> events = repository.getEventsByUserId(100);

        assertNotNull(events);
        assertEquals(3, events.size());
        
        // Verify all events belong to user 100
        for (Event event : events) {
            assertEquals(100, event.getUserId());
        }
    }

    @Test
    public void testGetEventsByUserId_MultipleUsers() {
        // Create events for user 100
        Event event1 = new Event();
        event1.setUserId(100);
        event1.setTitle("User 100 Event 1");
        event1.setStartTime(System.currentTimeMillis());
        event1.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event1);

        Event event2 = new Event();
        event2.setUserId(100);
        event2.setTitle("User 100 Event 2");
        event2.setStartTime(System.currentTimeMillis());
        event2.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event2);

        // Create events for user 200
        Event event3 = new Event();
        event3.setUserId(200);
        event3.setTitle("User 200 Event");
        event3.setStartTime(System.currentTimeMillis());
        event3.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event3);

        // Get events for user 100
        List<Event> user100Events = repository.getEventsByUserId(100);
        assertEquals(2, user100Events.size());

        // Get events for user 200
        List<Event> user200Events = repository.getEventsByUserId(200);
        assertEquals(1, user200Events.size());
    }

    @Test
    public void testGetAllEvents() {
        // Create events for multiple users
        Event event1 = new Event();
        event1.setUserId(100);
        event1.setTitle("Event 1");
        event1.setStartTime(System.currentTimeMillis());
        event1.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event1);

        Event event2 = new Event();
        event2.setUserId(200);
        event2.setTitle("Event 2");
        event2.setStartTime(System.currentTimeMillis());
        event2.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event2);

        List<Event> allEvents = repository.getAllEvents();

        assertNotNull(allEvents);
        assertEquals(2, allEvents.size());
    }

    @Test
    public void testClearAllByUserId() {
        // Create events for user 100
        Event event1 = new Event();
        event1.setUserId(100);
        event1.setTitle("User 100 Event 1");
        event1.setStartTime(System.currentTimeMillis());
        event1.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event1);

        Event event2 = new Event();
        event2.setUserId(100);
        event2.setTitle("User 100 Event 2");
        event2.setStartTime(System.currentTimeMillis());
        event2.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event2);

        // Create events for user 200
        Event event3 = new Event();
        event3.setUserId(200);
        event3.setTitle("User 200 Event");
        event3.setStartTime(System.currentTimeMillis());
        event3.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event3);

        // Clear events for user 100
        repository.clearAllByUserId(100);

        // Verify user 100's events are cleared
        List<Event> user100Events = repository.getEventsByUserId(100);
        assertTrue(user100Events.isEmpty());

        // Verify user 200's events still exist
        List<Event> user200Events = repository.getEventsByUserId(200);
        assertEquals(1, user200Events.size());
    }

    @Test
    public void testClearAll() {
        // Create multiple events
        for (int i = 1; i <= 5; i++) {
            Event event = new Event();
            event.setUserId(100);
            event.setTitle("Event " + i);
            event.setStartTime(System.currentTimeMillis());
            event.setEndTime(System.currentTimeMillis() + 3600000);
            repository.createEvent(event);
        }

        // Clear all events
        repository.clearAll();

        // Verify all events are cleared
        List<Event> allEvents = repository.getAllEvents();
        assertTrue(allEvents.isEmpty());
    }

    @Test
    public void testCreateMultipleEvents_DifferentIds() {
        Event event1 = new Event();
        event1.setUserId(100);
        event1.setTitle("Event 1");
        event1.setStartTime(System.currentTimeMillis());
        event1.setEndTime(System.currentTimeMillis() + 3600000);
        long id1 = repository.createEvent(event1);

        Event event2 = new Event();
        event2.setUserId(100);
        event2.setTitle("Event 2");
        event2.setStartTime(System.currentTimeMillis());
        event2.setEndTime(System.currentTimeMillis() + 3600000);
        long id2 = repository.createEvent(event2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testUpdateEvent_PreservesOtherFields() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setNote("Original Note");
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 3600000;
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setRemindBefore(15);
        long eventId = repository.createEvent(event);
        event.setId((int) eventId);

        // Update only title
        event.setTitle("Updated Title");
        repository.updateEvent(event);

        // Verify other fields are preserved
        Event updatedEvent = repository.getEventById((int) eventId);
        assertEquals("Updated Title", updatedEvent.getTitle());
        assertEquals("Original Note", updatedEvent.getNote());
        assertEquals(100, updatedEvent.getUserId());
        assertEquals(15, updatedEvent.getRemindBefore());
    }
}
