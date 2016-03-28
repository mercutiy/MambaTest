package ru.mamba.test.mambatest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.Request;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewAlbumFragment extends Fragment {

    private LinearLayout mLayout;

    public NewAlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_album, container, false);
        mLayout = (LinearLayout)view.findViewById(R.id.layout_form);

        new FormFetcher(getActivity()).execute();

        return view;
    }

    private class FormFetcher extends ApiFetcher {

        private String TAG = FormFetcher.class.getCanonicalName();

        private LayoutInflater mInflater;

        public FormFetcher(Context context) {
            super(context);
            mRequest = new Request("/albums/new/");
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray blocks = json.getJSONObject("formBuilder").getJSONArray("blocks");
                mInflater = getLayoutInflater(new Bundle());




            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }

            LayoutInflater inflater = getLayoutInflater(new Bundle());
            View fbText = inflater.inflate(R.layout.fb_text, null);
            ((TextView)fbText.findViewById(R.id.fb_text_title)).setText("asdfasdf111");
            mLayout.addView(fbText);
        }

        private void setBlock(String title) {
            View fbText = mInflater.inflate(R.layout.fb_block, null);
            ((TextView)fbText.findViewById(R.id.fb_block_title)).setText(title);
            mLayout.addView(fbText);
        }

        private void setText(String title, String value) {
            View fbText = mInflater.inflate(R.layout.fb_text, null);
            ((TextView)fbText.findViewById(R.id.fb_text_title)).setText(title);
            ((EditText)fbText.findViewById(R.id.fb_text_edit)).setText(value);
            mLayout.addView(fbText);
        }


    }
}
