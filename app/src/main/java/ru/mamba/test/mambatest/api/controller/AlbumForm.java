package ru.mamba.test.mambatest.api.controller;

import ru.mamba.test.mambatest.api.Authorise;
import ru.mamba.test.mambatest.api.Request;

public class AlbumForm extends Form implements Authorise {

    private final static String METHOD = Request.GET;

    private final static String URI = "/albums/new/";

    public AlbumForm() {
        setRequest(new Request(URI, METHOD));
    }

}

