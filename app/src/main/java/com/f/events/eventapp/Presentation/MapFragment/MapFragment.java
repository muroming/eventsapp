package com.f.events.eventapp.Presentation.MapFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.FragmentInteractions;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment implements FragmentInteractions.OnBackPressListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String ADD_EVENT = "ADD_EVENT";
    private static final String ATTEND_EVENT = "ATTEND_EVENT";

    private GoogleMap mMap;
    private EventDAO mSelectedEventDao;
    private Marker mSelectedMarker;
    private BottomSheetBehavior mBottomSheet;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private Map<String, Marker> mKeyMarkerMap;
    private SimpleDateFormat mFormat = new SimpleDateFormat("EEE, d MMM HH:mm", Locale.getDefault());

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.map_cardview)
    CardView cardView;

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

    @BindView(R.id.map_burger)
    ImageView mapBurger;

    @BindView(R.id.map_search)
    ImageView search;

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
                    mFloatButton.setTag(ATTEND_EVENT);
                }
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    mFloatButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, null));
                    mFloatButton.setTag(ADD_EVENT);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        mFloatButton.setTag(ADD_EVENT);

        mapBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)Objects.requireNonNull(getActivity())).getDrawerLayout().openDrawer(GravityCompat.START);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
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
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        ButterKnife.bind(this, v);

        mapFragment.getMapAsync(this);
        createSpinner();

        return v;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Map.Entry<String, Marker> m : mKeyMarkerMap.entrySet()) {
            if (m.getValue().equals(marker)) {
                mSelectedMarker = marker;
                mDatabase.getReference("events").child(m.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mSelectedEventDao = dataSnapshot.getValue(EventDAO.class);
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
        if (mSelectedEventDao != null) {
            mSelectedEventDao = null;
            mSelectedMarker = null;
            hideEventSheetInfo();
        } else {
            getActivity().finish();
        }
    }

    private void showEventSheetInfo() {
        mEventSheetName.setText(mSelectedEventDao.getName());
        mEventSheetTime.setText(mFormat.format(mSelectedEventDao.getEventTime()));
        mEventSheetDescription.setText(mSelectedEventDao.getDescription());
        mEventSheetPlace.setText(mSelectedEventDao.getPlaceName());
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
            client.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    LatLng pos = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
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

    public void createSpinner(){

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedCategory = spinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    @OnClick(R.id.btn_add_event)
    public void actionBarSetOnClickListener() {
        if (mFloatButton.getTag().equals(ADD_EVENT)) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showCreateEventFragment();
        } else {
            attendEvent();
        }
    }

    private void attendEvent() {
        String userKey = mUser.getUid();
        for (Map.Entry<String, Marker> m : mKeyMarkerMap.entrySet()) {
            if (m.getValue().equals(mSelectedMarker)) {
                mDatabase.getReference("events_users").child(m.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> attendingUsers = (ArrayList<String>) dataSnapshot.getValue();
                        if(attendingUsers == null){
                            attendingUsers = new ArrayList<>();
                        }
                        attendingUsers.add(userKey);
                        attendingUsers = new ArrayList(new HashSet(attendingUsers));
                        Map<String, Object> res = new HashMap<>();
                        res.put(m.getKey(), attendingUsers);
                        mDatabase.getReference("events_users").updateChildren(res);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mDatabase.getReference("users_events").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> eventsAttended = (ArrayList<String>) dataSnapshot.getValue();
                        if(eventsAttended == null){
                            eventsAttended = new ArrayList<>();
                        }
                        eventsAttended.add(m.getKey());
                        eventsAttended = new ArrayList(new HashSet(eventsAttended));
                        Map<String, Object> res = new HashMap<>();
                        res.put(userKey, eventsAttended);
                        mDatabase.getReference("users_events").updateChildren(res);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void setupEventsDB() {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference().child("events");


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EventDAO event = dataSnapshot.getValue(EventDAO.class);
                MarkerOptions options = new MarkerOptions().title(event.getName())
                        .position(event.getLatLng());
                Marker marker = mMap.addMarker(options);
                mKeyMarkerMap.put(dataSnapshot.getKey(), marker);
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
