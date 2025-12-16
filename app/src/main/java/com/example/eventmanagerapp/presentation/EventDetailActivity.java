package com.example.eventmanagerapp.presentation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.domain.usecase.DeleteEventUseCase;
import com.example.eventmanagerapp.domain.usecase.GetEventsUseCase;
import com.example.eventmanagerapp.utils.DateTimeHelper;

/**
 * EventDetailActivity - Chỉ lo UI
 */
public class EventDetailActivity extends AppCompatActivity {

    // Views
    private TextView tvTitle, tvDateTime, tvNote, tvRemind;
    private ImageButton btnClose;
    private Button btnEdit, btnDelete;

    // Data
    private Event event;
    private int eventId;

    // Use Cases
    private GetEventsUseCase getEventsUseCase;
    private DeleteEventUseCase deleteEventUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initViews();
        initData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventAndShow();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvNote = findViewById(R.id.tvNote);
        tvRemind = findViewById(R.id.tvRemind);
        btnClose = findViewById(R.id.btnClose);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void initData() {
        getEventsUseCase = new GetEventsUseCase(this);
        deleteEventUseCase = new DeleteEventUseCase(this);

        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Thiếu event_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEventAndShow();
    }

    private void loadEventAndShow() {
        event = getEventsUseCase.getEventById(eventId);

        if (event == null) {
            Toast.makeText(this, "Sự kiện không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showEventDetail();
    }

    private void showEventDetail() {
        tvTitle.setText(event.getTitle());

        // Note
        String note = event.getNote();
        if (note == null || note.trim().isEmpty()) {
            tvNote.setText("(Không có ghi chú)");
        } else {
            tvNote.setText(note);
        }

        // Date & Time
        String dateText = DateTimeHelper.formatDisplayDate(event.getStartTime());
        String timeText = DateTimeHelper.formatTimeRange(
                event.getStartTime(),
                event.getEndTime()
        );

        tvDateTime.setText(dateText + "\n" + timeText);

        // Remind Before
        int remindBefore = event.getRemindBefore();
        String remindText = formatRemindText(remindBefore);
        tvRemind.setText("Nhắc nhở: " + remindText);
    }

    /**
     * Format remind before thành text hiển thị
     */
    private String formatRemindText(int minutes) {
        if (minutes == 0) return "Đúng giờ";
        if (minutes < 60) return minutes + " phút trước";
        if (minutes < 1440) return (minutes / 60) + " giờ trước";
        return (minutes / 1440) + " ngày trước";
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditEventActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    /* ========== DELETE ========== */

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá sự kiện")
                .setMessage("Bạn có chắc muốn xoá sự kiện này?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteEvent())
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteEvent() {
        DeleteEventUseCase.Result result = deleteEventUseCase.execute(eventId);

        if (result.isSuccess()) {
            Toast.makeText(this, "Đã xoá sự kiện", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}