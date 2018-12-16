package com.f.events.eventapp.Presentation.ProfileFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.FragmentInteractions;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment implements FragmentInteractions.OnBackPressListener, ProfileEventsAdapter.OnItemClicked {

    @BindView(R.id.rv_profile_events)
    RecyclerView mRecyclerEvents;

    @BindView(R.id.et_profile_name)
    EditText mNameEditText;

    @BindView(R.id.tv_profile_name)
    TextView mNameTextView;

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
        adapter.setListener(this);

        mRecyclerEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerEvents.setAdapter(adapter);
        mNameEditText.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                //todo update
                mNameTextView.setText(mNameEditText.getText().toString());
                mNameEditText.setVisibility(View.INVISIBLE);
                mNameTextView.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        getEvents();

        return v;
    }

    public void getEvents() {
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.iv_profile_edit_name)
    public void onEditClicked(View v) { //todo redo this
        mNameEditText.setVisibility(View.VISIBLE);
        mNameTextView.setVisibility(View.INVISIBLE);
        mNameEditText.setText("Никита Типун");
        mNameEditText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if (mNameEditText.getVisibility() == View.VISIBLE) {
            mNameTextView.setText(mNameEditText.getText().toString());
            mNameEditText.setVisibility(View.INVISIBLE);
            mNameTextView.setVisibility(View.VISIBLE);
        } else {
            ((MainActivity) getActivity()).backToMap();
        }
    }

    @Override
    public void onClick(EventDAO event) {
        //todo ((MainActivity) getActivity()).showEventInfoFragment(event);
        Toast.makeText(getContext(), event.getName(), Toast.LENGTH_SHORT).show();
    }
}
