package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.SessionManager;

/**
 * Use Case - Xoá Event
 * ✅ Đã check quyền sở hữu event trước khi xóa
 */
public class DeleteEventUseCase {

    private final EventRepository repository;
    private final AlarmScheduler alarmScheduler;
    private final SessionManager sessionManager;  // ✅ THÊM

    public DeleteEventUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.alarmScheduler = new AlarmScheduler(context);
        this.sessionManager = new SessionManager(context);  // ✅ THÊM
    }

    /**
     * Xoá event
     */
    public Result execute(int eventId) {
        // 0. ✅ Lấy userId hiện tại
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return Result.error("Vui lòng đăng nhập lại");
        }

        // 1. Get event để check quyền sở hữu
        Event event = repository.getEventById(eventId);
        if (event == null) {
            return Result.error("Sự kiện không tồn tại");
        }

        // 2. ✅ Check quyền sở hữu
        if (event.getUserId() != userId) {
            return Result.error("Bạn không có quyền xóa sự kiện này");
        }

        // 3. Cancel alarm trước
        alarmScheduler.cancelAlarm(eventId);

        // 4. Xoá khỏi DB
        boolean deleted = repository.deleteEvent(eventId);

        if (!deleted) {
            return Result.error("Không thể xoá sự kiện");
        }

        return Result.success();
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