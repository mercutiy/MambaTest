package ru.mamba.test.mambatest.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Folder extends Model {

    private final static String F_INT_ID = "id";

    private final static String F_INT_CONTACTS = "count";

    private int mId;

    private int contactCount;

    public Folder(JSONObject json) throws JSONException {
        super(json);
        setId(json.getInt(F_INT_ID));
        setContactCount(json.getInt(F_INT_CONTACTS));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }
}
