package com.f.events.eventapp.Presentation.CreateEventActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.f.events.eventapp.R;

public class CreateEventActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, CreateEventActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        setTitle(R.string.title_activity_create_event);
    }
}
