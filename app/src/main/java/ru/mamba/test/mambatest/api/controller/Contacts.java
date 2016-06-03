package ru.mamba.test.mambatest.api.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.model.Contact;

public class Contacts extends Controller<Contacts.Model> {


    private final static String METHOD = Request.GET;

    private final static String URI = "/folders/%d/contacts/";

    private final static String F_OBJ_FOLDER = "folder";

    private final static String F_LST_CONTACTS = "contacts";

    private final static String F_INT_COUNT = "count";

    public Contacts(int folderId, int offset, int limit) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        setRequest(new Request(getUri(folderId), METHOD, params));
    }

    @Override
    protected Model parseResponse(JSONObject json) throws JSONException {
        Model model = new Model();

        model.setContactCount(json.getJSONObject(F_OBJ_FOLDER).getInt(F_INT_COUNT));
        JSONArray jsonContacts = json.getJSONArray(F_LST_CONTACTS);

        List<Contact> contacts = new ArrayList<Contact>();
        for (int i = 0; i < jsonContacts.length(); i++) {
            contacts.add(new Contact(jsonContacts.getJSONObject(i)));
        }
        model.setContacts(contacts.toArray(new Contact[contacts.size()]));

        return model;
    }

    public class Model {

        int mContactCount;

        Contact[] mContacts;

        public int getContactCount() {
            return mContactCount;
        }

        public void setContactCount(int contactCount) {
            mContactCount = contactCount;
        }

        public Contact[] getContacts() {
            return mContacts;
        }

        public void setContacts(Contact[] contacts) {
            mContacts = contacts;
        }
    }

    private String getUri(int folderId) {
        return String.format(URI, folderId);
    }
}
