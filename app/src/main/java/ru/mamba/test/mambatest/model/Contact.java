package ru.mamba.test.mambatest.model;

public class Contact {

    private int mId;

    private int mAnketaId;

    private String mName;

    private int mAge;

    private String mPhoto;

    private boolean mDeleted = false;

    private int mMessages;

    public Contact(int id, int anketaId, String name, int messages) {
        mId = id;
        mAnketaId = anketaId;
        mName = name;
        mMessages = messages;
    }

    public int getId() {
        return mId;
    }

    public int getAnketaId() {
        return mAnketaId;
    }

    public String getName() {
        return mName;
    }

    public int getAge() {
        return mAge;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public int getMessages() {
        return mMessages;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }
}
