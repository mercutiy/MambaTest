package ru.mamba.test.mambatest.api.response;

import ru.mamba.test.mambatest.model.Form;

public class FormBuilder {

    private String mMessage;

    private Form mForm;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Form getForm() {
        return mForm;
    }

    public void setForm(Form form) {
        mForm = form;
    }
}
