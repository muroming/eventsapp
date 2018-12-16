package com.f.events.eventapp.Presentation.EventsList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.f.events.eventapp.Data.EventDAO;

import java.util.ArrayList;
import java.util.List;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventItem> {

    private Context context;
    private List<EventDAO> events = new ArrayList<>();

    public EventsRecyclerAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public EventItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return EventItem.create(viewGroup, context);
    }

    @Override
    public void onBindViewHolder(@NonNull EventItem eventItem, int position) {
        EventDAO event = events.get(position);
        eventItem.bindItem(event);
    }

    public void addEvents(@NonNull List<EventDAO> events){
        this.events.clear();
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
