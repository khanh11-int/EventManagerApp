package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Case - Lấy danh sách Events
 * ✅ Đã filter events theo userId
 */
public class GetEventsUseCase {

    private final EventRepository repository;
    private final SessionManager sessionManager;  // ✅ THÊM

    public GetEventsUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.sessionManager = new SessionManager(context);  // ✅ THÊM
    }

    /**
     * ✅ Lấy tất cả events của user hiện tại
     */
    public List<Event> getAllEvents() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return new ArrayList<>();  // Trả về list rỗng nếu chưa login
        }
        return repository.getEventsByUserId(userId);
    }

    /**
     * Lấy event theo ID (không cần filter userId vì ID là unique)
     */
    public Event getEventById(int eventId) {
        return repository.getEventById(eventId);
    }
}