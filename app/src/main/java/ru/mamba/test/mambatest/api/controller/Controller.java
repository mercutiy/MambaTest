package ru.mamba.test.mambatest.api.controller;

import android.widget.TableRow;

import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.api.Response;

public abstract class Controller<Model> {

    protected Request mRequest;

    private Response mResponse;

    private Model mModel;

    protected abstract Model parseResponse(JSONObject json);

    public Request getRequest() {
        return mRequest;
    }

    public void setRequest(Request request) {
        mRequest = request;
    }

    public void setResponse(Response response) {
        mResponse = response;
        mModel = parseResponse(response.getJson());
    }

    public Model getModel() {
        return mModel;
    }
}
