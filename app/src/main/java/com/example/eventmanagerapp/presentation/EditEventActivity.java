package com.example.eventmanagerapp.presentation;

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
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.domain.usecase.GetEventsUseCase;
import com.example.eventmanagerapp.domain.usecase.UpdateEventUseCase;
import com.example.eventmanagerapp.utils.DateTimeHelper;

import java.util.Calendar;

/**
 * EditEventActivity - Chỉ lo UI
 */
public class EditEventActivity extends AppCompatActivity {

    // Views
    private TextView tvDate;
    private EditText edtTitle, edtNote;
    private Button btnPickStart, btnPickEnd, btnSave;
    private Spinner spinnerRemind;

    // Data
    private Event event;
    private Calendar startCal, endCal;
    private int[] remindValues;

    // Use Cases
    private GetEventsUseCase getEventsUseCase;
    private UpdateEventUseCase updateEventUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

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
        String[] options = getResources().getStringArray(R.array.remind_options);
        remindValues = getResources().getIntArray(R.array.remind_values);

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRemind.setAdapter(adapter);
    }

    private void initData() {
        getEventsUseCase = new GetEventsUseCase(this);
        updateEventUseCase = new UpdateEventUseCase(this);

        int eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        event = getEventsUseCase.getEventById(eventId);
        if (event == null) {
            finish();
            return;
        }

        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        startCal.setTimeInMillis(event.getStartTime());
        endCal.setTimeInMillis(event.getEndTime());

        bindData();
    }

    private void bindData() {
        edtTitle.setText(event.getTitle());
        edtNote.setText(event.getNote());

        tvDate.setText("Ngày: " + DateTimeHelper.formatDisplayDate(startCal));

        updateTimeButtons();

        // Set spinner position dựa vào remindBefore
        int remindBefore = event.getRemindBefore();
        int position = findRemindPosition(remindBefore);
        spinnerRemind.setSelection(position);
    }

    /**
     * Tìm vị trí trong spinner tương ứng với remindBefore
     */
    private int findRemindPosition(int remindBefore) {
        for (int i = 0; i < remindValues.length; i++) {
            if (remindValues[i] == remindBefore) {
                return i;
            }
        }
        return 0; // Default: Đúng giờ
    }

    private void updateTimeButtons() {
        btnPickStart.setText("🕘 Bắt đầu: " + DateTimeHelper.formatTime(event.getStartTime()));
        btnPickEnd.setText("🕒 Kết thúc: " + DateTimeHelper.formatTime(event.getEndTime()));
    }

    private void setupListeners() {
        btnPickStart.setOnClickListener(v -> openStartTimePicker());
        btnPickEnd.setOnClickListener(v -> openEndTimePicker());
        btnSave.setOnClickListener(v -> saveEdit());
    }

    /* ========== TIME PICKER ========== */

    private void openStartTimePicker() {
        new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    startCal.set(Calendar.HOUR_OF_DAY, hour);
                    startCal.set(Calendar.MINUTE, minute);
                    event.setStartTime(startCal.getTimeInMillis());
                    updateTimeButtons();
                },
                startCal.get(Calendar.HOUR_OF_DAY),
                startCal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
        ).show();
    }

    private void openEndTimePicker() {
        new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    endCal.set(Calendar.HOUR_OF_DAY, hour);
                    endCal.set(Calendar.MINUTE, minute);
                    event.setEndTime(endCal.getTimeInMillis());
                    updateTimeButtons();
                },
                endCal.get(Calendar.HOUR_OF_DAY),
                endCal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
        ).show();
    }

    /* ========== SAVE ========== */

    private void saveEdit() {
        String title = edtTitle.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        // Lấy remind value
        int selectedPosition = spinnerRemind.getSelectedItemPosition();
        int remindBefore = remindValues[selectedPosition];

        // Gọi Use Case
        UpdateEventUseCase.Result result = updateEventUseCase.execute(
                event.getId(),
                title,
                note,
                startCal.getTimeInMillis(),
                endCal.getTimeInMillis(),
                remindBefore
        );

        if (result.isSuccess()) {
            Toast.makeText(this, "Đã cập nhật sự kiện", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}