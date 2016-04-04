package ru.mamba.test.mambatest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.ApiFetcher2;
import ru.mamba.test.mambatest.fetcher.Autharize;
import ru.mamba.test.mambatest.fetcher.ConnectionException;
import ru.mamba.test.mambatest.fetcher.FetchException;
import ru.mamba.test.mambatest.fetcher.ImageFetcher;
import ru.mamba.test.mambatest.fetcher.ImageResponse;
import ru.mamba.test.mambatest.fetcher.JsonException;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.fetcher.Response;
import ru.mamba.test.mambatest.fetcher.Session;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = LoginFragment.class.getCanonicalName();

    public ProfileFragment() {
    }

    private ImageView mPhoto;

    private TextView mGreeting;

    private TextView mInterests;

    private Button mAlbumButton;

    private Button mContactButton;

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

        new ProfileFetcher(getActivity()).execute();

        return view;
    }


    private class ProfileFetcher extends ApiFetcher2 implements Autharize {

        public ProfileFetcher(Activity activity) {
            super(activity);
            int anketaId = new Session(activity).getAnketaId();
            try {
                JSONObject request = new JSONObject("{\"sysRequestsContainer\":[{\"method\":\"GET\", \"uri\":\"/profile/\", \"params\":{}}, {\"method\":\"GET\", \"uri\":\"/folders/\", \"params\":{}}, {\"method\":\"GET\", \"uri\":\"/users/" + String.valueOf(anketaId) + "/albums/\", \"params\":{}}]}");
                setRequest(new Request("", Request.POST, null, request));
            } catch (JSONException e) {
                Log.e(TAG, "json creating error", e);
            }
        }


        @Override
        protected Response getResponse(Request request) throws FetchException {
            Response response = super.getResponse(request);

            try {
                JSONObject json = response.getJson();

                if (!json.getBoolean("isAuth")) {
                    return response;
                }

                String photoSrc = json
                    .getJSONArray("sysResponsesContainer")
                    .getJSONObject(0)
                    .getJSONObject("anketa")
                    .getString("squarePhotoUrl");
                Bitmap photo = new ImageFetcher().fetchImage(photoSrc);

                ImageResponse imageResponse = new ImageResponse(response.getJson(), photo);
                imageResponse.setPhoto(photo);

                return imageResponse;

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
                throw new JsonException();
            }
            catch (IOException e) {
                Log.e(TAG, "IO error", e);
                throw new ConnectionException();
            }

        }

        @Override
        protected void uiExecute(Response response) throws JSONException {
            ImageResponse imageResponse = (ImageResponse)response;

            JSONArray container = imageResponse.getJson().getJSONArray("sysResponsesContainer");

            JSONObject anketa = container.getJSONObject(0).getJSONObject("anketa");

            String name = anketa.getString("name");

            int age = anketa.getInt("age");

            String greeting = "";
            if (anketa.has("aboutmeBlock") && !anketa.isNull("aboutmeBlock")) {
                JSONObject aboutMeBlock = anketa.getJSONObject("aboutmeBlock");
                JSONArray aboutMe = aboutMeBlock.getJSONArray("fields");
                for (int i = 0; i < aboutMe.length(); i++) {
                    JSONObject aboutmeItem = aboutMe.getJSONObject(i);
                    if (aboutmeItem.getString("key").equals("aboutme")) {
                        greeting = aboutmeItem.getString("value");
                        break;
                    }
                }
            }

            String interests = "";
            JSONArray jsonInterests = anketa.getJSONObject("interests").getJSONArray("items");
            for (int i = 0; i < jsonInterests.length(); i++) {
                interests = interests + jsonInterests.getJSONObject(i).getString("title") + " ";
            }

            mGreeting.setText(greeting);
            mInterests.setText(interests);
            mPhoto.setImageBitmap(imageResponse.getPhoto());

            JSONObject folder = container.getJSONObject(1).getJSONArray("folders").getJSONObject(0);

            int contacts = folder.getInt("count");
            int albums = container.getJSONObject(2).getJSONArray("albums").length();

            mAlbumButton.setText(getResources().getQuantityString(R.plurals.number_of_albums, albums, albums));
            mContactButton.setText(getResources().getQuantityString(R.plurals.number_of_contacts, contacts, contacts));

            ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (ab != null) {
                String title = name + " " + String.valueOf(age) + " " + getResources().getString(R.string.string_its_you);
                ab.setTitle(title);
            }

            new Session(getActivity()).setFolderId(folder.getInt("id"));
        }
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
}
