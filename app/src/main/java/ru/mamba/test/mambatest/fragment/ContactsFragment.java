package ru.mamba.test.mambatest.fragment;

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

import java.util.ArrayList;

import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.activity.Anketa;
import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.Contacts;
import ru.mamba.test.mambatest.api.image.PhotoFetcher;
import ru.mamba.test.mambatest.api.Session;
import ru.mamba.test.mambatest.model.Contact;

public class ContactsFragment
    extends Fragment
    implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, Callback1<Contacts.Model>
{

    private ContactAdapter mContactAdapter;

    private PhotoFetcher<ru.mamba.test.mambatest.model.Anketa> mPhotoFetcher;

    int mTotal = -1;

    int mCurrentTotal = -1;

    int mFolderId;

    private Fetcher mFetcher;

    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoFetcher = new PhotoFetcher<ru.mamba.test.mambatest.model.Anketa>(new Handler());
        mPhotoFetcher.setListener(new PhotoFetcher.Listener<ru.mamba.test.mambatest.model.Anketa>() {
            @Override
            public void onPhotoDownloaded(ru.mamba.test.mambatest.model.Anketa anketa, Bitmap bitmap) {
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

            ru.mamba.test.mambatest.model.Anketa anketa = contact.getAnketa();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = (Contact)mContactAdapter.getItem(position);
        if (contact.getAnketa().isDeleted()) {
            return;
        }

        Intent intent = new Intent(getActivity(), Anketa.class);
        intent.putExtra(Anketa.EXTRA_ANKETA_ID, contact.getAnketa().getId());
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
