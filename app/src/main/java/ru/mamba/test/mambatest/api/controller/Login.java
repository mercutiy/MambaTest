package ru.mamba.test.mambatest.api.controller;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.model.Profile;

public class Login extends Controller<Login.Model> {

    private final static String METHOD = Request.POST;

    private final static String URI = "/login/";

    private final static String F_STR_SECRET = "authSecret";

    private final static String TAG = Login.class.getSimpleName();

    public Login(String login, String password) throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("login", login);
        jsonRequest.put("password", password);
        setRequest(new Request(URI, METHOD, null, jsonRequest));
    }

    @Override
    protected Model parseResponse(JSONObject json) throws JSONException {
        Model response = new Model();
        response.setSuccess(json.getBoolean(F_BOOL_AUTH));
        if (!response.isSuccess()) {
            // todo Возмножны варианты?
            response.setError(json.getJSONObject("errors").getString("internal"));
            return response;
        }
        response.setProfile(new Profile(json.getJSONObject(F_OBJ_PROFILE)));
        response.setAuthSecret(json.getString(F_STR_SECRET));
        return response;
    }

    public class Model {

        private boolean mSuccess;

        private String mError;

        private String mAuthSecret;

        private Profile mProfile;

        public boolean isSuccess() {
            return mSuccess;
        }

        public void setSuccess(boolean success) {
            mSuccess = success;
        }

        public String getError() {
            return mError;
        }

        public void setError(String error) {
            mError = error;
        }

        public String getAuthSecret() {
            return mAuthSecret;
        }

        public void setAuthSecret(String authSecret) {
            mAuthSecret = authSecret;
        }

        public Profile getProfile() {
            return mProfile;
        }

        public void setProfile(Profile profile) {
            mProfile = profile;
        }
    }
}
