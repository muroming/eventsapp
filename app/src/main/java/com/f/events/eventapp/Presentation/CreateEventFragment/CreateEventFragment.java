package com.f.events.eventapp.Presentation.CreateEventFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.FragmentInteractions;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class CreateEventFragment extends Fragment implements FragmentInteractions.OnBackPressListener {

    public static final int PLACE_PICKER_REQUEST = 1;

    public static Fragment newInstance() {
        return new CreateEventFragment();
    }

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.et_meeting_name)
    EditText etMeetingName;

    @BindView(R.id.et_meeting_description)
    EditText etMeetingDescription;

    @BindView(R.id.meeting_date)
    TextView tvMeetingDate;

    @BindView(R.id.meeting_time)
    TextView tvMeetingTime;

    @BindView(R.id.tv_place_selection)
    TextView mPlaceSelection;

    Calendar dateAndTime = Calendar.getInstance();

    private Place mPlace;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private int category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        ButterKnife.bind(this, view);
        setInitialDateTime();

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        return view;
    }

    @OnClick(R.id.meeting_date)
    public void meetingDateSetOnClickListener() {
        setDate();
    }

    @OnClick(R.id.meeting_time)
    public void meetingTimeSetOnClickListener() {
        setTime();
    }

    @OnClick(R.id.tv_place_selection)
    public void selectPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    public void setDate() {
        new DatePickerDialog(getContext(), d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime() {
        new TimePickerDialog(getContext(), t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка начальных даты и времени
    private void setInitialDateTime() {

        tvMeetingDate.setText(DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

        tvMeetingTime.setText(DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        setInitialDateTime();
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setInitialDateTime();
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPlace = PlacePicker.getPlace(getContext(), data);
                mPlaceSelection.setText(Objects.requireNonNull(mPlace.getAddress()).toString());
            }
        }
    }

    @OnClick(R.id.btn_create_event)
    public void createEvent() {
        Map<String, Object> res = new HashMap<>();
        List<Double> coords = new ArrayList<>();
        coords.add(mPlace.getLatLng().latitude);
        coords.add(mPlace.getLatLng().longitude); //todo add category and checks
        EventDAO event = new EventDAO(coords, etMeetingName.getText().toString(), etMeetingDescription.getText().toString(),
                dateAndTime.getTime(), category, Collections.emptyList(), mPlace.getAddress().toString());
        String key = mDatabase.getReference("events").push().getKey();
        String userKey = mUser.getUid();
        res.put(key, event);
        mDatabase.getReference("events").updateChildren(res).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mDatabase.getReference("user_created_events").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> upd = new HashMap<>();
                        List<String> events = (List<String>) dataSnapshot.getValue();
                        if(events == null)
                            events = new ArrayList<>();
                        events.add(key);
                        upd.put(userKey, events);
                        mDatabase.getReference("user_created_events").updateChildren(upd);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDatabase.getReference("users_events").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> upd = new HashMap<>();
                        List<String> events = (List<String>) dataSnapshot.getValue();
                        if(events == null)
                            events = new ArrayList<>();
                        events.add(key);
                        upd.put(userKey, events);
                        mDatabase.getReference("users_events").updateChildren(upd);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Map<String, Object> upd = new HashMap<>();
                upd.put(key, Arrays.asList(new String[]{userKey}));
                mDatabase.getReference("events_users").updateChildren(upd);
                onBackPressed();
            } else {
                Toast.makeText(getContext(), "Failed loading please try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            ((MainActivity) getActivity()).backToMap();
        } catch (NullPointerException e) {
        }
    }
}





