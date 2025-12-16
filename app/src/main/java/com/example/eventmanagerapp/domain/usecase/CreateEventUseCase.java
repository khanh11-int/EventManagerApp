package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.DateTimeHelper;
import com.example.eventmanagerapp.utils.SessionManager;
import com.example.eventmanagerapp.utils.Validator;

import java.util.Calendar;

public class CreateEventUseCase {

    private final EventRepository repository;
    private final AlarmScheduler alarmScheduler;
    private final SessionManager sessionManager;

    public CreateEventUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.alarmScheduler = new AlarmScheduler(context);
        this.sessionManager = new SessionManager(context);
    }

    public Result execute(String title, String note, String dateTag,
                          int startHour, int startMinute,
                          int endHour, int endMinute,
                          int remindBefore) {

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return Result.error("Vui lòng đăng nhập lại");
        }

        String error = validateInput(title, dateTag, startHour, startMinute,
                endHour, endMinute);
        if (error != null) {
            return Result.error(error);
        }

        try {
            Calendar startCal = DateTimeHelper.createDateTime(dateTag, startHour, startMinute);
            Calendar endCal = DateTimeHelper.createDateTime(dateTag, endHour, endMinute);

            long startMillis = startCal.getTimeInMillis();
            long endMillis = endCal.getTimeInMillis();

            error = Validator.validateTimeRange(startMillis, endMillis);
            if (error != null) {
                return Result.error(error);
            }

            error = Validator.validateFutureTime(startMillis);
            if (error != null) {
                return Result.error(error);
            }

            Event event = new Event();
            event.setUserId(userId);
            event.setTitle(title);
            event.setNote(note);
            event.setStartTime(startMillis);
            event.setEndTime(endMillis);
            event.setRemindBefore(remindBefore);

            long eventId = repository.createEvent(event);
            if (eventId <= 0) {
                return Result.error("Không thể lưu sự kiện");
            }

            long alarmTime = startMillis - (remindBefore * 60 * 1000L);

            boolean alarmSet = alarmScheduler.scheduleAlarm(
                    (int) eventId,
                    title,
                    alarmTime
            );

            if (!alarmSet) {
                return Result.errorNeedPermission("Cần quyền 'Báo chính xác' để đặt nhắc nhở");
            }

            return Result.success((int) eventId);

        } catch (Exception e) {
            return Result.error("Lỗi: " + e.getMessage());
        }
    }

    private String validateInput(String title, String dateTag,
                                 int startHour, int startMinute,
                                 int endHour, int endMinute) {

        String error = Validator.validateTitle(title);
        if (error != null) return error;

        error = Validator.validateDateFormat(dateTag);
        if (error != null) return error;

        error = Validator.validateStartTime(startHour, startMinute);
        if (error != null) return error;

        error = Validator.validateEndTime(endHour, endMinute);
        if (error != null) return error;

        error = Validator.validateTimeRange(startHour, startMinute, endHour, endMinute);
        if (error != null) return error;

        return null;
    }

    public static class Result {
        private final boolean success;
        private final String errorMessage;
        private final int eventId;
        private final boolean needPermission;

        private Result(boolean success, String errorMessage, int eventId, boolean needPermission) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.eventId = eventId;
            this.needPermission = needPermission;
        }

        public static Result success(int eventId) {
            return new Result(true, null, eventId, false);
        }

        public static Result error(String message) {
            return new Result(false, message, -1, false);
        }

        public static Result errorNeedPermission(String message) {
            return new Result(false, message, -1, true);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public int getEventId() {
            return eventId;
        }

        public boolean needPermission() {
            return needPermission;
        }
    }
}