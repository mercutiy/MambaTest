package ru.mamba.test.mambatest.api.controller;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;

public class SecretAuth extends Controller {

    private final static String METHOD = Request.POST;

    private final static String URI = "/login/secret/";

    private final static String SECRET = "secret";

    public SecretAuth(String secret) throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(SECRET, secret);
        setRequest(new Request(URI, METHOD, null, jsonRequest));
    }

    @Override
    protected Object parseResponse(JSONObject json) throws JSONException {
        return null;
    }
}
