package ru.mamba.test.mambatest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.callback.Callback3;
import ru.mamba.test.mambatest.api.controller.Albums;
import ru.mamba.test.mambatest.api.controller.Folders;
import ru.mamba.test.mambatest.api.controller.Profile;
import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.Autharize;
import ru.mamba.test.mambatest.fetcher.ConnectionException;
import ru.mamba.test.mambatest.fetcher.FetchException;
import ru.mamba.test.mambatest.fetcher.ImageFetcher;
import ru.mamba.test.mambatest.fetcher.ImageResponse;
import ru.mamba.test.mambatest.fetcher.JsonException;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.fetcher.Response;
import ru.mamba.test.mambatest.fetcher.Session;
import ru.mamba.test.mambatest.model.Folder;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, Callback3<Profile.Model, Albums.Model, Folders.Model> {

    private static final String TAG = LoginFragment.class.getCanonicalName();

    public ProfileFragment() {
    }

    private ImageView mPhoto;

    private TextView mGreeting;

    private TextView mInterests;

    private Button mAlbumButton;

    private Button mContactButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mPhoto = (ImageView)view.findViewById(R.id.image_view_photo);
        mGreeting = (TextView)view.findViewById(R.id.text_view_slogan);
        mInterests = (TextView)view.findViewById(R.id.text_view_interests);
        mAlbumButton = (Button)view.findViewById(R.id.button_albums);
        mContactButton = (Button)view.findViewById(R.id.button_contacts);

        mAlbumButton.setOnClickListener(this);
        mContactButton.setOnClickListener(this);

        Fetcher fetcher = new Fetcher(getActivity(), this);
        fetcher.fetch(
            new Profile(),
            new Albums(Session.getInstance(getActivity()).getAnketaId(), false, 0),
            new Folders()
        );

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_contacts) {
            Intent intent = new Intent(getActivity(), ContactsActivity.class);
            getActivity().startActivity(intent);

        } else if (v.getId() == R.id.button_albums) {
            Intent intent = new Intent(getActivity(), AlbumsActivity.class);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onResponse(Profile.Model profile, Albums.Model albums, Folders.Model folders) {
        mGreeting.setText(profile.getProfile().getGreeting());
        mInterests.setText(StringUtils.join(profile.getProfile().getInterests(), " "));
        mPhoto.setImageBitmap(profile.getProfile().getPhoto());
        mAlbumButton.setText(getResources().getQuantityString(R.plurals.number_of_albums, albums.getAlbums().length, albums.getAlbums().length));
        Folder folder = folders.getFolders()[0];
        mContactButton.setText(getResources().getQuantityString(R.plurals.number_of_contacts, folder.getContactCount(), folder.getContactCount()));
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            String title = profile.getProfile().getName() + " " + String.valueOf(profile.getProfile().getAge()) + " " + getResources().getString(R.string.string_its_you);
            ab.setTitle(title);
        }
        Session.getInstance(getActivity()).setFolderId(folder.getId());
    }
}
