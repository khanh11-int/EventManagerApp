package com.example.eventmanagerapp.data.repository;

import android.content.Context;

import com.example.eventmanagerapp.data.local.EventDao;
import com.example.eventmanagerapp.domain.model.Event;

import java.util.List;

public class EventRepository {

    private final EventDao eventDao;

    private static EventRepository instance;

    private EventRepository(Context context) {
        this.eventDao = new EventDao(context);
    }

    public static synchronized EventRepository getInstance(Context context) {
        if (instance == null) {
            instance = new EventRepository(context.getApplicationContext());
        }
        return instance;
    }

    public long createEvent(Event event) {
        return eventDao.insert(event);
    }

    public boolean updateEvent(Event event) {
        return eventDao.update(event) > 0;
    }

    public boolean deleteEvent(int eventId) {
        return eventDao.delete(eventId) > 0;
    }

    public Event getEventById(int eventId) {
        return eventDao.getById(eventId);
    }

    public List<Event> getEventsByUserId(int userId) {
        return eventDao.getAllByUserId(userId);
    }

    public List<Event> getAllEvents() {
        return eventDao.getAll();
    }

    public void clearAllByUserId(int userId) {
        eventDao.deleteAllByUserId(userId);
    }

    public void clearAll() {
        eventDao.deleteAll();
    }
}