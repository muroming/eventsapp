package com.f.events.eventapp.Presentation.MainActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.Data.UserDAO;
import com.f.events.eventapp.Presentation.CreateEventFragment.CreateEventFragment;
import com.f.events.eventapp.Presentation.LoginActivity.LoginActivity;
import com.f.events.eventapp.Presentation.EventsList.EventsFragment;
import com.f.events.eventapp.Presentation.MapFragment.MapFragment;
import com.f.events.eventapp.Presentation.ParticipantsFragment.EventParticipantsFragment;
import com.f.events.eventapp.Presentation.ProfileFragment.ProfileFragment;
import com.f.events.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;


import butterknife.BindView;
import butterknife.ButterKnife;

import com.f.events.eventapp.FragmentInteractions.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private OnBackPressListener mOnBackListener;
    private SharedPreferences preferences;

    @BindView(R.id.nv_drawer)
    NavigationView mNavigationDrawer;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    ImageView mProfileIconNav;

    TextView mProfileNameNav;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof OnBackPressListener) {
            mOnBackListener = (OnBackPressListener) fragment;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        preferences = getSharedPreferences("PREFS", MODE_PRIVATE);

        if (!preferences.getBoolean("log", false)) {
            LoginActivity.start(this);
            finish();
        } else {

            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_fragment_container, MapFragment.newInstance())
                    .commit();
            mNavigationDrawer.getMenu().getItem(0)
                    .setChecked(true);


            mNavigationDrawer.setNavigationItemSelectedListener(menuItem -> {

                switch (menuItem.getItemId()) {
                    case R.id.nav_logout: {
                        mAuth.signOut();
                        preferences.edit().putBoolean("log", false).apply();
                        LoginActivity.start(this);
                        finish();
                        break;
                    }
                    case R.id.nav_profile: {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        setTitle("Профиль");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fl_fragment_container, ProfileFragment.newInstance())
                                .commit();
                        break;
                    }
                    case R.id.nav_my_events: {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_fragment_container, EventsFragment.newInstance())
                                .commit();
                        break;
                    }
                    case R.id.nav_map: {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_fragment_container, MapFragment.newInstance())
                                .commit();
                        break;
                    }
                }

                return true;
            });
            mProfileIconNav = mNavigationDrawer.getHeaderView(0).findViewById(R.id.nav_profile_icon);
            mProfileNameNav = mNavigationDrawer.getHeaderView(0).findViewById(R.id.nav_profile_name);
            loadDrawer();
        }
    }

    private void loadDrawer() {
        String key = mAuth.getCurrentUser().getUid();
        mDatabase.getReference("users").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDAO user = dataSnapshot.getValue(UserDAO.class);
                Picasso.get().load(user.getImageUri())
                        .placeholder(R.drawable.ic_profile)
                        .into(mProfileIconNav);
                mProfileNameNav.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        mOnBackListener.onBackPressed();
    }

    public void backToMap() {
        mNavigationDrawer.getMenu().getItem(0).setChecked(true);
        mDrawerLayout.closeDrawers();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_fragment_container, MapFragment.newInstance())
                .commit();
    }

    public DrawerLayout getDrawerLayout() {
        return findViewById(R.id.drawer_layout);
    }

    public void showCreateEventFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment_container, CreateEventFragment.newInstance())
                .commit();
    }

    public void goToEvent(EventDAO event) {
        MapFragment fragment = MapFragment.newInstance();
        fragment.goToEvent(event.getLatLng());

        mNavigationDrawer.getMenu().getItem(0).setChecked(true);
        mDrawerLayout.closeDrawers();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_fragment_container, fragment)
                .commit();

    }

    public void showParticipants(String key) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment_container, EventParticipantsFragment.newInstance(key))
                .addToBackStack(null)
                .commit();
    }
}
