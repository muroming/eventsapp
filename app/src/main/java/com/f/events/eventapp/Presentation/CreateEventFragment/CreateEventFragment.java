package com.f.events.eventapp.Presentation.CreateEventFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
        coords.add(mPlace.getLatLng().longitude); //todo add category
        EventDAO event = new EventDAO(coords, etMeetingName.getText().toString(), etMeetingDescription.getText().toString(),
                dateAndTime.getTime(), 0, Collections.emptyList(), mPlace.getAddress().toString());
        String key = mDatabase.getReference("events").push().getKey();
        res.put(key, event);
        mDatabase.getReference("events").updateChildren(res).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onBackPressed();
            } else {
                Toast.makeText(getContext(), "Failed loading please try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).backToMap();
    }
}





