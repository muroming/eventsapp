package com.f.events.eventapp.Presentation.EventsList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.FragmentInteractions;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.firebase.auth.internal.FederatedSignInActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsFragment extends Fragment implements FragmentInteractions.OnBackPressListener {

    @BindView(R.id.vp_switch_events)
    ViewPager viewPager;
    List<EventDAO> events;
    @BindView(R.id.pts_switch_events)
    PagerTabStrip pagerTab;

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        ButterKnife.bind(this, view);
        EventsPagerAdapter pagerAdapter = new EventsPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();
        events = new ArrayList<>();
        pagerTab.setTabIndicatorColor(getResources().getColor(R.color.white));
        return view;
    }

    public class EventsPagerAdapter extends FragmentStatePagerAdapter {

        EventsPagerAdapter(FragmentManager fragmentManager){ super(fragmentManager); }
        @Override
        public Fragment getItem(int i) {
            return EventsListFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "My Events";
            }else{
                return "All Events";
            }
        }
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).backToMap();
    }
}
