package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class GetEventsUseCase {

    private final EventRepository repository;
    private final SessionManager sessionManager;

    public GetEventsUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.sessionManager = new SessionManager(context);
    }

    public List<Event> getAllEvents() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return new ArrayList<>();
        }
        return repository.getEventsByUserId(userId);
    }

    public Event getEventById(int eventId) {
        return repository.getEventById(eventId);
    }
}