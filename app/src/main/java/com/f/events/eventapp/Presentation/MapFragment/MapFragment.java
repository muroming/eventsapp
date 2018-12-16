package com.f.events.eventapp.Presentation.MapFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.FragmentInteractions;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment implements FragmentInteractions.OnBackPressListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String EVENT_KEY = "event";

    private GoogleMap mMap;
    private EventDAO mSelectedMarker;
    private BottomSheetBehavior mBottomSheet;
    private FirebaseDatabase mDatabase;
    private Map<String, Marker> mKeyMarkerMap;
    private SimpleDateFormat mFormat = new SimpleDateFormat("EEE, d MMM HH:mm",Locale.getDefault());

    @BindView(R.id.ll_event_bottom_sheet)
    LinearLayout mEventBottomLayout;

    @BindView(R.id.tv_event_sheet_description)
    TextView mEventSheetDescription;

    @BindView(R.id.tv_event_sheet_time)
    TextView mEventSheetTime;

    @BindView(R.id.tv_event_sheet_name)
    TextView mEventSheetName;

    @BindView(R.id.tv_event_sheet_place)
    TextView mEventSheetPlace;

    @BindView(R.id.btn_add_event)
    FloatingActionButton mFloatButton;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mKeyMarkerMap = new HashMap<>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);
        mBottomSheet = BottomSheetBehavior.from(mEventBottomLayout);
        mBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_EXPANDED || i == BottomSheetBehavior.STATE_COLLAPSED) {
                    mFloatButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark, null));
                }
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    mFloatButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, null));
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        ButterKnife.bind(this, v);

        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Map.Entry<String, Marker> m : mKeyMarkerMap.entrySet()) {
            if (m.getValue().equals(marker)) {
                mDatabase.getReference("events").child(m.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mSelectedMarker = dataSnapshot.getValue(EventDAO.class);
                        showEventSheetInfo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMinZoomPreference(Math.max(mMap.getMinZoomLevel(), 10)); //City
        mMap.setMaxZoomPreference(20); //Buildings

        enableLocation();
        setupEventsDB();
    }

    @Override
    public void onBackPressed() {
        if (mSelectedMarker != null) {
            mSelectedMarker = null;
            hideEventSheetInfo();
        } else {
            getActivity().finish();
        }
    }

    private void showEventSheetInfo() {
        mEventSheetName.setText(mSelectedMarker.getName());
        mEventSheetTime.setText(mFormat.format(mSelectedMarker.getEventTime()));
        mEventSheetDescription.setText(mSelectedMarker.getDescription());
        mEventSheetPlace.setText(mSelectedMarker.getPlaceName());
        mBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideEventSheetInfo() {
        mBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // If API is greater 23 request permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            mMap.setMyLocationEnabled(true);
            FusedLocationProviderClient client = new FusedLocationProviderClient(getContext());
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        LatLng pos = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                    }
                }
            });
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @OnClick(R.id.btn_add_event)
    public void actionBarSetOnClickListener() {
        ((MainActivity) Objects.requireNonNull(getActivity())).showCreateEventFragment();
    }

    private void setupEventsDB() {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference().child("events");


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!dataSnapshot.getKey().equals("total")) {
                    EventDAO event = dataSnapshot.getValue(EventDAO.class);
                    MarkerOptions options = new MarkerOptions().title(event.getName())
                            .position(event.getLatLng());
                    Marker marker = mMap.addMarker(options);
                    mKeyMarkerMap.put(dataSnapshot.getKey(), marker);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mKeyMarkerMap.get(dataSnapshot.getKey()).remove();
                EventDAO event = dataSnapshot.getValue(EventDAO.class);
                MarkerOptions options = new MarkerOptions().title(event.getName())
                        .position(event.getLatLng());
                Marker marker = mMap.addMarker(options);
                mKeyMarkerMap.put(dataSnapshot.getKey(), marker);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                mKeyMarkerMap.get(dataSnapshot.getKey()).remove();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed loading please try later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
