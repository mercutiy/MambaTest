package ru.mamba.test.mambatest.api.controller;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.api.Response;
import ru.mamba.test.mambatest.api.exception.NotAuthException;

public abstract class Controller<Model> {

    private final static String TAG = Controller.class.getSimpleName();

    protected final static String F_BOOL_AUTH = "isAuth";

    protected final static String F_OBJ_PROFILE = "profile";

    protected Request mRequest;

    private JSONObject mResponse;

    private Throwable mError;

    private String mErrorMessage;

    private Model mModel;

    protected abstract Model parseResponse(JSONObject json) throws JSONException;

    public Request getRequest() {
        return mRequest;
    }

    public void setRequest(Request request) {
        mRequest = request;
    }

    public void setResponse(JSONObject response) throws NotAuthException {
        mResponse = response;
        try {
            if (this instanceof Authorise && !mResponse.getBoolean(F_BOOL_AUTH)) {
                throw new NotAuthException();
            }
            setModel(parseResponse(mResponse));
            completeModel();
        } catch (JSONException e) {
            setError(e);
        }
    }

    public Model getModel() {
        return mModel;
    }

    public void setModel(Model model) {
        mModel = model;
    }

    public Throwable getError() {
        return mError;
    }

    public void setError(Throwable error) {
        mError = error;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    protected void completeModel() {

    }
}
