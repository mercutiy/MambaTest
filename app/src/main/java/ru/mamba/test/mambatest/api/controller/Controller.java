package ru.mamba.test.mambatest.api.controller;

import android.util.Log;
import android.widget.TableRow;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.api.Response;

public abstract class Controller<Model> {

    private final static String TAG = Controller.class.getSimpleName();

    protected final static String F_BOOL_AUTH = "isAuth";

    protected final static String F_OBJ_PROFILE = "profile";

    protected Request mRequest;

    private Response mResponse;

    private Model mModel;

    protected abstract Model parseResponse(JSONObject json) throws JSONException;

    public Request getRequest() {
        return mRequest;
    }

    public void setRequest(Request request) {
        mRequest = request;
    }

    public void setResponse(Response response) {
        mResponse = response;
        try {
            mModel = parseResponse(response.getJson());
        } catch (JSONException e) {
            Log.e(TAG, "unpredictable json", e);
            mResponse.setException(e);
        }
    }

    public Model getModel() {
        return mModel;
    }
}
