package ru.mamba.test.mambatest.model.form;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Text extends Field {

    private final static String F_STR_VALUE = "value";

    private String mValue;

    public Text(JSONObject json) throws JSONException {
        super(json);
        setValue(json.getString(F_STR_VALUE));
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override
    public void actualize() {
        setValue(((TextView)getView()).getText().toString());
    }
}
