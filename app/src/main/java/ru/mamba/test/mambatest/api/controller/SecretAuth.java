package ru.mamba.test.mambatest.api.controller;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;

public class SecretAuth extends Controller<SecretAuth.Model> implements Authorise  {

    private final static String METHOD = Request.POST;

    private final static String URI = "/login/secret/";

    private final static String A_STR_SECRET = "secret";

    public SecretAuth(String secret) throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(A_STR_SECRET, secret);
        setRequest(new Request(URI, METHOD, null, jsonRequest));
    }

    @Override
    protected Model parseResponse(JSONObject json) throws JSONException {
        Model response = new Model();

        if (json.has(F_INT_ERROR_CODE)) {
            response.setErrorCode(json.getInt(F_INT_ERROR_CODE));
        }

        return response;
    }

    public class Model {

        private int mErrorCode;

        public int getErrorCode() {
            return mErrorCode;
        }

        public void setErrorCode(int errorCode) {
            mErrorCode = errorCode;
        }
    }
}
