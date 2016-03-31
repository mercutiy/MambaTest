package ru.mamba.test.mambatest;

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
import ru.mamba.test.mambatest.fetcher.FetchException;
import ru.mamba.test.mambatest.fetcher.ImageFetcher;
import ru.mamba.test.mambatest.fetcher.Request;

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


    private class ProfileFetcher extends ApiFetcher {

        private JSONObject mAnketa;

        private Bitmap mImage;

        public ProfileFetcher(Context context) {
            super(context);
            JSONObject multiReq;
            try {
                // TODO пробростиь anekta_id
                multiReq = new JSONObject("{\"sysRequestsContainer\":[{\"method\":\"GET\", \"uri\":\"/profile/\", \"params\":{}}, {\"method\": \"GET\", \"uri\":\"/contacts/all/\", \"params\":{\"limit\": 1}}, {\"method\":\"GET\", \"uri\":\"/users/634593392/albums/\", \"params\":{}}]}");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
                onPostExecute(null);
                return;
            }
            mRequest = new Request("", Request.POST, null, multiReq);
        }

        @Override
        protected JSONObject getResponse(Request request) throws FetchException {
            JSONObject response = super.getResponse(request);
            if (response == null) {
                return response;
            }

            try {
                JSONObject mAnketa = response.getJSONArray("sysResponsesContainer").getJSONObject(0).getJSONObject("anketa");
                String photoSrc = mAnketa.getString("squarePhotoUrl");
                mImage = new ImageFetcher().fetchImage(photoSrc);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
                throw new FetchException();
            }
            catch (IOException e) {
                Log.e(TAG, "IO error", e);
                throw new FetchException();
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject anketa = json.getJSONArray("sysResponsesContainer").getJSONObject(0).getJSONObject("anketa");

                String name = anketa.getString("name");

                int age = anketa.getInt("age");

                String greeting = "";
                JSONArray aboutme =
                    anketa.getJSONObject("aboutmeBlock").getJSONArray("fields");
                JSONObject aboutmeItem;
                for (int i = 0; i < aboutme.length(); i++) {
                    aboutmeItem = aboutme.getJSONObject(i);
                    if (aboutmeItem.getString("key").equals("aboutme")) {
                        greeting = aboutmeItem.getString("value");
                        break;
                    }
                }

                //List<String> interests = new ArrayList<String>();
                String interests = "";
                JSONArray jsonInterests = anketa.getJSONObject("interests").getJSONArray("items");
                for (int i = 0; i < jsonInterests.length(); i++) {
                    //interests.add(jsonInterests.getJSONObject(i).getString("title"));
                    interests = interests + jsonInterests.getJSONObject(i).getString("title") + " ";
                }


                int contacts = json.getJSONArray("sysResponsesContainer").getJSONObject(1).getInt("count");

                int albums = json.getJSONArray("sysResponsesContainer").getJSONObject(2).getJSONArray("albums").length();


                mGreeting.setText(greeting);

                mInterests.setText(interests);

                // TODO age == 0 Скрытый возраст
                String title = name + " " + String.valueOf(age) + " " + getResources().getString(R.string.string_its_you);

                ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
                if (ab != null) {
                    ab.setTitle(title);
                }

                mPhoto.setImageBitmap(mImage);

                mAlbumButton.setText(getResources().getQuantityString(R.plurals.number_of_albums, albums, albums));
                mContactButton.setText(getResources().getQuantityString(R.plurals.number_of_contacts, contacts, contacts));

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }

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
