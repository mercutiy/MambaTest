package ru.mamba.test.mambatest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                for (int i = 0; i < blocks.length(); i++) {
                    JSONObject block = blocks.getJSONObject(i);
                    setBlock(block);
                    JSONArray fields = block.getJSONArray("fields");
                    for (int j = 0; j < fields.length(); j++) {
                        JSONObject field = fields.getJSONObject(j);
                        String type = field.getString("inputType");
                        if ("Text".equals(type)) {
                            setText(field);
                        } else if ("Switcher".equals(type)) {
                            setSwitcher(field);
                        } else if ("SingleSelect".equals(type)) {
                            setSingleSelect(field);
                        } else {
                            Log.v(TAG, "Unknown fb type");
                        }
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }
        }

        private void setBlock(JSONObject block) throws JSONException {
            View fbBlock = mInflater.inflate(R.layout.fb_block, null);

            ((TextView)fbBlock.findViewById(R.id.fb_block_title)).setText(block.getString("name"));
            if (block.has("error")) {
                TextView error = (TextView)fbBlock.findViewById(R.id.fb_block_error);
                error.setText(block.getString("error"));
                error.setVisibility(View.VISIBLE);
            }
            mLayout.addView(fbBlock);
        }

        private void setText(JSONObject text) throws JSONException {
            View fbText = mInflater.inflate(R.layout.fb_text, null);

            ((TextView)fbText.findViewById(R.id.fb_text_title)).setText(text.getString("name"));
            ((EditText)fbText.findViewById(R.id.fb_text_edit)).setText(text.getString("value"));
            if (text.has("error")) {
                TextView error = (TextView)fbText.findViewById(R.id.fb_text_error);
                error.setText(text.getString("error"));
                error.setVisibility(View.VISIBLE);
            }
            // TODO Добавить поддержку desc (описание) во все поля

            mLayout.addView(fbText);
        }

        private void setSwitcher(JSONObject switcher) throws JSONException {
            View fbSwitcher = mInflater.inflate(R.layout.fb_switcher, null);

            Switch switchView = (Switch)fbSwitcher.findViewById(R.id.fb_switcher);
            switchView.setText(switcher.getString("name"));
            switchView.setChecked(switcher.getBoolean("value"));

            mLayout.addView(fbSwitcher);
        }

        private void setSingleSelect(JSONObject singleSelect) throws JSONException {
            View fbSS = mInflater.inflate(R.layout.fb_single_select, null);

            ((TextView)fbSS.findViewById(R.id.fb_ss_title)).setText(singleSelect.getString("name"));
            Spinner spinner = (Spinner)fbSS.findViewById(R.id.fb_ss_spinner);
            List<Item> options = new ArrayList<Item>();

            JSONArray variants = singleSelect.getJSONArray("variants");
            for (int k = 0; k < variants.length(); k++) {
                JSONObject variant = variants.getJSONObject(k);
                options.add(new Item(variant.getString("key"), variant.getString("name")));
            }

            ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item,
                options
            );
            spinner.setAdapter(adapter);

            mLayout.addView(fbSS);
        }
    }

    private class Item {

        private String mKey;

        private String mValue;

        public Item(String key, String value) {
            mKey = key;
            mValue = value;
        }

        public String getKey() {
            return mKey;
        }

        public String getValue() {
            return mValue;
        }

        public String toString() {
            return getValue();
        }
    }
}
