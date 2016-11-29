package ru.mamba.test.mambatest.api.controller;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.api.image.ImageFetcher;

public class Anketa extends Controller<Anketa.Model> {

    private final static String METHOD = Request.GET;

    private final static String URI = "/users/%d/";

    private final static String F_OBJ_ANKETA = "anketa";

    public Anketa(int anketaId) {
        setRequest(new Request(getUri(anketaId), METHOD));
    }

    @Override
    protected Model parseResponse(JSONObject json) throws JSONException {
        Model model = new Model();
        JSONObject jsonAnketa = json.getJSONObject(F_OBJ_ANKETA);
        model.setAnketa(new ru.mamba.test.mambatest.model.Anketa(jsonAnketa));

        return model;
    }

    public class Model {

        ru.mamba.test.mambatest.model.Anketa mAnketa;

        public ru.mamba.test.mambatest.model.Anketa getAnketa() {
            return mAnketa;
        }

        public void setAnketa(ru.mamba.test.mambatest.model.Anketa anketa) {
            mAnketa = anketa;
        }
    }

    private String getUri(int anketaId) {
        return String.format(URI, anketaId);
    }

    @Override
    protected void completeModel() {
        super.completeModel();
        try {
            Bitmap photo = new ImageFetcher().fetchImage(getModel().getAnketa().getPhoto().getUrl());
            getModel().getAnketa().getPhoto().setBitmap(photo);
        } catch (IOException e) {
            // todo Do nothing since it's just a photo or not?
        }
    }

}
