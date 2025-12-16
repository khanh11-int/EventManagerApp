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

import static org.junit.Assert.*;

/**
 * Unit tests for DeleteEventUseCase
 */
@RunWith(RobolectricTestRunner.class)
public class DeleteEventUseCaseTest {

    private DeleteEventUseCase deleteEventUseCase;
    private Context context;
    private EventRepository repository;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        deleteEventUseCase = new DeleteEventUseCase(context);
        repository = EventRepository.getInstance(context);
        sessionManager = new SessionManager(context);
        
        // Clear database
        repository.clearAll();
        
        // Login a test user
        sessionManager.createLoginSession(100, "testuser");
    }

    @Test
    public void testExecute_SuccessfulDeletion() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Test Event");
        event.setNote("Note");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        event.setRemindBefore(15);
        long eventId = repository.createEvent(event);

        // Delete the event
        DeleteEventUseCase.Result result = deleteEventUseCase.execute((int) eventId);

        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        
        // Verify event is deleted
        Event deletedEvent = repository.getEventById((int) eventId);
        assertNull(deletedEvent);
    }

    @Test
    public void testExecute_EventNotExists() {
        DeleteEventUseCase.Result result = deleteEventUseCase.execute(999);

        assertFalse(result.isSuccess());
        assertEquals("Sự kiện không tồn tại", result.getErrorMessage());
    }

    @Test
    public void testExecute_NotLoggedIn() {
        // Logout
        sessionManager.logout();

        DeleteEventUseCase.Result result = deleteEventUseCase.execute(1);

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng đăng nhập lại", result.getErrorMessage());
    }

    @Test
    public void testExecute_NoPermission() {
        // Create an event owned by another user
        Event event = new Event();
        event.setUserId(200);  // Different user
        event.setTitle("Test Event");
        event.setNote("Note");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        event.setRemindBefore(15);
        long eventId = repository.createEvent(event);

        // Try to delete as user 100
        DeleteEventUseCase.Result result = deleteEventUseCase.execute((int) eventId);

        assertFalse(result.isSuccess());
        assertEquals("Bạn không có quyền xóa sự kiện này", result.getErrorMessage());
        
        // Verify event still exists
        Event existingEvent = repository.getEventById((int) eventId);
        assertNotNull(existingEvent);
    }

    @Test
    public void testExecute_MultipleEvents() {
        // Create multiple events
        Event event1 = new Event();
        event1.setUserId(100);
        event1.setTitle("Event 1");
        event1.setStartTime(System.currentTimeMillis() + 3600000);
        event1.setEndTime(System.currentTimeMillis() + 7200000);
        event1.setRemindBefore(15);
        long eventId1 = repository.createEvent(event1);

        Event event2 = new Event();
        event2.setUserId(100);
        event2.setTitle("Event 2");
        event2.setStartTime(System.currentTimeMillis() + 10800000);
        event2.setEndTime(System.currentTimeMillis() + 14400000);
        event2.setRemindBefore(15);
        long eventId2 = repository.createEvent(event2);

        // Delete one event
        DeleteEventUseCase.Result result = deleteEventUseCase.execute((int) eventId1);
        assertTrue(result.isSuccess());

        // Verify only one is deleted
        assertNull(repository.getEventById((int) eventId1));
        assertNotNull(repository.getEventById((int) eventId2));
    }
}
