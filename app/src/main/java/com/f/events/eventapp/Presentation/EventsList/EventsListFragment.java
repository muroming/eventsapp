package com.f.events.eventapp.Presentation.EventsList;

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
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsListFragment extends Fragment implements EventsRecyclerAdapter.OnClickInterface {

    private static final String ID = "ID";

    private RecyclerView recyclerView;
    private View view;
    private Map<String, EventDAO> map;
    private EventsRecyclerAdapter adapter;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public static EventsListFragment newInstance(int id) {
        Bundle b = new Bundle();
        b.putInt(ID, id);
        EventsListFragment fragment = new EventsListFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.events_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.rv_events_list);
        adapter = new EventsRecyclerAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance();
        map = new HashMap<>();
        adapter.setListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setEvents();
    }

    public void setEvents() {
        String key = mUser.getUid();
        if (getArguments().getInt(ID, -1) == 0) {
            mDatabase.getReference("user_created_events").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> events = (List<String>) dataSnapshot.getValue();
                    if (events != null) {
                        for (String event : events) {
                            mDatabase.getReference("events").child(event)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getKey() != "test") {
                                                EventDAO e = dataSnapshot.getValue(EventDAO.class);
                                                adapter.addEvent(e);
                                                map.put(event, e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            mDatabase.getReference("users_events").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> events = (List<String>) dataSnapshot.getValue();
                    if (events != null) {
                        for (String event : events) {
                            mDatabase.getReference("events").child(event)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getKey() != "test") {
                                                EventDAO e = dataSnapshot.getValue(EventDAO.class);
                                                adapter.addEvent(e);
                                                map.put(event, e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void clicked(EventDAO event) {
        for (Map.Entry<String, EventDAO> e : map.entrySet()) {
            if (e.getValue().equals(event)) {
                ((MainActivity) getActivity()).showParticipants(e.getKey());
            }
        }
    }
}
