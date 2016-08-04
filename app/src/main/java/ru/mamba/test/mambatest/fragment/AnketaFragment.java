package ru.mamba.test.mambatest.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.Session;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.Anketa;
import ru.mamba.test.mambatest.misc.TagLayout;

public class AnketaFragment extends Fragment implements Callback1<Anketa.Model> {


    private static final String ARG_ANKETA_ID = "ANKETA_ID";

    private int mAnketaId;

    private ru.mamba.test.mambatest.model.Anketa mAnketa;

    private TextView mGreeting;

    private TagLayout mInterests;

    private ImageView mPhoto;

    public AnketaFragment() {
    }

    public static AnketaFragment newInstance(int anketaId) {
        AnketaFragment fragment = new AnketaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ANKETA_ID, anketaId);
        fragment.setArguments(args);
        return fragment;
    }

    public static AnketaFragment newInstance(ru.mamba.test.mambatest.model.Anketa anketa) {
        AnketaFragment fragment = new AnketaFragment();
        fragment.mAnketa = anketa;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mAnketaId = getArguments().getInt(ARG_ANKETA_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anketa, container, false);

        mGreeting = (TextView)view.findViewById(R.id.text_view_anketa_greeting);
        mInterests = (TagLayout)view.findViewById(R.id.layout_anketa_interests);
        mPhoto = (ImageView)view.findViewById(R.id.image_view_anketa_photo);

        if (mAnketa != null) {
            showAnketa(mAnketa);
        } else {
            Fetcher fetcher = new Fetcher(getActivity(), this);
            fetcher.fetch(new Anketa(mAnketaId));
        }

        return view;
    }

    @Override
    public void onResponse(Anketa.Model anketa) {
        showAnketa(anketa.getAnketa());
    }

    private void showAnketa(ru.mamba.test.mambatest.model.Anketa anketa) {
        mGreeting.setText(anketa.getGreeting());
        LayoutInflater inflater = getLayoutInflater(new Bundle());
        for (String interest : anketa.getInterests()) {
            View interestView = inflater.inflate(R.layout.anketa_tag, null);
            ((TextView)interestView.findViewById(R.id.anketa_tag_title)).setText(interest);
            mInterests.addView(interestView);
        }
        mPhoto.setImageBitmap(anketa.getPhoto());
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            String title = anketa.getName() + " " + String.valueOf(anketa.getAge());
            if (anketa.getId() == Session.getInstance(getActivity()).getAnketaId()) {
                title += " " + getResources().getString(R.string.string_its_you);
            }
            actionBar.setTitle(title);
        }
    }

}
