package com.example.eventmanagerapp.utils;

public class Validator {

    public static String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Vui lòng nhập tiêu đề";
        }
        if (title.trim().length() < 3) {
            return "Tiêu đề phải có ít nhất 3 ký tự";
        }
        return null; // valid
    }

    public static String validateStartTime(int hour, int minute) {
        if (hour == -1 || minute == -1) {
            return "Vui lòng chọn giờ bắt đầu";
        }
        return null;
    }

    public static String validateEndTime(int hour, int minute) {
        if (hour == -1 || minute == -1) {
            return "Vui lòng chọn giờ kết thúc";
        }
        return null;
    }

    public static String validateTimeRange(int startHour, int startMinute,
                                           int endHour, int endMinute) {
        if (endHour < startHour) {
            return "Giờ kết thúc phải sau giờ bắt đầu";
        }
        if (endHour == startHour && endMinute <= startMinute) {
            return "Giờ kết thúc phải sau giờ bắt đầu";
        }
        return null;
    }

    public static String validateTimeRange(long startMillis, long endMillis) {
        if (endMillis <= startMillis) {
            return "Giờ kết thúc phải sau giờ bắt đầu";
        }
        return null;
    }

    public static String validateFutureTime(long timeMillis) {
        if (timeMillis <= System.currentTimeMillis()) {
            return "Giờ bắt đầu đã qua, không đặt nhắc nhở";
        }
        return null;
    }

    public static String validateDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return "Thiếu ngày";
        }

        String[] parts = date.split("-");
        if (parts.length != 3) {
            return "Ngày không hợp lệ";
        }

        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (year < 2020 || year > 2100) {
                return "Năm không hợp lệ";
            }
            if (month < 1 || month > 12) {
                return "Tháng không hợp lệ";
            }
            if (day < 1 || day > 31) {
                return "Ngày không hợp lệ";
            }
        } catch (NumberFormatException e) {
            return "Ngày không hợp lệ";
        }

        return null;
    }
}