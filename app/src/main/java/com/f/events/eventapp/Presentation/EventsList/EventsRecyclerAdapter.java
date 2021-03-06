package com.f.events.eventapp.Presentation.EventsList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.Presentation.ProfileFragment.ProfileEventsAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventItem> {

    public interface OnClickInterface{
        void clicked(EventDAO event);
    }

    private Context context;
    private List<EventDAO> events = new ArrayList<>();
    private OnClickInterface listener;

    public EventsRecyclerAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public EventItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return EventItem.create(viewGroup, context);
    }

    public void setListener(OnClickInterface listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull EventItem eventItem, int position) {
        EventDAO event = events.get(position);
        eventItem.bindItem(event, listener);
    }

    public void addEvent(EventDAO event){
        this.events.add(event);
        notifyItemInserted(events.size() - 1);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
