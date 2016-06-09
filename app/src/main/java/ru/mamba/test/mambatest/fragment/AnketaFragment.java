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

import org.apache.commons.lang3.StringUtils;

import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.Anketa;

public class AnketaFragment extends Fragment implements Callback1<Anketa.Model> {


    private static final String ARG_ANKETA_ID = "ANKETA_ID";

    private int mAnketaId;

    private TextView mGreeting;

    private TextView mInterests;

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
        mInterests = (TextView)view.findViewById(R.id.text_view_anketa_interests);
        mPhoto = (ImageView)view.findViewById(R.id.image_view_anketa_photo);

        Fetcher fetcher = new Fetcher(getActivity(), this);
        fetcher.fetch(new Anketa(mAnketaId));

        return view;
    }

    @Override
    public void onResponse(Anketa.Model anketa) {
        mGreeting.setText(anketa.getAnketa().getGreeting());
        mInterests.setText(StringUtils.join(anketa.getAnketa().getInterests(), " "));
        mPhoto.setImageBitmap(anketa.getAnketa().getPhoto());
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            String title = anketa.getAnketa().getName() + " " + String.valueOf(anketa.getAnketa().getAge());
            ab.setTitle(title);
        }
    }

}
