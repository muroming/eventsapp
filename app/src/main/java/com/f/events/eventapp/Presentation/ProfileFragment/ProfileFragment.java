package com.f.events.eventapp.Presentation.ProfileFragment;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.f.events.eventapp.Data.EventDAO;
import com.f.events.eventapp.Data.UserDAO;
import com.f.events.eventapp.FragmentInteractions;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment implements FragmentInteractions.OnBackPressListener, ProfileEventsAdapter.OnItemClicked {

    @BindView(R.id.rv_profile_events)
    RecyclerView mRecyclerEvents;

    @BindView(R.id.et_profile_name)
    EditText mNameEditText;

    @BindView(R.id.tv_profile_name)
    TextView mNameTextView;

    @BindView(R.id.iv_profile_picture)
    ImageView mPictureProfile;

    private ProfileEventsAdapter adapter;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private Map<String, EventDAO> map;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        adapter = new ProfileEventsAdapter(getContext());
        adapter.setListener(this);
        mDatabase = FirebaseDatabase.getInstance();
        map = new HashMap<>();

        mRecyclerEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerEvents.setAdapter(adapter);
        mNameEditText.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                mNameTextView.setText(mNameEditText.getText().toString());
                mNameEditText.setVisibility(View.INVISIBLE);
                mNameTextView.setVisibility(View.VISIBLE);
                saveName(mNameEditText.getText().toString());
                return true;
            }
            return false;
        });

        getEvents();
        loadUser();

        return v;
    }

    private void loadUser() {
        String key = mUser.getUid();
        mDatabase.getReference("users").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDAO user = dataSnapshot.getValue(UserDAO.class);
                Picasso.get().load(user.getImageUri())
                        .transform(new CircularTransformation(420))
                        .placeholder(R.drawable.ic_profile)
                        .into(mPictureProfile);
                mNameTextView.setText(user.getName());
                mNameEditText.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class CircularTransformation implements Transformation {

        private int mRadius = 10;

        public CircularTransformation(final int radius) {
            this.mRadius = radius;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            canvas.drawARGB(0, 0, 0, 0);

            paint.setColor(Color.parseColor("#BAB399"));

            if (mRadius == 0) {
                canvas.drawCircle(source.getWidth() / 2 + 0.7f, source.getHeight() / 2 + 0.7f,
                        source.getWidth() / 2 - 1.1f, paint);
            } else {
                canvas.drawCircle(source.getWidth() / 2 + 0.7f, source.getHeight() / 2 + 0.7f,
                        mRadius, paint);
            }

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            canvas.drawBitmap(source, rect, rect, paint);

            if (source != output) {
                source.recycle();
            }
            return output;
        }

        @Override
        public String key() {
            return "circular" + String.valueOf(mRadius);
        }
    }

    private void saveName(String name) {
        String key = mUser.getUid();
        Map<String, Object> res = new HashMap<>();
        res.put("name", name);
        mDatabase.getReference("users").child(key).updateChildren(res);
    }

    public void getEvents() {
        String key = mUser.getUid();

        mDatabase.getReference("users_events").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> eventKeys = (List<String>) dataSnapshot.getValue();
                if(eventKeys == null)
                    eventKeys = new ArrayList<>();
                for (String key : eventKeys) {
                    mDatabase.getReference("events").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            EventDAO e = dataSnapshot.getValue(EventDAO.class);
                            map.put(key, e);
                            adapter.addItem(e);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.iv_profile_edit_name)
    public void onEditClicked(View v) {
        mNameEditText.setVisibility(View.VISIBLE);
        mNameTextView.setVisibility(View.INVISIBLE);
        mNameEditText.setText(mNameTextView.getText().toString());
        mNameEditText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if (mNameEditText.getVisibility() == View.VISIBLE) {
            saveName(mNameEditText.getText().toString());
            mNameTextView.setText(mNameEditText.getText().toString());
            mNameEditText.setVisibility(View.INVISIBLE);
            mNameTextView.setVisibility(View.VISIBLE);
        } else {
            ((MainActivity) getActivity()).backToMap();
        }
    }

    @Override
    public void onClick(EventDAO event) {
        for (Map.Entry<String, EventDAO> e : map.entrySet()) {
            if (e.getValue().equals(event)) {
                ((MainActivity) getActivity()).showParticipants(e.getKey());
            }
        }
    }
}
