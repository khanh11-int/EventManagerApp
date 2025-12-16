package com.example.eventmanagerapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeHelper {

    private static final SimpleDateFormat FORMAT_DISPLAY_DATE =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final SimpleDateFormat FORMAT_TAG_DATE =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String formatDisplayDate(Calendar calendar) {
        return FORMAT_DISPLAY_DATE.format(calendar.getTime());
    }

    public static String formatDisplayDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return formatDisplayDate(cal);
    }

    public static String formatTagDate(Calendar calendar) {
        return FORMAT_TAG_DATE.format(calendar.getTime());
    }

    public static String formatTagDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return formatTagDate(cal);
    }

    public static String formatTimeRange(long startMillis, long endMillis) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTimeInMillis(startMillis);
        end.setTimeInMillis(endMillis);

        return String.format(
                Locale.getDefault(),
                "%02d:%02d - %02d:%02d",
                start.get(Calendar.HOUR_OF_DAY),
                start.get(Calendar.MINUTE),
                end.get(Calendar.HOUR_OF_DAY),
                end.get(Calendar.MINUTE)
        );
    }

    public static String formatTime(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    public static String formatTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return formatTime(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
        );
    }

    public static Calendar parseTagDate(String dateTag) throws Exception {
        String[] parts = dateTag.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format");
        }

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // Calendar month starts from 0
        int day = Integer.parseInt(parts[2]);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }

    public static Calendar createDateTime(String dateTag, int hour, int minute)
            throws Exception {
        Calendar cal = parseTagDate(dateTag);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Thứ 2";
            case Calendar.TUESDAY: return "Thứ 3";
            case Calendar.WEDNESDAY: return "Thứ 4";
            case Calendar.THURSDAY: return "Thứ 5";
            case Calendar.FRIDAY: return "Thứ 6";
            case Calendar.SATURDAY: return "Thứ 7";
            case Calendar.SUNDAY: return "Chủ nhật";
            default: return "";
        }
    }

    public static boolean isMorning(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.get(Calendar.HOUR_OF_DAY) < 12;
    }

    public static Calendar getWeekStart(Calendar date) {
        Calendar weekStart = (Calendar) date.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return weekStart;
    }

    public static String formatWeekHeader(Calendar date) {
        return getDayName(date.get(Calendar.DAY_OF_WEEK)) +
                "\n" + formatDisplayDate(date);
    }
}