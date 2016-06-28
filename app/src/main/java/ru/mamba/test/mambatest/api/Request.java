package ru.mamba.test.mambatest.api;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private static final String API_SCHEME = "http";

    private static final String API_DOMAIN = "api.mobile-api.ru";

    private static final String API_VERSION = "v5.2.20.0";

    public static final String GET = "GET";

    public static final String POST = "POST";

    public static final String PUT = "PUT";

    public static final String DELETE = "DELETE";

    private String mPath;

    private String mMethod;

    private Map<String, String> mParams;

    private JSONObject mPost;

    public Request(String path, String method, Map<String, String> params, JSONObject post) {
        mMethod = method;
        mParams = params;
        mPath = path;
        mPost = post;
    }

    public Request(String path, String method, Map<String, String> params) {
        mMethod = method;
        mParams = params;
        mPath = path;
        mPost = null;
    }

    public Request(String path, String method) {
        mMethod = method;
        mParams = new HashMap<String, String>();
        mPath = path;
        mPost = null;
    }

    public Request(String path) {
        mMethod = GET;
        mParams = new HashMap<String, String>();
        mPath = path;
        mPost = null;
    }

    public String getMethod() {
        return mMethod;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public String getPath() {
        return mPath;
    }

    public JSONObject getPost() {
        return mPost;
    }

    public URL getURL() throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(API_SCHEME).authority(API_DOMAIN).appendPath(API_VERSION).appendEncodedPath(getPath());
        if (getParams() != null) {
            for (Map.Entry<String, String> entry : getParams().entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return new URL(builder.toString());
    }

    public String getRawPost() {
        return getPost().toString();
    }

    public JSONObject getRequestForBatch() throws JSONException {
        JSONObject request = new JSONObject();
        request.put("method", getMethod());
        request.put("uri", getPath());
        request.put("params", getPost() != null ? getPost() : new JSONObject());
        return request;
    }
}
