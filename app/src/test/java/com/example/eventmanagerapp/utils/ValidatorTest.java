package com.example.eventmanagerapp.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for Validator utility class
 */
public class ValidatorTest {

    // ============ validateTitle Tests ============

    @Test
    public void testValidateTitle_ValidTitle() {
        String result = Validator.validateTitle("Meeting with team");
        assertNull(result); // null means valid
    }

    @Test
    public void testValidateTitle_MinimumLength() {
        String result = Validator.validateTitle("ABC");
        assertNull(result);
    }

    @Test
    public void testValidateTitle_NullTitle() {
        String result = Validator.validateTitle(null);
        assertNotNull(result);
        assertEquals("Vui lòng nhập tiêu đề", result);
    }

    @Test
    public void testValidateTitle_EmptyTitle() {
        String result = Validator.validateTitle("");
        assertNotNull(result);
        assertEquals("Vui lòng nhập tiêu đề", result);
    }

    @Test
    public void testValidateTitle_OnlyWhitespace() {
        String result = Validator.validateTitle("   ");
        assertNotNull(result);
        assertEquals("Vui lòng nhập tiêu đề", result);
    }

    @Test
    public void testValidateTitle_TooShort() {
        String result = Validator.validateTitle("AB");
        assertNotNull(result);
        assertEquals("Tiêu đề phải có ít nhất 3 ký tự", result);
    }

    @Test
    public void testValidateTitle_TwoCharactersWithSpaces() {
        String result = Validator.validateTitle("  AB  ");
        assertNotNull(result);
        assertEquals("Tiêu đề phải có ít nhất 3 ký tự", result);
    }

    // ============ validateStartTime Tests ============

    @Test
    public void testValidateStartTime_Valid() {
        String result = Validator.validateStartTime(10, 30);
        assertNull(result);
    }

    @Test
    public void testValidateStartTime_Midnight() {
        String result = Validator.validateStartTime(0, 0);
        assertNull(result);
    }

    @Test
    public void testValidateStartTime_InvalidHour() {
        String result = Validator.validateStartTime(-1, 30);
        assertNotNull(result);
        assertEquals("Vui lòng chọn giờ bắt đầu", result);
    }

    @Test
    public void testValidateStartTime_InvalidMinute() {
        String result = Validator.validateStartTime(10, -1);
        assertNotNull(result);
        assertEquals("Vui lòng chọn giờ bắt đầu", result);
    }

    @Test
    public void testValidateStartTime_BothInvalid() {
        String result = Validator.validateStartTime(-1, -1);
        assertNotNull(result);
        assertEquals("Vui lòng chọn giờ bắt đầu", result);
    }

    // ============ validateEndTime Tests ============

    @Test
    public void testValidateEndTime_Valid() {
        String result = Validator.validateEndTime(18, 45);
        assertNull(result);
    }

    @Test
    public void testValidateEndTime_InvalidHour() {
        String result = Validator.validateEndTime(-1, 45);
        assertNotNull(result);
        assertEquals("Vui lòng chọn giờ kết thúc", result);
    }

    @Test
    public void testValidateEndTime_InvalidMinute() {
        String result = Validator.validateEndTime(18, -1);
        assertNotNull(result);
        assertEquals("Vui lòng chọn giờ kết thúc", result);
    }

    @Test
    public void testValidateEndTime_BothInvalid() {
        String result = Validator.validateEndTime(-1, -1);
        assertNotNull(result);
        assertEquals("Vui lòng chọn giờ kết thúc", result);
    }

    // ============ validateTimeRange (Hour/Minute) Tests ============

    @Test
    public void testValidateTimeRange_Valid() {
        String result = Validator.validateTimeRange(9, 0, 10, 30);
        assertNull(result);
    }

    @Test
    public void testValidateTimeRange_SameHour_ValidMinutes() {
        String result = Validator.validateTimeRange(9, 0, 9, 30);
        assertNull(result);
    }

    @Test
    public void testValidateTimeRange_EndHourBefore() {
        String result = Validator.validateTimeRange(10, 0, 9, 0);
        assertNotNull(result);
        assertEquals("Giờ kết thúc phải sau giờ bắt đầu", result);
    }

    @Test
    public void testValidateTimeRange_SameHour_EndMinuteEqual() {
        String result = Validator.validateTimeRange(9, 30, 9, 30);
        assertNotNull(result);
        assertEquals("Giờ kết thúc phải sau giờ bắt đầu", result);
    }

    @Test
    public void testValidateTimeRange_SameHour_EndMinuteBefore() {
        String result = Validator.validateTimeRange(9, 30, 9, 15);
        assertNotNull(result);
        assertEquals("Giờ kết thúc phải sau giờ bắt đầu", result);
    }

    // ============ validateTimeRange (Millis) Tests ============

    @Test
    public void testValidateTimeRangeMillis_Valid() {
        long startMillis = System.currentTimeMillis();
        long endMillis = startMillis + 3600000; // +1 hour
        String result = Validator.validateTimeRange(startMillis, endMillis);
        assertNull(result);
    }

    @Test
    public void testValidateTimeRangeMillis_EndBeforeStart() {
        long startMillis = System.currentTimeMillis();
        long endMillis = startMillis - 3600000; // -1 hour
        String result = Validator.validateTimeRange(startMillis, endMillis);
        assertNotNull(result);
        assertEquals("Giờ kết thúc phải sau giờ bắt đầu", result);
    }

    @Test
    public void testValidateTimeRangeMillis_EndEqualsStart() {
        long time = System.currentTimeMillis();
        String result = Validator.validateTimeRange(time, time);
        assertNotNull(result);
        assertEquals("Giờ kết thúc phải sau giờ bắt đầu", result);
    }

    // ============ validateFutureTime Tests ============

    @Test
    public void testValidateFutureTime_FutureTime() {
        long futureTime = System.currentTimeMillis() + 86400000; // +1 day
        String result = Validator.validateFutureTime(futureTime);
        assertNull(result);
    }

    @Test
    public void testValidateFutureTime_PastTime() {
        long pastTime = System.currentTimeMillis() - 3600000; // -1 hour
        String result = Validator.validateFutureTime(pastTime);
        assertNotNull(result);
        assertEquals("Giờ bắt đầu đã qua, không đặt nhắc nhở", result);
    }

    @Test
    public void testValidateFutureTime_CurrentTime() {
        long currentTime = System.currentTimeMillis();
        String result = Validator.validateFutureTime(currentTime);
        assertNotNull(result); // Should be invalid as it's not in future
    }

    // ============ validateDateFormat Tests ============

    @Test
    public void testValidateDateFormat_Valid() {
        String result = Validator.validateDateFormat("2025-12-25");
        assertNull(result);
    }

    @Test
    public void testValidateDateFormat_ValidLeapYear() {
        String result = Validator.validateDateFormat("2024-02-29");
        assertNull(result);
    }

    @Test
    public void testValidateDateFormat_NullDate() {
        String result = Validator.validateDateFormat(null);
        assertNotNull(result);
        assertEquals("Thiếu ngày", result);
    }

    @Test
    public void testValidateDateFormat_EmptyDate() {
        String result = Validator.validateDateFormat("");
        assertNotNull(result);
        assertEquals("Thiếu ngày", result);
    }

    @Test
    public void testValidateDateFormat_WrongFormat() {
        String result = Validator.validateDateFormat("25/12/2025");
        assertNotNull(result);
        assertEquals("Ngày không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_InvalidYear() {
        String result = Validator.validateDateFormat("2019-12-25");
        assertNotNull(result);
        assertEquals("Năm không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_YearTooHigh() {
        String result = Validator.validateDateFormat("2101-12-25");
        assertNotNull(result);
        assertEquals("Năm không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_InvalidMonth() {
        String result = Validator.validateDateFormat("2025-13-25");
        assertNotNull(result);
        assertEquals("Tháng không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_MonthZero() {
        String result = Validator.validateDateFormat("2025-00-25");
        assertNotNull(result);
        assertEquals("Tháng không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_InvalidDay() {
        String result = Validator.validateDateFormat("2025-12-32");
        assertNotNull(result);
        assertEquals("Ngày không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_DayZero() {
        String result = Validator.validateDateFormat("2025-12-00");
        assertNotNull(result);
        assertEquals("Ngày không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_NonNumericValues() {
        String result = Validator.validateDateFormat("2025-AB-CD");
        assertNotNull(result);
        assertEquals("Ngày không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_IncompleteDate() {
        String result = Validator.validateDateFormat("2025-12");
        assertNotNull(result);
        assertEquals("Ngày không hợp lệ", result);
    }

    @Test
    public void testValidateDateFormat_ExtraParts() {
        String result = Validator.validateDateFormat("2025-12-25-extra");
        assertNotNull(result);
        assertEquals("Ngày không hợp lệ", result);
    }
}
