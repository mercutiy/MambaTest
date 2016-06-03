package ru.mamba.test.mambatest.api.controller;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;

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
}
