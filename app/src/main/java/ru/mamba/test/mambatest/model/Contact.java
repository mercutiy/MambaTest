package ru.mamba.test.mambatest.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact extends Model {

    private final static String F_INT_ID = "contactId";

    private final static String F_INT_MESSAGES = "messages";

    private final static String F_OBJ_ANKETA = "anketa";

    private int mId;

    private int mMessages;

    private Anketa mAnketa;

    public Contact(JSONObject json) throws JSONException {
        super(json);
        setId(json.getInt(F_INT_ID));
        setMessages(json.getInt(F_INT_MESSAGES));
        setAnketa(new Anketa(json.getJSONObject(F_OBJ_ANKETA)));
    }

    public int getId() {
        return mId;
    }

    public int getMessages() {
        return mMessages;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setMessages(int messages) {
        mMessages = messages;
    }

    public Anketa getAnketa() {
        return mAnketa;
    }

    public void setAnketa(Anketa anketa) {
        mAnketa = anketa;
    }
}
