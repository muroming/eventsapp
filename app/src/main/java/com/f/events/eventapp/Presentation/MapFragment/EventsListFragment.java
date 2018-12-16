package com.f.events.eventapp.Presentation.MapFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsListFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static View view;
    private static List<EventDAO> events;
    private EventsRecyclerAdapter adapter;

    public static EventsListFragment newInstance(List<EventDAO> newEvents){
        events = newEvents;
        return new EventsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.events_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.rv_events_list);
        adapter = new EventsRecyclerAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setEvents();
        adapter.addEvents(events);
    }

    public void setEvents(){
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(),new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
    }
}
