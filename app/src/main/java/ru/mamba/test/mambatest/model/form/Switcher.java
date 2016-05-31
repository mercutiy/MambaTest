package ru.mamba.test.mambatest.model.form;

import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

public class Switcher extends Field {

    private final static String F_BOOL_VALUE = "value";

    private boolean mValue;

    public Switcher(JSONObject json) throws JSONException {
        super(json);
        setValue(json.getBoolean(F_BOOL_VALUE));
    }

    public Boolean getValue() {
        return mValue;
    }

    public void setValue(boolean value) {
        mValue = value;
    }

    @Override
    public void actualize() {
        setValue(((Switch)getView()).isChecked());
    }
}
