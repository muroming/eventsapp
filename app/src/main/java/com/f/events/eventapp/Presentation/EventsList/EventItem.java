package com.f.events.eventapp.Presentation.EventsList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.R;

public class EventItem extends RecyclerView.ViewHolder {

    Context context;
    TextView name;
    TextView about;
    TextView position;
    TextView date;

    public static EventItem create(@NonNull ViewGroup parent, Context context){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventItem(view, context);
    }

    private EventItem(@NonNull View itemView, Context context){
        super(itemView);
        this.context = context;
        findViews(itemView);
    }

    public void bindItem(@NonNull EventDAO event){
        name.setText(event.getName());
        about.setText(event.getDescription());
        date.setText(DateUtils.getRelativeDateTimeString(context, event.getEventTime().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_SHOW_YEAR));

    }

    private void findViews(View view){
        name = view.findViewById(R.id.tv_name);
        about = view.findViewById(R.id.tv_about);
        position = view.findViewById(R.id.tv_position);
        date = view.findViewById(R.id.tv_date);
    }
}
