package com.example.eventmanagerapp.presentation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.domain.usecase.GetEventsUseCase;
import com.example.eventmanagerapp.presentation.auth.LoginActivity;
import com.example.eventmanagerapp.adapter.EventAdapter;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.DateTimeHelper;
import com.example.eventmanagerapp.utils.SessionManager;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtDate;
    private ImageButton btnPickDate, btnLogout;
    private Button btnAddEvent;

    private TextView[] headerDays = new TextView[7];

    private RecyclerView[] morningCells = new RecyclerView[7];
    private RecyclerView[] afternoonCells = new RecyclerView[7];

    private EventAdapter[] morningAdapters = new EventAdapter[7];
    private EventAdapter[] afternoonAdapters = new EventAdapter[7];

    private Calendar selectedDate;
    private Calendar weekStart;
    private String pendingDateToCreate;

    private GetEventsUseCase getEventsUseCase;
    private AlarmScheduler alarmScheduler;
    private SessionManager sessionManager;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

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
        btnLogout = findViewById(R.id.btnLogout);

        bindWeekCalendarViews();
    }

    private void bindWeekCalendarViews() {
        headerDays[0] = findViewById(R.id.headerDay1);
        headerDays[1] = findViewById(R.id.headerDay2);
        headerDays[2] = findViewById(R.id.headerDay3);
        headerDays[3] = findViewById(R.id.headerDay4);
        headerDays[4] = findViewById(R.id.headerDay5);
        headerDays[5] = findViewById(R.id.headerDay6);
        headerDays[6] = findViewById(R.id.headerDay7);

        morningCells[0] = findViewById(R.id.morningCell1);
        morningCells[1] = findViewById(R.id.morningCell2);
        morningCells[2] = findViewById(R.id.morningCell3);
        morningCells[3] = findViewById(R.id.morningCell4);
        morningCells[4] = findViewById(R.id.morningCell5);
        morningCells[5] = findViewById(R.id.morningCell6);
        morningCells[6] = findViewById(R.id.morningCell7);

        afternoonCells[0] = findViewById(R.id.afternoonCell1);
        afternoonCells[1] = findViewById(R.id.afternoonCell2);
        afternoonCells[2] = findViewById(R.id.afternoonCell3);
        afternoonCells[3] = findViewById(R.id.afternoonCell4);
        afternoonCells[4] = findViewById(R.id.afternoonCell5);
        afternoonCells[5] = findViewById(R.id.afternoonCell6);
        afternoonCells[6] = findViewById(R.id.afternoonCell7);

        for (int i = 0; i < 7; i++) {
            morningAdapters[i] = new EventAdapter();
            afternoonAdapters[i] = new EventAdapter();

            morningCells[i].setLayoutManager(new LinearLayoutManager(this));
            afternoonCells[i].setLayoutManager(new LinearLayoutManager(this));

            morningCells[i].setAdapter(morningAdapters[i]);
            afternoonCells[i].setAdapter(afternoonAdapters[i]);
        }
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

        btnAddEvent.setOnClickListener(v -> {
            String dateTag = DateTimeHelper.formatTagDate(selectedDate);
            checkPermissionAndOpenAddEvent(dateTag);
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        sessionManager.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

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

            final String finalDateTag = dateTag;
            morningCells[i].setOnClickListener(v -> checkPermissionAndOpenAddEvent(finalDateTag));
            afternoonCells[i].setOnClickListener(v -> checkPermissionAndOpenAddEvent(finalDateTag));

            cursor.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void clearAllCells() {
        for (int i = 0; i < 7; i++) {
            morningAdapters[i].clear();
            afternoonAdapters[i].clear();
        }
    }

    private void loadEventsToCells() {
        List<Event> events = getEventsUseCase.getAllEvents();

        for (Event event : events) {
            String eventDate = DateTimeHelper.formatTagDate(event.getStartTime());
            boolean isMorning = DateTimeHelper.isMorning(event.getStartTime());

            RecyclerView[] targetRow = isMorning ? morningCells : afternoonCells;
            EventAdapter[] targetAdapters = isMorning ? morningAdapters : afternoonAdapters;

            for (int i = 0; i < 7; i++) {
                String cellTag = (String) targetRow[i].getTag();
                if (eventDate.equals(cellTag)) {
                    targetAdapters[i].addEvent(event);
                }
            }
        }
    }

    private void checkPermissionAndOpenAddEvent(String dateTag) {
        if (alarmScheduler.canScheduleExactAlarms()) {
            openAddEventActivity(dateTag);
        } else {
            pendingDateToCreate = dateTag;
            Toast.makeText(
                    this,
                    "Vui lòng bật quyền 'Alarms & reminders' để tạo sự kiện",
                    Toast.LENGTH_LONG
            ).show();
            alarmScheduler.openExactAlarmSettings();
        }
    }

    private void openAddEventActivity(String dateTag) {
        Intent intent = new Intent(this, AddEventActivity.class);
        intent.putExtra("date", dateTag);
        startActivity(intent);
    }

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