package ru.mamba.test.mambatest.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends Model {

    protected final static String F_INT_ID = "id";

    protected final static String F_INT_NAME = "name";

    protected final static String F_INT_AGE = "age";

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
