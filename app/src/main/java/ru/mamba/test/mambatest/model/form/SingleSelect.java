package ru.mamba.test.mambatest.model.form;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingleSelect extends Field {

    private static final String F_OBJ_VARIANTS = "variants";

    private static final String F_STR_KEY = "variants";

    private static final String F_STR_VALUE = "variants";

    Map<String, String> mVariants = new HashMap<String, String>();

    public SingleSelect(JSONObject json) throws JSONException {
        super(json);
        JSONArray variants = json.getJSONArray(F_OBJ_VARIANTS);
        JSONObject variant;
        for (int i = 0; i < variants.length(); i++) {
            variant = variants.getJSONObject(i);
            addVariant(variant.getString(F_STR_KEY), variant.getString(F_STR_VALUE));
        }
    }

    public Map<String, String> getVariants() {
        return mVariants;
    }

    public void addVariant(String key, String value) {
        mVariants.put(key, value);
    }
}

