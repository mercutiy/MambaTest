package ru.mamba.test.mambatest.api.controller;

import org.json.JSONObject;

import java.util.HashMap;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.model.Album;

public class Albums extends Controller<Albums.Model> {

    public Albums(int anketaId, boolean isPhotos, int limit) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("photos", isPhotos ? "true" : "false");
        params.put("limit", String.valueOf(limit));
        setRequest(new Request("/users/" + String.valueOf(anketaId) + "/albums/", Request.GET, params));
    }

    @Override
    protected Model parseResponse(JSONObject json) {
        return new Model();
    }

    public class Model {
        Album[] mAlbums;

        public Album[] getAlbums() {
            return mAlbums;
        }

        public void setAlbums(Album[] albums) {
            mAlbums = albums;
        }
    }

}
