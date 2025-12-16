package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.SessionManager;
import com.example.eventmanagerapp.utils.Validator;

/**
 * Use Case - Cập nhật Event
 * ✅ Đã check quyền sở hữu event trước khi update
 */
public class UpdateEventUseCase {

    private final EventRepository repository;
    private final AlarmScheduler alarmScheduler;
    private final SessionManager sessionManager;  // ✅ THÊM

    public UpdateEventUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.alarmScheduler = new AlarmScheduler(context);
        this.sessionManager = new SessionManager(context);  // ✅ THÊM
    }

    /**
     * Cập nhật event
     */
    public Result execute(int eventId, String title, String note,
                          long startMillis, long endMillis, int remindBefore) {

        // 0. ✅ Lấy userId hiện tại
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return Result.error("Vui lòng đăng nhập lại");
        }

        // 1. Validate
        String error = validateInput(title, startMillis, endMillis);
        if (error != null) {
            return Result.error(error);
        }

        // 2. Get existing event
        Event event = repository.getEventById(eventId);
        if (event == null) {
            return Result.error("Sự kiện không tồn tại");
        }

        // 3. ✅ Check quyền sở hữu
        if (event.getUserId() != userId) {
            return Result.error("Bạn không có quyền sửa sự kiện này");
        }

        // 4. Update event data
        event.setTitle(title);
        event.setNote(note);
        event.setStartTime(startMillis);
        event.setEndTime(endMillis);
        event.setRemindBefore(remindBefore);
        // userId giữ nguyên, không cần set lại

        // 5. Save to DB
        boolean updated = repository.updateEvent(event);
        if (!updated) {
            return Result.error("Không thể cập nhật sự kiện");
        }

        // 6. Reschedule alarm nếu thời gian chưa qua
        long alarmTime = startMillis - (remindBefore * 60 * 1000L);
        if (alarmTime > System.currentTimeMillis()) {
            alarmScheduler.rescheduleAlarm(eventId, title, alarmTime);
        }

        return Result.success();
    }

    private String validateInput(String title, long startMillis, long endMillis) {
        String error = Validator.validateTitle(title);
        if (error != null) return error;

        error = Validator.validateTimeRange(startMillis, endMillis);
        if (error != null) return error;

        return null;
    }

    /**
     * Result class
     */
    public static class Result {
        private final boolean success;
        private final String errorMessage;

        private Result(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static Result success() {
            return new Result(true, null);
        }

        public static Result error(String message) {
            return new Result(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}