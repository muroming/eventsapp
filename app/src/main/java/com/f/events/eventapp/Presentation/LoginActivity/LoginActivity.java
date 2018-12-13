package com.f.events.eventapp.Presentation.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.f.events.eventapp.Presentation.MapActivity.MapActivity;
import com.f.events.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void login(View v) {
        mAuth.signInWithEmailAndPassword(mLogin.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        MapActivity.start(this);
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
                        MapActivity.start(this);
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
