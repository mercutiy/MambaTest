package ru.mamba.test.mambatest.fetcher;

import org.json.JSONObject;

public class Response {

    JSONObject mJson;

    FetchException mError;

    public Response(JSONObject json) {
        mJson = json;
        mError = null;
    }

    public Response(FetchException error) {
        mError = error;
        mJson = null;
    }

    public JSONObject getJson() {
        return mJson;
    }

    public FetchException getError() {
        return mError;
    }

    public void setError(FetchException error) {
        mError = error;
    }

    public void setJson(JSONObject json) {
        mJson = json;
    }
}
