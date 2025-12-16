package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.SessionManager;
import com.example.eventmanagerapp.utils.Validator;

public class UpdateEventUseCase {

    private final EventRepository repository;
    private final AlarmScheduler alarmScheduler;
    private final SessionManager sessionManager;

    public UpdateEventUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.alarmScheduler = new AlarmScheduler(context);
        this.sessionManager = new SessionManager(context);
    }

    public Result execute(int eventId, String title, String note,
                          long startMillis, long endMillis, int remindBefore) {

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return Result.error("Vui lòng đăng nhập lại");
        }

        String error = validateInput(title, startMillis, endMillis);
        if (error != null) {
            return Result.error(error);
        }

        Event event = repository.getEventById(eventId);
        if (event == null) {
            return Result.error("Sự kiện không tồn tại");
        }

        if (event.getUserId() != userId) {
            return Result.error("Bạn không có quyền sửa sự kiện này");
        }

        event.setTitle(title);
        event.setNote(note);
        event.setStartTime(startMillis);
        event.setEndTime(endMillis);
        event.setRemindBefore(remindBefore);

        boolean updated = repository.updateEvent(event);
        if (!updated) {
            return Result.error("Không thể cập nhật sự kiện");
        }

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