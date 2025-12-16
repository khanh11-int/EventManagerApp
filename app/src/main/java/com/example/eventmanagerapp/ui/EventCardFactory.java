package com.example.eventmanagerapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eventmanagerapp.presentation.EventDetailActivity;
import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.model.Event;

import java.util.Calendar;
import java.util.Locale;

/**
 * Factory tạo UI Event Card
 * -> Tách khỏi MainActivity cho gọn & dễ bảo trì
 */
public class EventCardFactory {

    private EventCardFactory() {
        // không cho new
    }

    public static LinearLayout create(Context context, Event event) {

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(12, 10, 12, 10);
        card.setBackgroundResource(R.drawable.bg_event_card);
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 10);
        card.setLayoutParams(lp);

        // ===== TIME =====
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTimeInMillis(event.getStartTime());
        end.setTimeInMillis(event.getEndTime());

        // ===== TITLE =====
        TextView tvTitle = new TextView(context);
        tvTitle.setText(event.getTitle());
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.parseColor("#111111"));

        // ===== TIME TEXT =====
        TextView tvTime = new TextView(context);
        tvTime.setText(String.format(
                Locale.getDefault(),
                "%02d:%02d - %02d:%02d",
                start.get(Calendar.HOUR_OF_DAY),
                start.get(Calendar.MINUTE),
                end.get(Calendar.HOUR_OF_DAY),
                end.get(Calendar.MINUTE)
        ));
        tvTime.setTextSize(12);
        tvTime.setTextColor(Color.parseColor("#444444"));

        card.addView(tvTitle);
        card.addView(tvTime);

        // ===== CLICK → DETAIL =====
        card.setOnClickListener(v -> {
            Intent i = new Intent(context, EventDetailActivity.class);
            i.putExtra("event_id", event.getId());
            context.startActivity(i);
        });

        return card;
    }
}
