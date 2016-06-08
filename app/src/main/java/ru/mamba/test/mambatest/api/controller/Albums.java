package ru.mamba.test.mambatest.api.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.model.Album;

public class Albums extends Controller<Albums.Model> {

    private final static String F_LST_ALBUMS = "albums";

    private final static String METHOD = Request.GET;

    private final static String URI = "/users/%d/albums/";

    public Albums(int anketaId, boolean isPhotos, int limit) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("photos", isPhotos ? "true" : "false");
        if (limit != 0) {
            params.put("limit", String.valueOf(limit));
        }
        setRequest(new Request(getUri(anketaId), METHOD, params));
    }

    @Override
    protected Model parseResponse(JSONObject json) throws JSONException {
        Model response = new Model();

        JSONArray jsonAlbums = json.getJSONArray(F_LST_ALBUMS);
        List<Album> albums = new ArrayList<Album>();
        for (int i = 0; i < jsonAlbums.length(); i++) {
            albums.add(new Album(jsonAlbums.getJSONObject(i)));
        }
        response.setAlbums(albums.toArray(new Album[albums.size()]));

        return response;
    }

    public class Model {
        private Album[] mAlbums;

        public Album[] getAlbums() {
            return mAlbums;
        }

        public void setAlbums(Album[] albums) {
            mAlbums = albums;
        }
    }

    protected String getUri(int anketaId) {
         return String.format(URI, anketaId);
    }
}
