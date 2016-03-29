package ru.mamba.test.mambatest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.FetchException;
import ru.mamba.test.mambatest.fetcher.ImageFetcher;
import ru.mamba.test.mambatest.fetcher.Request;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnketaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnketaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnketaFragment extends Fragment {


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

        return view;
    }


    private class AnketaFetcher extends ApiFetcher {

        private final String TAG = AnketaFetcher.class.getCanonicalName();

        private JSONObject mAnketa;

        private Bitmap mImage;

        public AnketaFetcher(Context context, int anketaId) {
            super(context);
            mRequest = new Request("/users/" + String.valueOf(anketaId) + "/");
        }

        @Override
        protected JSONObject getResponse(Request request) throws FetchException {
            JSONObject response = super.getResponse(request);
            if (response == null) {
                return response;
            }

            try {
                JSONObject mAnketa = response.getJSONObject("anketa");
                String photoSrc = mAnketa.getString("squarePhotoUrl");
                mImage = new ImageFetcher().fetchImage(photoSrc);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
                throw new FetchException();
            } catch (IOException e) {
                Log.e(TAG, "IO error", e);
                throw new FetchException();
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                String name = mAnketa.getString("name");

                int age = mAnketa.getInt("age");

                String greeting = "";
                JSONArray aboutme = mAnketa.getJSONObject("aboutmeBlock").getJSONArray("fields");
                JSONObject aboutmeItem;
                for (int i = 0; i < aboutme.length(); i++) {
                    aboutmeItem = aboutme.getJSONObject(i);
                    if (aboutmeItem.getString("key").equals("aboutme")) {
                        greeting = aboutmeItem.getString("value");
                        break;
                    }
                }

                String interests = "";
                JSONArray jsonInterests = mAnketa.getJSONObject("interests").getJSONArray("items");
                for (int i = 0; i < jsonInterests.length(); i++) {
                    interests = interests + jsonInterests.getJSONObject(i).getString("title") + " ";
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }
        }
   }
}
