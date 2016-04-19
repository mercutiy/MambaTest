package ru.mamba.test.mambatest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.Autharize;
import ru.mamba.test.mambatest.fetcher.PhotoFetcher;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.fetcher.Response;
import ru.mamba.test.mambatest.fetcher.Session;
import ru.mamba.test.mambatest.model.Album;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlbumsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private AlbumAdapter mAlbumAdapter;

    private PhotoFetcher<Album> mPhotoFetcher;

    private AlbumFetcher mFetcher;

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
             }
        );
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

        if (mFetcher == null) {
            mFetcher = new AlbumFetcher(getActivity());
            mFetcher.execute();
        } else {
            mFetcher.handleResponse();
        }

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

    private class AlbumFetcher extends ApiFetcher implements Autharize {


        public AlbumFetcher(Activity activity) {
            super(activity);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("photos", "true");
            params.put("limit", "1");
            int anketaId = new Session(activity).getAnketaId();
            setRequest(new Request("/users/" + String.valueOf(anketaId) + "/albums/", Request.GET, params));
        }


        @Override
        protected void uiExecute(Response response) throws JSONException {
            JSONArray albumsJson = response.getJson().getJSONArray("albums");
            int albumsCount = albumsJson.length();
            for (int i = 0; i < albumsCount; i++) {
                JSONObject albumJson = albumsJson.getJSONObject(i);
                Album album = new Album(
                    albumJson.getInt("id"),
                    albumJson.getString("name"),
                    albumJson.isNull("photos") ? null : albumJson.getJSONArray("photos").getJSONObject(0).getString("squarePhotoUrl")
                );
                mAlbumAdapter.add(album);
            }

            ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.setTitle(getResources().getQuantityString(R.plurals.number_of_albums, albumsCount, albumsCount));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album album = (Album)mAlbumAdapter.getItem(position);
    }
}
