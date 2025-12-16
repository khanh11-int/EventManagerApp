package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for GetEventsUseCase
 */
@RunWith(RobolectricTestRunner.class)
public class GetEventsUseCaseTest {

    private GetEventsUseCase getEventsUseCase;
    private Context context;
    private EventRepository repository;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        getEventsUseCase = new GetEventsUseCase(context);
        repository = EventRepository.getInstance(context);
        sessionManager = new SessionManager(context);
        
        // Clear database
        repository.clearAll();
        
        // Login a test user
        sessionManager.createLoginSession(100, "testuser");
    }

    @Test
    public void testGetAllEvents_EmptyList() {
        List<Event> events = getEventsUseCase.getAllEvents();

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetAllEvents_SingleEvent() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Test Event");
        event.setStartTime(System.currentTimeMillis());
        event.setEndTime(System.currentTimeMillis() + 3600000);
        event.setRemindBefore(15);
        repository.createEvent(event);

        List<Event> events = getEventsUseCase.getAllEvents();

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Test Event", events.get(0).getTitle());
    }

    @Test
    public void testGetAllEvents_MultipleEvents() {
        // Create multiple events
        for (int i = 1; i <= 5; i++) {
            Event event = new Event();
            event.setUserId(100);
            event.setTitle("Event " + i);
            event.setStartTime(System.currentTimeMillis());
            event.setEndTime(System.currentTimeMillis() + 3600000);
            event.setRemindBefore(15);
            repository.createEvent(event);
        }

        List<Event> events = getEventsUseCase.getAllEvents();

        assertNotNull(events);
        assertEquals(5, events.size());
    }

    @Test
    public void testGetAllEvents_FilterByUserId() {
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

        // Create events for another user
        Event event3 = new Event();
        event3.setUserId(200);
        event3.setTitle("User 200 Event");
        event3.setStartTime(System.currentTimeMillis());
        event3.setEndTime(System.currentTimeMillis() + 3600000);
        repository.createEvent(event3);

        // Get events for user 100
        List<Event> events = getEventsUseCase.getAllEvents();

        assertNotNull(events);
        assertEquals(2, events.size());  // Should only get user 100's events
        
        // Verify all events belong to user 100
        for (Event event : events) {
            assertEquals(100, event.getUserId());
        }
    }

    @Test
    public void testGetAllEvents_NotLoggedIn() {
        // Logout
        sessionManager.logout();

        List<Event> events = getEventsUseCase.getAllEvents();

        assertNotNull(events);
        assertTrue(events.isEmpty());  // Should return empty list
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

        Event retrievedEvent = getEventsUseCase.getEventById((int) eventId);

        assertNotNull(retrievedEvent);
        assertEquals("Test Event", retrievedEvent.getTitle());
        assertEquals("Test Note", retrievedEvent.getNote());
    }

    @Test
    public void testGetEventById_NotExists() {
        Event event = getEventsUseCase.getEventById(999);

        assertNull(event);
    }

    @Test
    public void testGetEventById_DifferentUser() {
        // Create an event for another user
        Event event = new Event();
        event.setUserId(200);
        event.setTitle("Other User Event");
        event.setStartTime(System.currentTimeMillis());
        event.setEndTime(System.currentTimeMillis() + 3600000);
        long eventId = repository.createEvent(event);

        // Should still be able to get by ID (no filtering in this method)
        Event retrievedEvent = getEventsUseCase.getEventById((int) eventId);

        assertNotNull(retrievedEvent);
        assertEquals(200, retrievedEvent.getUserId());
    }
}
