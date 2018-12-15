package com.f.events.eventapp.Presentation.MapFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsFragment extends Fragment {

    @BindView(R.id.vp_switch_events)
    ViewPager viewPager;
    List<EventDAO> events;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        ButterKnife.bind(this, view);
        viewPager.setAdapter(new EventsPagerAdapter(getFragmentManager()));
        events = new ArrayList<>();
        return view;
    }

    public class EventsPagerAdapter extends FragmentPagerAdapter{

        public EventsPagerAdapter(FragmentManager fragmentManager){ super(fragmentManager); }
        @Override
        public Fragment getItem(int i) {
            return EventsListFragment.newInstance(events);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
