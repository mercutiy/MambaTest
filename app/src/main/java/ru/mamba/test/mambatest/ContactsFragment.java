package ru.mamba.test.mambatest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.Contacts;
import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.Autharize;
import ru.mamba.test.mambatest.fetcher.ImageFetcher;
import ru.mamba.test.mambatest.fetcher.PhotoFetcher;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.fetcher.Response;
import ru.mamba.test.mambatest.fetcher.Session;
import ru.mamba.test.mambatest.model.Anketa;
import ru.mamba.test.mambatest.model.Contact;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment
    extends Fragment
    implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, Callback1<Contacts.Model>
{

    private ContactAdapter mContactAdapter;

    private PhotoFetcher<Anketa> mPhotoFetcher;

    int mTotal = -1;

    int mCurrentTotal = -1;

    ListView mListView;

    int mFolderId;

    private Fetcher mFetcher;

    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoFetcher = new PhotoFetcher<Anketa>(new Handler());
        mPhotoFetcher.setListener(new PhotoFetcher.Listener<Anketa>() {
            @Override
            public void onPhotoDownloaded(Anketa anketa, Bitmap bitmap) {
                anketa.setPhoto(bitmap);
                if (isVisible()) {
                    mContactAdapter.notifyDataSetChanged();
                }
            }
        });
        mPhotoFetcher.start();
        mPhotoFetcher.getLooper();

        mFolderId = Session.getInstance(getActivity()).getFolderId();
        mFetcher = new Fetcher(getActivity(), this);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPhotoFetcher.quit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPhotoFetcher.clearQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        if (mContactAdapter == null) {
            mContactAdapter = new ContactAdapter(new ArrayList<Contact>());
        }

        ListView listView = (ListView)view.findViewById(R.id.list_view_contacts);
        listView.setAdapter(mContactAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        return view;
    }

    private class ContactAdapter extends ArrayAdapter<Contact> {

        public ContactAdapter(ArrayList<Contact> contacts) {
            super(getActivity(), 0, contacts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.contact_item, null);
            }

            Contact contact = getItem(position);

            TextView nameAgeView = (TextView)convertView.findViewById(R.id.text_view_contact_name);
            TextView messagesView = (TextView)convertView.findViewById(R.id.text_view_contact_messages);

            // TODO Заменить конкатеницию
            nameAgeView.setText(contact.getAnketa().getName() + ", " + String.valueOf(contact.getAnketa().getAge()));
            messagesView.setText(getResources().getQuantityString(R.plurals.number_of_messages, contact.getMessages(), contact.getMessages()));

            Bitmap noPhoto = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.nophoto);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view_contact_photo);

            Anketa anketa = contact.getAnketa();
            if (anketa.getPhotoSrc() == null || "".equals(anketa.getPhotoSrc())) {
                imageView.setImageBitmap(noPhoto);
            } else if (anketa.getPhoto() != null) {
                imageView.setImageBitmap(anketa.getPhoto());
            } else {
                mPhotoFetcher.queueThumbnail(anketa, anketa.getPhotoSrc());
            }

            return convertView;
        }
    }

    /*private class ContactFetcher extends ApiFetcher implements Autharize {

        public ContactFetcher(Activity activity) {
            super(activity);
        }

        @Override
        protected void uiExecute(Response response) throws JSONException {

            JSONObject json = response.getJson();

            int contactsCount = json.getJSONObject("folder").getInt("count");

            JSONArray contactsJson = json.getJSONArray("contacts");

            for (int i = 0; i < contactsJson.length(); i++) {
                JSONObject contactJson = contactsJson.getJSONObject(i);
                JSONObject anketaJson = contactJson.getJSONObject("anketa");
                Contact contact = new Contact(
                    contactJson.getInt("contactId"),
                    anketaJson.getInt("id"),
                    anketaJson.getString("name"),
                    contactJson.getInt("messages")
                );

                if (anketaJson.has("deleted")) {
                    contact.setDeleted(anketaJson.getBoolean("deleted"));
                }

                if (!contact.isDeleted()) {
                    contact.setAge(anketaJson.getInt("age"));
                    contact.setPhoto(anketaJson.getString("squarePhotoUrl"));
                }

                mContactAdapter.add(contact);
            }

            if (mTotal == -1) {
                mTotal = contactsCount;
            }

            ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.setTitle(getResources().getQuantityString(R.plurals.number_of_contacts, contactsCount, contactsCount));
            }
        }
    }*/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = (Contact)mContactAdapter.getItem(position);
        if (contact.getAnketa().isDeleted()) {
            return;
        }

        Intent intent = new Intent(getActivity(), AnketaActivity.class);
        intent.putExtra(AnketaActivity.EXTRA_ANKETA_ID, contact.getAnketa().getId());
        startActivity(intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int limit = 10;
        if (
            firstVisibleItem + visibleItemCount >= totalItemCount &&
            (totalItemCount >= mCurrentTotal && mCurrentTotal + limit <= mTotal) || mCurrentTotal == -1
        ) {
            mCurrentTotal = totalItemCount + limit;
            mFetcher = new Fetcher(getActivity(), this);
            mFetcher.fetch(new Contacts(mFolderId, totalItemCount, 10));
        }
    }

    @Override
    public void onResponse(Contacts.Model model) {
        int contactsCount = model.getContactCount();

        for (Contact contact : model.getContacts()) {
            mContactAdapter.add(contact);
        }

        if (mTotal == -1) {
            mTotal = contactsCount;
        }

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getResources().getQuantityString(R.plurals.number_of_contacts, contactsCount, contactsCount));
        }
    }
}
