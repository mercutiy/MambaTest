package ru.mamba.test.mambatest.api;
import org.json.JSONObject;

public class Response {

    JSONObject mJson;
    Throwable mException;

    public Response(JSONObject json) {
        mJson = json;
        mException = null;
    }

    public Response(Throwable exception) {
        mException = exception;
        mJson = null;
    }

    public Throwable getException() {
        return mException;
    }

    public void setException(Throwable exception) {
        mException = exception;
    }

    public JSONObject getJson() {
        return mJson;
    }

    public void setJson(JSONObject json) {
        mJson = json;
    }
}
