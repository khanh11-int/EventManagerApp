package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Unit tests for CreateEventUseCase
 */
@RunWith(RobolectricTestRunner.class)
public class CreateEventUseCaseTest {

    private CreateEventUseCase createEventUseCase;
    private Context context;
    private EventRepository repository;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        createEventUseCase = new CreateEventUseCase(context);
        repository = EventRepository.getInstance(context);
        sessionManager = new SessionManager(context);
        
        // Clear database
        repository.clearAll();
        
        // Login a test user
        sessionManager.createLoginSession(100, "testuser");
    }

    @Test
    public void testExecute_SuccessfulEventCreation() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                "Important meeting",
                dateTag,
                9, 0,
                10, 30,
                15
        );

        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
    }

    @Test
    public void testExecute_EmptyTitle() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "",
                "Note",
                dateTag,
                9, 0,
                10, 30,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng nhập tiêu đề", result.getErrorMessage());
    }

    @Test
    public void testExecute_TitleTooShort() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "AB",
                "Note",
                dateTag,
                9, 0,
                10, 30,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Tiêu đề phải có ít nhất 3 ký tự", result.getErrorMessage());
    }

    @Test
    public void testExecute_InvalidStartTime() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                "Note",
                dateTag,
                -1, -1,
                10, 30,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng chọn giờ bắt đầu", result.getErrorMessage());
    }

    @Test
    public void testExecute_InvalidEndTime() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                "Note",
                dateTag,
                9, 0,
                -1, -1,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng chọn giờ kết thúc", result.getErrorMessage());
    }

    @Test
    public void testExecute_EndTimeBeforeStartTime() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                "Note",
                dateTag,
                10, 30,
                9, 0,  // End before start
                15
        );

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("sau"));
    }

    @Test
    public void testExecute_PastTime() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        String dateTag = String.format("%04d-%02d-%02d",
                yesterday.get(Calendar.YEAR),
                yesterday.get(Calendar.MONTH) + 1,
                yesterday.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                "Note",
                dateTag,
                9, 0,
                10, 30,
                15
        );

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("qua"));
    }

    @Test
    public void testExecute_NotLoggedIn() {
        // Logout
        sessionManager.logout();

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                "Note",
                dateTag,
                9, 0,
                10, 30,
                15
        );

        assertFalse(result.isSuccess());
        assertEquals("Vui lòng đăng nhập lại", result.getErrorMessage());
    }

    @Test
    public void testExecute_ZeroRemindBefore() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                "Note",
                dateTag,
                9, 0,
                10, 30,
                0  // Remind at exact time
        );

        // Should succeed or ask for permission
        assertTrue(result.isSuccess() || result.needPermission());
    }

    @Test
    public void testExecute_NullNote() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String dateTag = String.format("%04d-%02d-%02d",
                tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH) + 1,
                tomorrow.get(Calendar.DAY_OF_MONTH));

        CreateEventUseCase.Result result = createEventUseCase.execute(
                "Meeting",
                null,  // null note
                dateTag,
                9, 0,
                10, 30,
                15
        );

        // Should succeed (note is optional)
        assertTrue(result.isSuccess() || result.needPermission());
    }
}
