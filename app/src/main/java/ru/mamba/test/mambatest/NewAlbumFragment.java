package ru.mamba.test.mambatest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import ru.mamba.test.mambatest.fetcher.ApiFetcher2;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.fetcher.Response;

public class NewAlbumFragment extends Fragment {

    private LinearLayout mLayout;

    private FormFetcher mFetcher;

    public NewAlbumFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_album, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_album, container, false);
        mLayout = (LinearLayout)view.findViewById(R.id.layout_form);

        mFetcher = new FormFetcher(getActivity());
        mFetcher.execute();


        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit_album) {
            mLayout.removeAllViews();
            JSONObject json = mFetcher.getNewRequest();
            new FormFetcher(getActivity()).execute(new Request("/albums/", Request.POST, null, json));

        }
        return super.onOptionsItemSelected(item);
    }

    private class FormFetcher extends ApiFetcher2 {

        private LayoutInflater mInflater;

        private Map<String, View[]> mFormMap = new HashMap<String, View[]>();


        public FormFetcher(Activity activity) {
            super(activity);
            setRequest(new Request("/albums/new/"));
        }

        @Override
        protected void uiExecute(Response response) throws JSONException {

            JSONObject json = response.getJson();

            if (json.has("message")) {
                String message = json.getString("message");
                success(message);
                return;
            }

            JSONArray blocks = json.getJSONObject("formBuilder").getJSONArray("blocks");
            mInflater = getLayoutInflater(new Bundle());

            for (int i = 0; i < blocks.length(); i++) {
                JSONObject block = blocks.getJSONObject(i);
                addBlock(block);
                JSONArray fields = block.getJSONArray("fields");
                List<View> blockFields = new ArrayList<View>();
                for (int j = 0; j < fields.length(); j++) {
                    JSONObject field = fields.getJSONObject(j);
                    String type = field.getString("inputType");
                    if ("Text".equals(type)) {
                        blockFields.add(addText(field));
                    } else if ("Switcher".equals(type)) {
                        blockFields.add(addSwitcher(field));
                    } else if ("SingleSelect".equals(type)) {
                        blockFields.add(addSingleSelect(field));
                    } else {
                        Log.v(TAG, "Unknown fb type");
                    }
                }

                mFormMap.put(block.getString("field"), blockFields.toArray(new View[blockFields.size()]));
            }
        }

        public JSONObject getNewRequest() {
            JSONObject result = new JSONObject();

            for (Map.Entry<String, View[]> entry : mFormMap.entrySet()) {
                JSONObject block = new JSONObject();
                try {
                    for (View view : entry.getValue()) {
                        block.put((String)view.getTag(), getValue(view));
                    }
                    result.put(entry.getKey(), block);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing json", e);
                }
            }

            Log.v(TAG, result.toString());

            return result;
        }

        private Object getValue(View view) {
            if (view instanceof EditText) {
                return ((EditText)view).getText().toString();
            } else if (view instanceof Spinner) {
                return ((Item)((Spinner)view).getSelectedItem()).getKey();
            } else if (view instanceof Switch) {
                return ((Switch)view).isChecked();
            }
            return null;
        }

        private void success(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        private void addBlock(JSONObject block) throws JSONException {
            View fbBlock = mInflater.inflate(R.layout.fb_block, null);

            ((TextView)fbBlock.findViewById(R.id.fb_block_title)).setText(block.getString("name"));
            if (block.has("error")) {
                TextView error = (TextView)fbBlock.findViewById(R.id.fb_block_error);
                error.setText(block.getString("error"));
                error.setVisibility(View.VISIBLE);
            }
            mLayout.addView(fbBlock);
        }

        private View addText(JSONObject text) throws JSONException {
            View fbText = mInflater.inflate(R.layout.fb_text, null);

            ((TextView)fbText.findViewById(R.id.fb_text_title)).setText(text.getString("name"));
            EditText edit = (EditText)fbText.findViewById(R.id.fb_text_edit);
            edit.setText(text.getString("value"));
            edit.setTag(text.getString("field"));
            if (text.has("error")) {
                TextView error = (TextView)fbText.findViewById(R.id.fb_text_error);
                error.setText(text.getString("error"));
                error.setVisibility(View.VISIBLE);
            }
            // TODO Добавить поддержку desc (описание) во все поля

            mLayout.addView(fbText);

            return edit;
        }

        private View addSwitcher(JSONObject switcher) throws JSONException {
            View fbSwitcher = mInflater.inflate(R.layout.fb_switcher, null);

            Switch switchView = (Switch)fbSwitcher.findViewById(R.id.fb_switcher);
            switchView.setText(switcher.getString("name"));
            switchView.setChecked(switcher.getBoolean("value"));
            switchView.setTag(switcher.getString("field"));

            mLayout.addView(fbSwitcher);

            return switchView;
        }

        private View addSingleSelect(JSONObject singleSelect) throws JSONException {
            View fbSS = mInflater.inflate(R.layout.fb_single_select, null);

            ((TextView)fbSS.findViewById(R.id.fb_ss_title)).setText(singleSelect.getString("name"));
            Spinner spinner = (Spinner)fbSS.findViewById(R.id.fb_ss_spinner);
            spinner.setTag(singleSelect.getString("field"));
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

            return spinner;
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
