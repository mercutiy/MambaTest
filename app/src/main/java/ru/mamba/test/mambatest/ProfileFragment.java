package ru.mamba.test.mambatest;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.Request;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = LoginActivityFragment.class.getCanonicalName();

    public ProfileFragment() {
    }

    private ImageView mPhoto;

    private TextView mGreeting;

    private TextView mInterests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mPhoto = (ImageView)view.findViewById(R.id.image_view_photo);
        mGreeting = (TextView)view.findViewById(R.id.text_view_slogan);
        mInterests = (TextView)view.findViewById(R.id.text_view_interests);

        new LoginFetcher(getActivity()).execute();

        return view;
    }


    private class LoginFetcher extends ApiFetcher {

        public LoginFetcher(Context context) {
            super(context);
            JSONObject multiReq;
            try {
                multiReq = new JSONObject("{\"sysRequestsContainer\":[{\"method\":\"GET\", \"uri\":\"/profile/\", \"params\":{}}, {\"method\": \"GET\", \"uri\":\"/contacts/all/\", \"params\":{\"limit\": 1}}, {\"method\":\"GET\", \"uri\":\"/users/634593392/albums/\", \"params\":{}}]}");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
                onPostExecute(null);
                return;
            }
            mRequest = new Request("", Request.POST, null, multiReq);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null) {
                Toast.makeText(getActivity(), R.string.notice_wrong_login, Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getActivity(), R.string.notice_right_login, Toast.LENGTH_LONG).show();

            try {
                JSONObject anketa = json.getJSONArray("sysResponsesContainer").getJSONObject(0).getJSONObject("anketa");

                String name = anketa.getString("name");

                int age = anketa.getInt("age");

                String photoSrc = anketa.getString("squarePhotoUrl");

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

                String title = name + " " + String.valueOf(age) + " " + R.string.button_login;

                ActionBar ab =  getActivity().getActionBar();
                if (ab != null) {
                    ab.setTitle(title);
                }

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }
        }
    }
}
