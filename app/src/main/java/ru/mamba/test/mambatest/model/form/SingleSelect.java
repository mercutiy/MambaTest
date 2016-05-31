package ru.mamba.test.mambatest.model.form;

import android.util.ArrayMap;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingleSelect extends Field {

    private static final String F_OBJ_VARIANTS = "variants";

    private static final String F_STR_KEY = "variants";

    private static final String F_STR_VALUE = "variants";

    private Item[] mVariants;

    private String mValue;

    public SingleSelect(JSONObject json) throws JSONException {
        super(json);
        JSONArray jsonVariants = json.getJSONArray(F_OBJ_VARIANTS);
        JSONObject jsonVariant;
        Item[] variants = new Item[jsonVariants.length()];
        for (int i = 0; i < jsonVariants.length(); i++) {
            jsonVariant = jsonVariants.getJSONObject(i);
            variants[i] = new Item(jsonVariant.getString(F_STR_KEY), jsonVariant.getString(F_STR_VALUE));
        }
        setVariants(variants);
    }

    public Item[] getVariants() {
        return mVariants;
    }

    public void setVariants(Item[] variants) {
        mVariants = variants;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override
    public void actualize() {
        setValue(((Item)((Spinner)getView()).getSelectedItem()).getKey());
    }

    public class Item {

        private String mKey;

        private String mValue;

        public Item(String key, String value) {
            mKey = key;
            mValue = value;
        }

        public String getKey() {
            return mKey;
        }

        public String getValue() {
            return mValue;
        }

        public String toString() {
            return getValue();
        }
    }
}

