package ru.mamba.test.mambatest;

import android.content.Context;
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
import ru.mamba.test.mambatest.fetcher.PhotoFetcher;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.model.Album;

/**
 * A placeholder fragment containing a simple view.
 */
public class AlbumsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private AlbumAdapter mAlbumAdapter;

    private PhotoFetcher<ImageView> mPhotoFetcher;

    private MenuItem mMenuAdd;

    public AlbumsFragment() {
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
             }
        );
        mPhotoFetcher.start();
        mPhotoFetcher.getLooper();
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

        new AlbumFetcher(getActivity()).execute();

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

            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view_album_photo);
            TextView titleView = (TextView)convertView.findViewById(R.id.text_view_album);

            //Picasso.with(getActivity()).load(album.getImage()).into(image);
            mPhotoFetcher.queueThumbnail(imageView, album.getPhoto());
            titleView.setText(album.getTitle());

            return convertView;
        }
    }

    private class AlbumFetcher extends ApiFetcher {

        private String TAG = AlbumFetcher.class.getCanonicalName();

        public AlbumFetcher(Context context) {
            super(context);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("photos", "true");
            params.put("limit", "1");
            mRequest = new Request("/users/634593392/albums/", Request.GET, params);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray albumsJson = json.getJSONArray("albums");
                int albumsCount = albumsJson.length();
                for (int i = 0; i < albumsCount; i++) {
                    JSONObject albumJson = albumsJson.getJSONObject(i);
                    Album album = new Album(
                        albumJson.getInt("id"),
                        albumJson.getString("name"),
                        albumJson.getString("coverUrl")
                    );
                    mAlbumAdapter.add(album);
                }

                ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
                if (ab != null) {
                    ab.setTitle(getResources().getQuantityString(R.plurals.number_of_albums, albumsCount, albumsCount));
                }


            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album album = (Album)mAlbumAdapter.getItem(position);
    }
}
