package ru.mamba.test.mambatest.api.controller;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;

public class Profile extends Controller<Profile.Model> {

    private final static String METHOD = Request.GET;

    private final static String URI = "/profile/";

    private final static String F_OBJ_PROFILE = "anketa";

    public Profile() {
        setRequest(new Request(URI, METHOD));
    }

    @Override
    protected Model parseResponse(JSONObject json) throws JSONException {
        Model response = new Model();

        response.setProfile(new ru.mamba.test.mambatest.model.Profile(json.getJSONObject(F_OBJ_PROFILE)));

        return response;
    }

    public class Model {

        ru.mamba.test.mambatest.model.Profile mProfile;

        public ru.mamba.test.mambatest.model.Profile getProfile() {
            return mProfile;
        }

        public void setProfile(ru.mamba.test.mambatest.model.Profile profile) {
            mProfile = profile;
        }
    }
}
