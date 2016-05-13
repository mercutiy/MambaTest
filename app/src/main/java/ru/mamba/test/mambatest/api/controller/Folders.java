package ru.mamba.test.mambatest.api.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mamba.test.mambatest.api.Request;
import ru.mamba.test.mambatest.model.Folder;

public class Folders extends Controller<Folders.Model> {

    protected final static String F_LST_FOLDERS = "folders";

    public Folders() {
        setRequest(new Request("/folders/"));
    }

    @Override
    protected Model parseResponse(JSONObject json) throws JSONException {
        Model response = new Model();

        JSONArray jsonFolders = json.getJSONArray(F_LST_FOLDERS);
        List<Folder> folders = new ArrayList<Folder>();
        for (int i = 0; i < jsonFolders.length(); i++) {
            folders.add(new Folder(jsonFolders.getJSONObject(i)));
        }
        response.setFolders(folders.toArray(new Folder[folders.size()]));

        return response;
    }

    public class Model {
        private Folder[] folders;

        private int contacts;

        public Folder[] getFolders() {
            return folders;
        }

        public void setFolders(Folder[] folders) {
            this.folders = folders;
        }

        public int getContacts() {
            return contacts;
        }

        public void setContacts(int contacts) {
            this.contacts = contacts;
        }
    }
}
