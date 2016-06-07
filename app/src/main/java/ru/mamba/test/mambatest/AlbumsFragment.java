package ru.mamba.test.mambatest;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.Albums;
import ru.mamba.test.mambatest.api.controller.Controller;
import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.fetcher.PhotoFetcher;
import ru.mamba.test.mambatest.api.Session;
import ru.mamba.test.mambatest.model.Album;

public class AlbumsFragment extends Fragment implements AdapterView.OnItemClickListener, Callback1<Albums.Model> {

    private AlbumAdapter mAlbumAdapter;

    private PhotoFetcher<Album> mPhotoFetcher;

    public AlbumsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoFetcher = new PhotoFetcher<Album>(new Handler());
        mPhotoFetcher.setListener(new PhotoFetcher.Listener<Album>() {
            @Override
            public void onPhotoDownloaded(Album album, Bitmap bitmap) {
            album.setPhotoBitmap(bitmap);
            if (isVisible()) {
                mAlbumAdapter.notifyDataSetChanged();
            }
            }
        });
        mPhotoFetcher.start();
        mPhotoFetcher.getLooper();
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
        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        mAlbumAdapter = new AlbumAdapter(new ArrayList<Album>());
        ListView listView = (ListView)view.findViewById(R.id.list_view_albums);
        listView.setAdapter(mAlbumAdapter);
        listView.setOnItemClickListener(this);

        Fetcher albumsFetcher = new Fetcher(getActivity(), this);
        Controller controller = new Albums(Session.getInstance(getActivity()).getAnketaId(), true, 1);
        albumsFetcher.fetch(controller);

        return view;
    }

    private class AlbumAdapter extends ArrayAdapter<Album> {

        public AlbumAdapter(ArrayList<Album> albums) {
            super(getActivity(), 0, albums);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.album_item, null);
            }

            Album album = getItem(position);

            TextView titleView = (TextView)convertView.findViewById(R.id.text_view_album);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view_album_photo);

            titleView.setText(album.getTitle());

            Bitmap noPhoto = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.nophoto);
            if (album.getPhoto() == null || "".equals(album.getPhoto())) {
                imageView.setImageBitmap(noPhoto);
            } else if (album.getPhotoBitmap() == null) {
                mPhotoFetcher.queueThumbnail(album, album.getPhoto());
            } else {
                imageView.setImageBitmap(album.getPhotoBitmap());
            }

            return convertView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album album = (Album)mAlbumAdapter.getItem(position);
    }

    @Override
    public void onResponse(Albums.Model model) {
        for (Album album : model.getAlbums()) {
            mAlbumAdapter.add(album);
        }

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getResources().getQuantityString(R.plurals.number_of_albums, model.getAlbums().length, model.getAlbums().length));
        }

    }
}
