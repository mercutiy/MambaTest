package ru.mamba.test.mambatest.model.form;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.model.Model;

public class Field extends Model {

    private final static String F_STR_TYPE = "inputType";

    private final static String F_STR_FIELD = "field";

    private final static String F_STR_TITLE = "name";

    private final static String F_STR_ERROR = "error";

    private final static String F_STR_DESCRIPTION = "desc";

    private final static String TYPE_TEXT = "Text";

    private final static String TYPE_SWITCHER = "Switcher";

    private final static String TYPE_SINGLE_SELECT = "SingleSelect";

    private String mTitle;

    private String mField;

    private String mError;

    private String mDescription;

    private View mView;

    public Field(JSONObject json) throws JSONException {
        super(json);
        setTitle(json.getString(F_STR_TITLE));
        setField(json.getString(F_STR_FIELD));
        if (json.has(F_STR_ERROR)) {
            setError(json.getString(F_STR_ERROR));
        }
        if (json.has(F_STR_DESCRIPTION)) {
            setDescription(json.getString(F_STR_DESCRIPTION));
        }
    }

    public static Field getField(JSONObject json) throws JSONException {
        String type = json.getString(F_STR_TYPE);
        switch (type) {
            case TYPE_TEXT:
                return new Text(json);
            case TYPE_SINGLE_SELECT:
                return new SingleSelect(json);
            case TYPE_SWITCHER:
                return new Switcher(json);
            default:
                // todo придумать че-то поумнее
                return new Field(json);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getField() {
        return mField;
    }

    public void setField(String field) {
        mField = field;
    }

    public String getError() {
        return mError;
    }

    public void setError(String error) {
        mError = error;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Object getValue() {
        return "";
    }

    public void setView(View view) {
        mView = view;
    }

    public View getView() {
        return mView;
    }

    public void actualize() {

    }
}
