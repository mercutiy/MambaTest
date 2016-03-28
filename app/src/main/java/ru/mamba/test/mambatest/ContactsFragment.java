package ru.mamba.test.mambatest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.PhotoFetcher;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.model.Album;
import ru.mamba.test.mambatest.model.Contact;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ContactAdapter mContactAdapter;

    private PhotoFetcher<ImageView> mPhotoFetcher;

    public ContactsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoFetcher = new PhotoFetcher<ImageView>(new Handler());
        mPhotoFetcher.setListener(new PhotoFetcher.Listener<ImageView>() {
            @Override
            public void onPhotoDownloaded(ImageView imageView, Bitmap bitmap) {
                if (isVisible()) {
                       imageView.setImageBitmap(bitmap);
                }
            }
        });
        mPhotoFetcher.start();
        mPhotoFetcher.getLooper();

        setHasOptionsMenu(true);
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

        mContactAdapter = new ContactAdapter(new ArrayList<Contact>());

        ListView listView = (ListView)view.findViewById(R.id.list_view_contacts);
        listView.setAdapter(mContactAdapter);
        listView.setOnItemClickListener(this);

        new ContactFetcher(getActivity()).execute();


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

            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view_contact_photo);
            TextView nameAgeView = (TextView)convertView.findViewById(R.id.text_view_contact_name);
            TextView messagesView = (TextView)convertView.findViewById(R.id.text_view_contact_messages);

            mPhotoFetcher.queueThumbnail(imageView, contact.getPhoto());
            // TODO Заменить конкатеницию
            nameAgeView.setText(contact.getName() + ", " + String.valueOf(contact.getAge()));
            messagesView.setText(getResources().getQuantityString(R.plurals.number_of_messages, contact.getMessages(), contact.getMessages()));

            return convertView;
        }
    }

    private class ContactFetcher extends ApiFetcher {

        private String TAG = ContactFetcher.class.getCanonicalName();

        public ContactFetcher(Context context) {
            super(context);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("limit", "10");
            mRequest = new Request("/folders/390353/contacts/", Request.GET, params);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
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

                    try {
                        contact.setDeleted(anketaJson.getBoolean("deleted"));
                    } catch (JSONException e) {
                        contact.setDeleted(false);
                    }

                    if (!contact.isDeleted()) {
                        contact.setAge(anketaJson.getInt("age"));
                        contact.setPhoto(anketaJson.getString("squarePhotoUrl"));
                    }
                    // TODO Сделать поддержку удаленной анкеты

                    mContactAdapter.add(contact);
                }

                ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
                if (ab != null) {
                    ab.setTitle(getResources().getQuantityString(R.plurals.number_of_contacts, contactsCount, contactsCount));
                }


            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = (Contact)mContactAdapter.getItem(position);
        if (contact.isDeleted()) {
            return;
        }

        Intent intent = new Intent(getActivity(), AnketaActivity.class);
        intent.putExtra(AnketaActivity.EXTRA_ANKETA_ID, contact.getAnketaId());
        startActivity(intent);
    }
}
