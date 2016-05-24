package ru.mamba.test.mambatest.model;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Profile extends Model {

    private final static String F_INT_ID = "id";

    private final static String F_INT_NAME = "name";

    private final static String F_INT_AGE = "age";

    private final static String F_OBJ_INTERESTS = "interests";

    private final static String F_LST_INTERESTS = "items";

    private final static String F_STR_INTEREST = "title";

    private final static String F_OBJ_ABOUTME = "aboutmeBlock";

    private final static String F_LST_ABOUTME = "fields";

    private final static String F_STR_KEY = "key";

    private final static String F_STR_VAL = "value";

    private final static String ABOUTME_KEY = "aboutme";

    private final static String F_STR_PHOTO = "squarePhotoUrl";

    private int mId;

    private String mName;

    private int mAge;

    private String mGreeting;

    private String[] mInterests;

    private String mPhotoSrc;

    private Bitmap mPhoto;

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

        setPhotoSrc(json.getString(F_STR_PHOTO));
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

    public Bitmap getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Bitmap photo) {
        mPhoto = photo;
    }

    public String getPhotoSrc() {
        return mPhotoSrc;
    }

    public void setPhotoSrc(String photoSrc) {
        mPhotoSrc = photoSrc;
    }
}
