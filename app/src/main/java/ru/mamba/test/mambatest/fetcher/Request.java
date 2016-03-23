package ru.mamba.test.mambatest.fetcher;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Request {

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
}
