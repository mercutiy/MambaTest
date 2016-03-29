package ru.mamba.test.mambatest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AnketaActivity extends AppCompatActivity {

    public static String EXTRA_ANKETA_ID = "ANKETA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anketa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int anketaId = getIntent().getIntExtra(EXTRA_ANKETA_ID, 0);
        AnketaFragment fragment = AnketaFragment.newInstance(anketaId);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.fragment_anketa, fragment)
            .commit();
    }

}
