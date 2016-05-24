package ru.mamba.test.mambatest.model.form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mamba.test.mambatest.model.Model;

public class Block extends Model {

    private final static String F_STR_TITLE = "name";

    private final static String F_STR_ERROR = "error";

    private final static String F_LST_FIELDS = "fields";

    private String mTitle;

    private String mError;

    private Field[] mFields;

    public Block(JSONObject json) throws JSONException {
        super(json);
        setTitle(json.getString(F_STR_TITLE));
        if (json.has(F_STR_ERROR)) {
            setError(json.getString(F_STR_ERROR));
        }
        List<Field> fields = new ArrayList<Field>();
        JSONArray jsonFields = json.getJSONArray(F_LST_FIELDS);
        for (int i = 0; i < jsonFields.length(); i++) {
            fields.add(new Field(jsonFields.getJSONObject(i)));
        }
        setFields(fields.toArray(new Field[fields.size()]));
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getError() {
        return mError;
    }

    public void setError(String error) {
        mError = error;
    }

    public Field[] getFields() {
        return mFields;
    }

    public void setFields(Field[] fields) {
        mFields = fields;
    }
}
