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
 * Unit tests for UpdateEventUseCase
 */
@RunWith(RobolectricTestRunner.class)
public class UpdateEventUseCaseTest {

    private UpdateEventUseCase updateEventUseCase;
    private Context context;
    private EventRepository repository;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        updateEventUseCase = new UpdateEventUseCase(context);
        repository = EventRepository.getInstance(context);
        sessionManager = new SessionManager(context);
        
        // Clear database
        repository.clearAll();
        
        // Login a test user
        sessionManager.createLoginSession(100, "testuser");
    }

    @Test
    public void testExecute_SuccessfulUpdate() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setNote("Original Note");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        event.setRemindBefore(15);
        long eventId = repository.createEvent(event);

        // Update the event
        long newStartTime = System.currentTimeMillis() + 86400000; // +1 day
        long newEndTime = newStartTime + 3600000;
        
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                (int) eventId,
                "Updated Title",
                "Updated Note",
                newStartTime,
                newEndTime,
                30
        );

        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        
        // Verify the update
        Event updatedEvent = repository.getEventById((int) eventId);
        assertEquals("Updated Title", updatedEvent.getTitle());
        assertEquals("Updated Note", updatedEvent.getNote());
        assertEquals(30, updatedEvent.getRemindBefore());
    }

    @Test
    public void testExecute_EmptyTitle() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        long eventId = repository.createEvent(event);

        // Try to update with empty title
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                (int) eventId,
                "",
                "Note",
                System.currentTimeMillis() + 3600000,
                System.currentTimeMillis() + 7200000,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập tiêu đề", result.getErrorMessage());
    }

    @Test
    public void testExecute_TitleTooShort() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        long eventId = repository.createEvent(event);

        // Try to update with short title
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                (int) eventId,
                "AB",
                "Note",
                System.currentTimeMillis() + 3600000,
                System.currentTimeMillis() + 7200000,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Tiêu đề phải có ít nhất 3 ký tự", result.getErrorMessage());
    }

    @Test
    public void testExecute_EndTimeBeforeStartTime() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        long eventId = repository.createEvent(event);

        // Try to update with invalid time range
        long startTime = System.currentTimeMillis() + 7200000;
        long endTime = System.currentTimeMillis() + 3600000;  // Before start

        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                (int) eventId,
                "Title",
                "Note",
                startTime,
                endTime,
                15
        );

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("sau"));
    }

    @Test
    public void testExecute_EventNotExists() {
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                999,
                "Title",
                "Note",
                System.currentTimeMillis() + 3600000,
                System.currentTimeMillis() + 7200000,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Sự kiện không tồn tại", result.getErrorMessage());
    }

    @Test
    public void testExecute_NotLoggedIn() {
        // Logout
        sessionManager.logout();

        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                1,
                "Title",
                "Note",
                System.currentTimeMillis() + 3600000,
                System.currentTimeMillis() + 7200000,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng đăng nhập lại", result.getErrorMessage());
    }

    @Test
    public void testExecute_NoPermission() {
        // Create an event owned by another user
        Event event = new Event();
        event.setUserId(200);  // Different user
        event.setTitle("Original Title");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        long eventId = repository.createEvent(event);

        // Try to update as user 100
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                (int) eventId,
                "Updated Title",
                "Note",
                System.currentTimeMillis() + 3600000,
                System.currentTimeMillis() + 7200000,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Bạn không có quyền sửa sự kiện này", result.getErrorMessage());
        
        // Verify event was not modified
        Event unchangedEvent = repository.getEventById((int) eventId);
        assertEquals("Original Title", unchangedEvent.getTitle());
    }

    @Test
    public void testExecute_UserIdNotChanged() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        long eventId = repository.createEvent(event);

        // Update the event
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                (int) eventId,
                "Updated Title",
                "Note",
                System.currentTimeMillis() + 3600000,
                System.currentTimeMillis() + 7200000,
                15
        );

        assertTrue(result.isSuccess());
        
        // Verify userId remains the same
        Event updatedEvent = repository.getEventById((int) eventId);
        assertEquals(100, updatedEvent.getUserId());
    }

    @Test
    public void testExecute_NullNote() {
        // Create an event
        Event event = new Event();
        event.setUserId(100);
        event.setTitle("Original Title");
        event.setNote("Original Note");
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        long eventId = repository.createEvent(event);

        // Update with null note
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                (int) eventId,
                "Updated Title",
                null,
                System.currentTimeMillis() + 3600000,
                System.currentTimeMillis() + 7200000,
                15
        );

        assertTrue(result.isSuccess());
        
        // Verify note was updated to null
        Event updatedEvent = repository.getEventById((int) eventId);
        assertNull(updatedEvent.getNote());
    }
}
