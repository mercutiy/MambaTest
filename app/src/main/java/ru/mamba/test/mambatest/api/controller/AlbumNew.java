package ru.mamba.test.mambatest.api.controller;

import org.json.JSONObject;

import ru.mamba.test.mambatest.api.Authorise;
import ru.mamba.test.mambatest.api.Request;

public class AlbumNew extends Form implements Authorise {

    private final static String METHOD = Request.POST;

    private final static String URI = "/albums/";

    public AlbumNew(JSONObject form) {
        setRequest(new Request(URI, METHOD, null, form));
    }

}
