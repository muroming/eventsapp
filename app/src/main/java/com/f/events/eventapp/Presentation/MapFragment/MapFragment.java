package com.f.events.eventapp.Presentation.MapFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.f.events.eventapp.FragmentInteractions;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment implements FragmentInteractions.OnBackPressListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private Marker mSelectedMarker;
    private BottomSheetBehavior mBottomSheet;

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
        mSelectedMarker = marker;
        showEventSheetInfo();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMinZoomPreference(Math.max(mMap.getMinZoomLevel(), 10)); //City
        mMap.setMaxZoomPreference(20); //Buildings

        List<LatLng> places = new ArrayList<>();
        places.add(new LatLng(55.754724, 37.621380));
        places.add(new LatLng(55.760133, 37.618697));
        places.add(new LatLng(55.764753, 37.591313));
        places.add(new LatLng(55.728466, 37.604155));

        for (LatLng l : places) {
            MarkerOptions m = new MarkerOptions()
                    .position(l)
                    .title("Marker");

            googleMap.addMarker(m);
        }

        enableLocation();
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
}
