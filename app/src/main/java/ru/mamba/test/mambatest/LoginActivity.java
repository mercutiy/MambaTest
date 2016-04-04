package ru.mamba.test.mambatest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import ru.mamba.test.mambatest.fetcher.ApiFetcher;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sid = getSharedPreferences(ApiFetcher.PREF_SESSION, Context.MODE_PRIVATE).getString(ApiFetcher.PREF_FIELD_SID, "");
        if (sid.length() > 0) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
