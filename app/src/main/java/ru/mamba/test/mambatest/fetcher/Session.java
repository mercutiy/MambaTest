package ru.mamba.test.mambatest.fetcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Session {

    public static final String PREF_SESSION = "session";

    public static final String PREF_FIELD_SID = "SID";

    public static final String PREF_FIELD_SECRET = "SECRET";

    public static final String PREF_FIELD_ANKETA = "ANKETA";

    public static final String PREF_FIELD_FOLDER = "FOLDER";

    private SharedPreferences mPreferences;

    private String mSid = null;

    private int mAnketaId = -1;

    public Session(Context context) {
        mPreferences = context.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE);
    }

    public String getSid() {
        if (mSid == null) {
            mSid = mPreferences.getString(PREF_FIELD_SID, "");
        }

        return mSid;
    }

    private void setSid(String sid) {
        if (!sid.equals(getSid())) {
            mPreferences.edit().putString(PREF_FIELD_SID, sid).apply();
            mSid = sid;
        }
    }

    public void setCookie(HttpURLConnection connection) {
        if (!getSid().isEmpty()) {
            connection.addRequestProperty("Cookie", "mmbsid=" + getSid());
        }
    }

    public void saveSid(List<String> cookies) {
        for (String cookie : cookies) {
            Matcher matcher = Pattern.compile("mmbsid=([^;]+);").matcher(cookie);
            if (matcher.find()) {
                setSid(matcher.group(1));
                break;
            }
        }
    }

    public void setSecret(String secret) {
        mPreferences.edit().putString(PREF_FIELD_SECRET, secret).apply();
    }

    public Response restoreSession(Request request) {
        // TODO Перелогин по secret
        setSid("");
        return new Response(new FetchException());
    }

    public int getAnketaId() {
        if (mAnketaId == -1) {
            mAnketaId = mPreferences.getInt(PREF_FIELD_ANKETA, 0);
        }

        return mAnketaId;
    }

    public void setAnketaId(int anketaId) {
        if (anketaId != getAnketaId()) {
            mPreferences.edit().putInt(PREF_FIELD_ANKETA, anketaId).apply();
            mAnketaId = anketaId;
        }
    }

    public void setFolderId(int folderId) {
        mPreferences.edit().putInt(PREF_FIELD_FOLDER, folderId).apply();
    }

    public int getFolderId() {
        return mPreferences.getInt(PREF_FIELD_FOLDER, 0);
    }

}
