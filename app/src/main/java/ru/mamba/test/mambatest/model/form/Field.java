package ru.mamba.test.mambatest.model.form;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.model.Model;

public class Field extends Model {

    private String title;

    private String type;

    private String error;

    public Field(JSONObject json) throws JSONException {
        super(json);
    }

}
