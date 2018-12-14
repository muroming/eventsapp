package com.f.events.eventapp.Presentation.MapActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.f.events.eventapp.Presentation.CreateEventActivity.CreateEventActivity;
import com.f.events.eventapp.Presentation.LoginActivity.LoginActivity;
import com.f.events.eventapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private SearchView mSearchView;
    private Marker mSelectedMarker;

    @BindView(R.id.nv_drawer)
    NavigationView mNavigationDrawer;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    public static void start(Context context) {
        Intent starter = new Intent(context, MapActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            LoginActivity.start(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mNavigationDrawer.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()){
                case R.id.nav_logout: {
                    mAuth.signOut();
                    LoginActivity.start(this);
                    finish();
                    break;
                }
                case R.id.nav_profile: {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    break;
                }
                case R.id.nav_my_events: {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    break;
                }
            }

            return true;
        });
    }

    @OnClick(R.id.btn_add_event)
    public void onAddEventClicked(View v) {
        CreateEventActivity.start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.getItem(0));
        mSearchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mSelectedMarker != null) {
            mSelectedMarker.hideInfoWindow();
            mSelectedMarker = null;
        } else {
            finish();
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        mSelectedMarker = marker;
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "InfoClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
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

    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // If API is greater 23 request permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

    }
}
