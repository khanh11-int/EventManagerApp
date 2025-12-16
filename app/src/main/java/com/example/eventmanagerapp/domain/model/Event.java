package com.example.eventmanagerapp.domain.model;

/**
 * Domain Model - Event Entity
 * ✅ Đã thêm userId để phân biệt events của từng user
 */
public class Event {

    private int id;
    private int userId;         // ✅ THÊM userId
    private String title;
    private String note;
    private long startTime;     // millis
    private long endTime;       // millis
    private int remindBefore;   // phút nhắc trước (0 = đúng giờ)

    // Constructor
    public Event() {
    }

    public Event(int id, int userId, String title, String note,
                 long startTime, long endTime, int remindBefore) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.note = note;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remindBefore = remindBefore;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getRemindBefore() {
        return remindBefore;
    }

    public void setRemindBefore(int remindBefore) {
        this.remindBefore = remindBefore;
    }

    @Override
    public String toString() {
        return "Event{id=" + id + ", userId=" + userId + ", title='" + title + "'}";
    }
}