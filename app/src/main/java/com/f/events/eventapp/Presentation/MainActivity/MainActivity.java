package com.f.events.eventapp.Presentation.MainActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import com.f.events.eventapp.Presentation.LoginActivity.LoginActivity;
import com.f.events.eventapp.Presentation.MapFragment.MapFragment;
import com.f.events.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public interface OnBackPressListener {
        void onBackPressed();
    }

    private FirebaseAuth mAuth;
    private OnBackPressListener mOnBackListener;

    @BindView(R.id.nv_drawer)
    NavigationView mNavigationDrawer;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

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
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            LoginActivity.start(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_fragment_container ,MapFragment.newInstance())
                .commit();


        mNavigationDrawer.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()) {
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

    @Override
    public void onBackPressed() {
        mOnBackListener.onBackPressed();
    }
}
