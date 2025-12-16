package com.example.eventmanagerapp.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanagerapp.domain.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho 1 cell (Sáng/Chiều).
 * EventCardFactory: vẽ UI cho 1 event.
 * EventAdapter: quản lý danh sách event cho RecyclerView.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.Holder> {

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
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo "container" rỗng để nhét card vào
        ViewGroup container = new android.widget.FrameLayout(parent.getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new Holder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Event event = data.get(position);

        ViewGroup container = (ViewGroup) holder.itemView;
        container.removeAllViews();

        // Dùng lại UI hiện có của bạn
        View card = EventCardFactory.create(container.getContext(), event);
        container.addView(card);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
