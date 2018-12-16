package com.f.events.eventapp.Presentation.ParticipantsFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.f.events.eventapp.Data.UserDAO;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.firebase.auth.internal.FederatedSignInActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventParticipantsFragment extends Fragment {

    public static final String KEY = "KEY";

    @BindView(R.id.rv_event_participants)
    RecyclerView mParticipatnsRecycler;

    private ParticipantsAdapter adapter;
    private FirebaseDatabase mDatabase;

    public static EventParticipantsFragment newInstance(String eventKey) {
        Bundle args = new Bundle();
        args.putString(KEY, eventKey);
        EventParticipantsFragment fragment = new EventParticipantsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_participants, container, false);
        ButterKnife.bind(this, v);
        mDatabase = FirebaseDatabase.getInstance();
        adapter = new ParticipantsAdapter(getContext());
        mParticipatnsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mParticipatnsRecycler.setAdapter(adapter);

        mDatabase.getReference("events_users").child(getArguments().getString(KEY)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> users = (List<String>) dataSnapshot.getValue();
                if (users == null) {
                    users = new ArrayList<>();
                }
                for (String user : users) {
                    mDatabase.getReference("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserDAO user = dataSnapshot.getValue(UserDAO.class);
                            if (user != null) {
                                Participant p = new Participant(user.getName(), user.getImageUri());
                                adapter.addParticipant(p);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }
}
