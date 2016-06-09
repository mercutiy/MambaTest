package ru.mamba.test.mambatest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.api.Session;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sid = Session.getInstance(this).getSid();
        if (sid.length() > 0) {
            startActivity(new Intent(this, Profile.class));
        }
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
