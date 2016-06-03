package ru.mamba.test.mambatest;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.AlbumForm;
import ru.mamba.test.mambatest.api.controller.AlbumNew;
import ru.mamba.test.mambatest.api.response.FormBuilder;
import ru.mamba.test.mambatest.model.form.Block;
import ru.mamba.test.mambatest.model.form.Field;
import ru.mamba.test.mambatest.model.form.SingleSelect;
import ru.mamba.test.mambatest.model.form.Switcher;
import ru.mamba.test.mambatest.model.form.Text;

public class NewAlbumFragment extends Fragment implements Callback1<FormBuilder> {

    private LinearLayout mLayout;

    private Fetcher mFetcher;

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

        mFetcher = new Fetcher(getActivity(), this);
        mFetcher.fetch(new AlbumForm());

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit_album) {
            mLayout.removeAllViews();
            mFetcher = new Fetcher(getActivity(), this);
            mFetcher.fetch(new AlbumNew(mFormBuilder.getForm().getJson()));
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(FormBuilder formBuilder) {
        mFormBuilder = formBuilder;
        if (formBuilder.getMessage() != null) {
            Toast.makeText(getActivity(), formBuilder.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AlbumsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        View[] views = new FormBuilderDrawer(getLayoutInflater(new Bundle()), mFormBuilder).getViews();
        for (View view : views) {
            mLayout.addView(view);
        }
    }


    public class FormBuilderDrawer {

        private LayoutInflater mInflater;

        private FormBuilder mForm;

        public FormBuilderDrawer(LayoutInflater inflater, FormBuilder formBuilder) {
            mInflater = inflater;
            mForm = formBuilder;
        }

        public View[] getViews() {
            List<View> views = new ArrayList<View>();
            for (Block block : mForm.getForm().getBlocks()) {
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
            text.setView(edit);
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
