package ru.mamba.test.mambatest.api.controller;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.api.response.FormBuilder;

public abstract class Form extends Controller<FormBuilder> {

    private final static String F_STR_MESSAGE = "message";

    private final static String F_OBJ_FORM = "formBuilder";

    @Override
    protected FormBuilder parseResponse(JSONObject json) throws JSONException {
        FormBuilder response = new FormBuilder();
        if (json.has(F_STR_MESSAGE)) {
            response.setMessage(json.getString(F_STR_MESSAGE));
        }
        response.setForm(new ru.mamba.test.mambatest.model.Form(json.getJSONObject(F_OBJ_FORM)));

        return response;
    }
}
