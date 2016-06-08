package ru.mamba.test.mambatest.api.callback;

public interface Callback2<Model1, Model2> extends Callback {

    public void onResponse(Model1 model1, Model2 model2);

}
