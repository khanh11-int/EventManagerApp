package com.example.eventmanagerapp.presentation.main;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.domain.usecase.GetEventsUseCase;
import com.example.eventmanagerapp.presentation.add.AddEventActivity;
import com.example.eventmanagerapp.ui.EventCardFactory;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.DateTimeHelper;

import java.util.Calendar;
import java.util.List;

/**
 * MainActivity - Chỉ lo UI và tương tác user
 * Logic đã tách ra Helper và UseCase
 */
public class MainActivity extends AppCompatActivity {

    // Views
    private EditText edtDate;
    private ImageButton btnPickDate;
    private Button btnAddEvent;

    private TextView[] headerDays = new TextView[7];
    private LinearLayout[] morningCells = new LinearLayout[7];
    private LinearLayout[] afternoonCells = new LinearLayout[7];

    // Data
    private Calendar selectedDate;
    private Calendar weekStart;
    private String pendingDateToCreate; // Lưu ngày đang chờ tạo event

    // Use Case & Utils
    private GetEventsUseCase getEventsUseCase;
    private AlarmScheduler alarmScheduler;

    // Request codes
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        setupListeners();
        requestNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderWeek();

        // ✅ Nếu có pending date và đã có quyền → mở AddEventActivity
        if (pendingDateToCreate != null) {
            if (alarmScheduler.canScheduleExactAlarms()) {
                openAddEventActivity(pendingDateToCreate);
                pendingDateToCreate = null;
            }
        }
    }

    private void initViews() {
        edtDate = findViewById(R.id.edtDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        bindWeekCalendarViews();
    }

    private void bindWeekCalendarViews() {
        // Headers
        headerDays[0] = findViewById(R.id.headerDay1);
        headerDays[1] = findViewById(R.id.headerDay2);
        headerDays[2] = findViewById(R.id.headerDay3);
        headerDays[3] = findViewById(R.id.headerDay4);
        headerDays[4] = findViewById(R.id.headerDay5);
        headerDays[5] = findViewById(R.id.headerDay6);
        headerDays[6] = findViewById(R.id.headerDay7);

        // Morning cells
        morningCells[0] = findViewById(R.id.morningCell1);
        morningCells[1] = findViewById(R.id.morningCell2);
        morningCells[2] = findViewById(R.id.morningCell3);
        morningCells[3] = findViewById(R.id.morningCell4);
        morningCells[4] = findViewById(R.id.morningCell5);
        morningCells[5] = findViewById(R.id.morningCell6);
        morningCells[6] = findViewById(R.id.morningCell7);

        // Afternoon cells
        afternoonCells[0] = findViewById(R.id.afternoonCell1);
        afternoonCells[1] = findViewById(R.id.afternoonCell2);
        afternoonCells[2] = findViewById(R.id.afternoonCell3);
        afternoonCells[3] = findViewById(R.id.afternoonCell4);
        afternoonCells[4] = findViewById(R.id.afternoonCell5);
        afternoonCells[5] = findViewById(R.id.afternoonCell6);
        afternoonCells[6] = findViewById(R.id.afternoonCell7);
    }

    private void initData() {
        getEventsUseCase = new GetEventsUseCase(this);
        alarmScheduler = new AlarmScheduler(this);

        selectedDate = Calendar.getInstance();
        weekStart = DateTimeHelper.getWeekStart(selectedDate);

        edtDate.setText(DateTimeHelper.formatDisplayDate(selectedDate));
        renderWeek();
    }

    private void setupListeners() {
        edtDate.setOnClickListener(v -> openDatePicker());
        btnPickDate.setOnClickListener(v -> openDatePicker());

        // ✅ CHECK QUYỀN TRƯỚC KHI MỞ ADD EVENT
        btnAddEvent.setOnClickListener(v -> {
            String dateTag = DateTimeHelper.formatTagDate(selectedDate);
            checkPermissionAndOpenAddEvent(dateTag);
        });
    }

    /* ========== DATE PICKER ========== */

    private void openDatePicker() {
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    edtDate.setText(DateTimeHelper.formatDisplayDate(selectedDate));
                    weekStart = DateTimeHelper.getWeekStart(selectedDate);
                    renderWeek();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /* ========== RENDER WEEK ========== */

    private void renderWeek() {
        renderHeaderAndCellTags();
        clearAllCells();
        loadEventsToCells();
    }

    private void renderHeaderAndCellTags() {
        Calendar cursor = (Calendar) weekStart.clone();

        for (int i = 0; i < 7; i++) {
            String dateTag = DateTimeHelper.formatTagDate(cursor);
            String headerText = DateTimeHelper.formatWeekHeader(cursor);

            headerDays[i].setText(headerText);

            morningCells[i].setTag(dateTag);
            afternoonCells[i].setTag(dateTag);

            // ✅ CHECK QUYỀN TRƯỚC KHI MỞ ADD EVENT
            final String finalDateTag = dateTag;
            morningCells[i].setOnClickListener(v -> checkPermissionAndOpenAddEvent(finalDateTag));
            afternoonCells[i].setOnClickListener(v -> checkPermissionAndOpenAddEvent(finalDateTag));

            cursor.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    /* ========== CHECK PERMISSION & OPEN ADD EVENT ========== */

    /**
     * ✅ KIỂM TRA QUYỀN TRƯỚC KHI MỞ ADD EVENT ACTIVITY
     */
    private void checkPermissionAndOpenAddEvent(String dateTag) {
        if (alarmScheduler.canScheduleExactAlarms()) {
            // ✅ Đã có quyền → Mở ngay
            openAddEventActivity(dateTag);
        } else {
            // ❌ Chưa có quyền → Lưu date và yêu cầu quyền
            pendingDateToCreate = dateTag;
            Toast.makeText(
                    this,
                    "Vui lòng bật quyền 'Alarms & reminders' để tạo sự kiện",
                    Toast.LENGTH_LONG
            ).show();
            alarmScheduler.openExactAlarmSettings();
        }
    }

    /**
     * Mở AddEventActivity với date đã chọn
     */
    private void openAddEventActivity(String dateTag) {
        Intent intent = new Intent(this, AddEventActivity.class);
        intent.putExtra("date", dateTag);
        startActivity(intent);
    }

    private void clearAllCells() {
        for (int i = 0; i < 7; i++) {
            morningCells[i].removeAllViews();
            afternoonCells[i].removeAllViews();
        }
    }

    private void loadEventsToCells() {
        List<Event> events = getEventsUseCase.getAllEvents();

        for (Event event : events) {
            String eventDate = DateTimeHelper.formatTagDate(event.getStartTime());
            boolean isMorning = DateTimeHelper.isMorning(event.getStartTime());

            LinearLayout[] targetRow = isMorning ? morningCells : afternoonCells;

            for (int i = 0; i < 7; i++) {
                String cellTag = (String) targetRow[i].getTag();
                if (eventDate.equals(cellTag)) {
                    targetRow[i].addView(EventCardFactory.create(this, event));
                }
            }
        }
    }

    /* ========== PERMISSION ========== */

    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }
}