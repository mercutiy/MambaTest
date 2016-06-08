package ru.mamba.test.mambatest.api.callback;

public interface Callback3<Model1, Model2, Model3> extends Callback {

    public void onResponse(Model1 model1, Model2 model2, Model3 model3);

}
