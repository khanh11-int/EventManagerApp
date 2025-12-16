package com.example.eventmanagerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.presentation.EventDetailActivity;
import com.example.eventmanagerapp.utils.DateTimeHelper;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> data = new ArrayList<>();

    public void setData(List<Event> newData) {
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }

    public void addEvent(Event e) {
        data.add(e);
        notifyItemInserted(data.size() - 1);
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = data.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvTime;
        private final Context context;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(Event event) {
            tvTitle.setText(event.getTitle());

            String timeText = DateTimeHelper.formatTimeRange(
                    event.getStartTime(),
                    event.getEndTime()
            );
            tvTime.setText(timeText);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("event_id", event.getId());
                context.startActivity(intent);
            });
        }
    }
}