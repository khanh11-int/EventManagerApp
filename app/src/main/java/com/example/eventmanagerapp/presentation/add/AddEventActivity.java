package com.example.eventmanagerapp.presentation.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.usecase.CreateEventUseCase;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.DateTimeHelper;
import com.example.eventmanagerapp.utils.Validator;

import java.util.Calendar;

/**
 * AddEventActivity - Chỉ lo UI và tương tác user
 * Logic đã tách ra UseCase
 */
public class AddEventActivity extends AppCompatActivity {

    // Views
    private TextView tvDate;
    private Button btnPickStart, btnPickEnd, btnSave;
    private EditText edtTitle, edtNote;
    private Spinner spinnerRemind;

    // Data
    private String selectedDate; // yyyy-MM-dd
    private int startHour = -1, startMinute = -1;
    private int endHour = -1, endMinute = -1;
    private int[] remindValues; // Mảng giá trị remind (phút)

    // Use Case
    private CreateEventUseCase createEventUseCase;
    private AlarmScheduler alarmScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        tvDate = findViewById(R.id.tvDate);
        btnPickStart = findViewById(R.id.btnPickStart);
        btnPickEnd = findViewById(R.id.btnPickEnd);
        btnSave = findViewById(R.id.btnSave);
        edtTitle = findViewById(R.id.edtTitle);
        edtNote = findViewById(R.id.edtNote);
        spinnerRemind = findViewById(R.id.spinnerRemind);

        // Setup Spinner
        setupRemindSpinner();
    }

    private void setupRemindSpinner() {
        // Load options từ arrays.xml
        String[] options = getResources().getStringArray(R.array.remind_options);
        remindValues = getResources().getIntArray(R.array.remind_values);

        // Tạo adapter
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRemind.setAdapter(adapter);

        // Default: Đúng giờ (index 0)
        spinnerRemind.setSelection(0);
    }

    private void initData() {
        createEventUseCase = new CreateEventUseCase(this);
        alarmScheduler = new AlarmScheduler(this);

        // Nhận ngày từ Intent
        selectedDate = getIntent().getStringExtra("date");

        String error = Validator.validateDateFormat(selectedDate);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDate.setText("Ngày: " + selectedDate);
    }

    private void setupListeners() {
        tvDate.setOnClickListener(v -> openDatePicker());
        btnPickStart.setOnClickListener(v -> openStartTimePicker());
        btnPickEnd.setOnClickListener(v -> openEndTimePicker());
        btnSave.setOnClickListener(v -> saveEvent());
    }

    /* ========== DATE PICKER ========== */

    private void openDatePicker() {
        try {
            Calendar cal = DateTimeHelper.parseTagDate(selectedDate);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        tvDate.setText("Ngày: " + selectedDate);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi định dạng ngày", Toast.LENGTH_SHORT).show();
        }
    }

    /* ========== TIME PICKER ========== */

    private void openStartTimePicker() {
        Calendar now = Calendar.getInstance();

        new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    startHour = hour;
                    startMinute = minute;
                    btnPickStart.setText("🕘 Bắt đầu: " + DateTimeHelper.formatTime(hour, minute));
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
        ).show();
    }

    private void openEndTimePicker() {
        int defaultHour = (startHour != -1) ? startHour : 8;
        int defaultMinute = (startMinute != -1) ? startMinute : 0;

        new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    endHour = hour;
                    endMinute = minute;
                    btnPickEnd.setText("🕒 Kết thúc: " + DateTimeHelper.formatTime(hour, minute));
                },
                defaultHour, defaultMinute,
                DateFormat.is24HourFormat(this)
        ).show();
    }

    /* ========== SAVE EVENT ========== */

    // AddEventActivity.java - saveEvent() method
    private void saveEvent() {
        String title = edtTitle.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        int selectedPosition = spinnerRemind.getSelectedItemPosition();
        int remindBefore = remindValues[selectedPosition];

        // Gọi Use Case (không cần lo về quyền nữa vì MainActivity đã check)
        CreateEventUseCase.Result result = createEventUseCase.execute(
                title, note, selectedDate,
                startHour, startMinute,
                endHour, endMinute,
                remindBefore
        );

        if (result.isSuccess()) {
            Toast.makeText(this, "Đã tạo sự kiện", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Chỉ hiển thị lỗi validation
            Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}