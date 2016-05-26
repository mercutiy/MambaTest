package ru.mamba.test.mambatest.model.form;

import org.json.JSONException;
import org.json.JSONObject;

public class Switcher extends Field {

    private final static String F_BOOL_VALUE = "value";

    private boolean mValue;

    public Switcher(JSONObject json) throws JSONException {
        super(json);
        setValue(json.getBoolean(F_BOOL_VALUE));
    }

    public boolean isValue() {
        return mValue;
    }

    public void setValue(boolean value) {
        mValue = value;
    }

}
