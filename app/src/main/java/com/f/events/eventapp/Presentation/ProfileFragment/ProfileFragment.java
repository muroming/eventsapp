package com.f.events.eventapp.Presentation.ProfileFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.rv_profile_events)
    RecyclerView mRecyclerEvents;

    private ProfileEventsAdapter adapter;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        adapter = new ProfileEventsAdapter(getContext());

        mRecyclerEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerEvents.setAdapter(adapter);

        getEvents();

        return v;
    }

    public void getEvents() {
        List<EventDAO> events = new ArrayList<>();
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        events.add(new EventDAO(new LatLng(30, 40), "Test event", "Test description", new Date(), new ArrayList<>()));
        adapter.setEvents(events);
        adapter.notifyDataSetChanged();
    }
}
