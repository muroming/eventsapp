package com.f.events.eventapp.Presentation.ProfileFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.R;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileEventsAdapter extends RecyclerView.Adapter<ProfileEventsAdapter.ViewHolder> {

    public interface OnItemClicked {
        void onClick(EventDAO event);
    }

    private List<EventDAO> mEvents;
    private WeakReference<Context> mContext;
    private SimpleDateFormat mDateFormat;
    private OnItemClicked mListener;

    public ProfileEventsAdapter(Context mContext) {
        this.mEvents = new ArrayList<>();
        this.mContext = new WeakReference<>(mContext);
        mDateFormat = new SimpleDateFormat("EEE, d MMM HH:mm", Locale.getDefault());
    }

    public void setEvents(List<EventDAO> events) {
        this.mEvents = events;
    }

    public void setListener(OnItemClicked mListener) {
        this.mListener = mListener;
    }

    public void addItem(EventDAO event) {
        mEvents.add(event);
        notifyItemInserted(mEvents.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext.get()).inflate(R.layout.event_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        EventDAO event = mEvents.get(i);
        viewHolder.bind(event);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mAbout;
        private TextView mPosition;
        private TextView mDate;
        private View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mName = itemView.findViewById(R.id.tv_name);
            mAbout = itemView.findViewById(R.id.tv_about);
            mPosition = itemView.findViewById(R.id.tv_position);
            mDate = itemView.findViewById(R.id.tv_date);
        }

        void bind(EventDAO event) {
            mName.setText(event.getName());
            mAbout.setText(event.getDescription());
            mPosition.setText(event.getPosition().toString());  //todo convert
            mDate.setText(mDateFormat.format(event.getEventTime()));
            mView.setOnClickListener(v -> mListener.onClick(event));
        }
    }
}
