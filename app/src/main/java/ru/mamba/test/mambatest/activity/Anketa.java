package ru.mamba.test.mambatest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ru.mamba.test.mambatest.fragment.AnketaFragment;
import ru.mamba.test.mambatest.R;

public class Anketa extends AppCompatActivity {

    public static String EXTRA_ANKETA_ID = "ANKETA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anketa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int anketaId = getIntent().getIntExtra(EXTRA_ANKETA_ID, 0);
        AnketaFragment fragment = AnketaFragment.newInstance(anketaId);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.fragment_anketa, fragment)
            .commit();
    }

}
