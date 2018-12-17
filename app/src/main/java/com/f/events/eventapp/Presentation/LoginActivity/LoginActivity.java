package com.f.events.eventapp.Presentation.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.f.events.eventapp.Data.UserDAO;
import com.f.events.eventapp.Presentation.MainActivity.MainActivity;
import com.f.events.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private SharedPreferences preferences;

    @BindView(R.id.tv_login)
    TextView mLogin;

    @BindView(R.id.tv_password)
    TextView mPassword;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void login(View v) {
        mAuth.signInWithEmailAndPassword(mLogin.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        preferences.edit().putBoolean("log", true).apply();
                        MainActivity.start(this);
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.btn_create)
    public void createUser(View v) {
        mAuth.createUserWithEmailAndPassword(mLogin.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String key = task.getResult().getUser().getUid();
                        UserDAO user = new UserDAO(null, mLogin.getText().toString());

                        mDatabase.getReference("users").child(key).setValue(user);
                        MainActivity.start(this);
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
