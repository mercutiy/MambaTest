package ru.mamba.test.mambatest;

import android.app.Activity;
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

import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.AlbumForm;
import ru.mamba.test.mambatest.api.controller.AlbumNew;
import ru.mamba.test.mambatest.api.controller.Albums;
import ru.mamba.test.mambatest.api.controller.Controller;
import ru.mamba.test.mambatest.api.response.FormBuilder;
import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.Autharize;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.fetcher.Response;
import ru.mamba.test.mambatest.fetcher.Session;
import ru.mamba.test.mambatest.model.form.Block;
import ru.mamba.test.mambatest.model.form.Field;
import ru.mamba.test.mambatest.model.form.SingleSelect;
import ru.mamba.test.mambatest.model.form.Switcher;
import ru.mamba.test.mambatest.model.form.Text;

public class NewAlbumFragment extends Fragment implements Callback1<FormBuilder> {

    private LinearLayout mLayout;

    //private FormFetcher mFetcher;

    private Fetcher mFetcher2;

    private FormBuilder mFormBuilder;

    public NewAlbumFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
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

        /*if (mFetcher == null) {
            mFetcher = new FormFetcher(getActivity());
            mFetcher.execute();
        } else {
            mFetcher.handleResponse();
        }*/

        mFetcher2 = new Fetcher(getActivity(), this);
        mFetcher2.fetch(new AlbumForm());

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit_album) {
            mLayout.removeAllViews();
            /*JSONObject json = mFetcher.getNewRequest();
            mFetcher = new FormFetcher(getActivity());
            mFetcher.execute(new Request("/albums/", Request.POST, null, json));*/
            mFetcher2.fetch(new AlbumNew(mFormBuilder.getForm().getJson()));


            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /*
    private class FormFetcher extends ApiFetcher implements Autharize {

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
            //getActivity().finish();
            Intent intent = new Intent(getActivity(), AlbumsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
                R.layout.fb_single_select_item,
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
    }*/

    @Override
    public void onResponse(FormBuilder formBuilder) {
        View[] views = new FormBuilderDrawer(getLayoutInflater(new Bundle()), formBuilder).getViews();
        for (View view : views) {
            mLayout.addView(view);
        }
    }


    public class FormBuilderDrawer {

        private LayoutInflater mInflater;

        private FormBuilder mFromBuilder;

        public FormBuilderDrawer(LayoutInflater inflater, FormBuilder fromBuilder) {
            mInflater = inflater;
            mFromBuilder = fromBuilder;
        }

        public View[] getViews() {
            List<View> views = new ArrayList<View>();
            for (Block block : mFormBuilder.getForm().getBlocks()) {
                views.add(getBlockView(block));
                for (Field field : block.getFields()) {
                    if (field instanceof Text) {
                        views.add(getTextView((Text)field));
                    } else if (field instanceof Switcher) {
                        views.add(getSwitcherView((Switcher)field));
                    } else if (field instanceof SingleSelect) {
                        views.add(getSingleSelectView((SingleSelect)field));
                    }
                }
            }

            return views.toArray(new View[views.size()]);
        }

        private View getBlockView(Block block) {
            View fbBlock = mInflater.inflate(R.layout.fb_block, null);
            ((TextView)fbBlock.findViewById(R.id.fb_block_title)).setText(block.getTitle());
            if (block.getError() != null) {
                TextView error = (TextView)fbBlock.findViewById(R.id.fb_block_error);
                error.setText(block.getError());
                error.setVisibility(View.VISIBLE);
            }

            return fbBlock;
        }

        private View getTextView(Text text) {
            View fbText = mInflater.inflate(R.layout.fb_text, null);

            ((TextView)fbText.findViewById(R.id.fb_text_title)).setText(text.getTitle());
            EditText edit = (EditText)fbText.findViewById(R.id.fb_text_edit);
            edit.setText(text.getValue());
            if (text.getError() != null) {
                TextView error = (TextView)fbText.findViewById(R.id.fb_text_error);
                error.setText(text.getError());
                error.setVisibility(View.VISIBLE);
            }
            text.setView(fbText);
            // TODO Добавить поддержку desc (описание) во все поля

            return fbText;
        }

        private View getSingleSelectView(SingleSelect singleSelect) {
            View fbSS = mInflater.inflate(R.layout.fb_single_select, null);

            ((TextView)fbSS.findViewById(R.id.fb_ss_title)).setText(singleSelect.getTitle());
            Spinner spinner = (Spinner)fbSS.findViewById(R.id.fb_ss_spinner);
            ArrayAdapter<SingleSelect.Item> adapter = new ArrayAdapter<SingleSelect.Item>(
                getActivity().getApplicationContext(),
                R.layout.fb_single_select_item,
                singleSelect.getVariants()
            );
            spinner.setAdapter(adapter);
            singleSelect.setView(spinner);
            // TODO Добавить поддержку desc (описание) во все поля

            return fbSS;
        }

        private View getSwitcherView(Switcher switcher) {
            View fbSwitcher = mInflater.inflate(R.layout.fb_switcher, null);

            Switch switchView = (Switch)fbSwitcher.findViewById(R.id.fb_switcher);
            switchView.setText(switcher.getTitle());
            switchView.setChecked(switcher.getValue());
            switcher.setView(switchView);
            // TODO Добавить поддержку desc (описание) во все поля

            return fbSwitcher;
        }
    }

}
