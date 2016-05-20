package ru.mamba.test.mambatest.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Profile extends Model {

    protected final static String F_INT_ID = "id";

    protected final static String F_INT_NAME = "name";

    protected final static String F_INT_AGE = "age";

    protected final static String F_OBJ_INTERESTS = "interests";

    protected final static String F_LST_INTERESTS = "items";

    protected final static String F_STR_INTEREST = "title";

    protected final static String F_OBJ_ABOUTME = "aboutmeBlock";

    protected final static String F_LST_ABOUTME = "fields";

    protected final static String F_STR_KEY = "key";

    protected final static String F_STR_VAL = "value";

    protected final static String ABOUTME_KEY = "value";

    private int mId;

    private String mName;

    private int mAge;

    private String mGreeting;

    private String[] mInterests;

    public Profile(JSONObject json) throws JSONException {
        super(json);
        setId(json.getInt(F_INT_ID));
        setName(json.getString(F_INT_NAME));
        setAge(json.getInt(F_INT_AGE));

        if (json.has(F_OBJ_INTERESTS)) {
            JSONArray jsonInterests = json.getJSONObject(F_OBJ_INTERESTS).getJSONArray(F_LST_INTERESTS);
            List<String> interests = new ArrayList<String>();
            for (int i = 0; i < jsonInterests.length(); i++) {
                interests.add(jsonInterests.getJSONObject(i).getString(F_STR_INTEREST));
            }
            setInterests(interests.toArray(new String[interests.size()]));
        }

        if (json.has(F_OBJ_ABOUTME) && !json.isNull(F_OBJ_ABOUTME)) {
            JSONObject aboutMeBlock = json.getJSONObject(F_OBJ_ABOUTME);
            JSONArray aboutMe = aboutMeBlock.getJSONArray(F_LST_ABOUTME);
            for (int i = 0; i < aboutMe.length(); i++) {
                JSONObject aboutmeItem = aboutMe.getJSONObject(i);
                if (aboutmeItem.getString(F_STR_KEY).equals(ABOUTME_KEY)) {
                    setGreeting(aboutmeItem.getString(F_STR_VAL));
                    break;
                }
            }
        }

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public String getGreeting() {
        return mGreeting;
    }

    public void setGreeting(String greeting) {
        mGreeting = greeting;
    }

    public String[] getInterests() {
        return mInterests;
    }

    public void setInterests(String[] interests) {
        mInterests = interests;
    }
}
