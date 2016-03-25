package ru.mamba.test.mambatest.fetcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ApiFetcher extends AsyncTask<Request, Void, JSONObject> {

    private static final String TAG = ApiFetcher.class.getCanonicalName();

    public static final String PREF_SESSION = "session";

    public static final String PREF_FIELD_SID = "SID";

    public static final String PREF_FIELD_SECRET = "SECRET";

    private final String mBaseUrl = "http://api.mobile-api.ru.www19.lan/v5.2.20.0/";

    protected Request mRequest;

    private Context mContext;

    public SharedPreferences mProperties;

    private FetchException mException = new FetchException();

    public ApiFetcher(Context context) {
        mContext = context;
        mProperties = mContext.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE);
    }

    @Override
    protected JSONObject doInBackground(Request... params) {
        Request request = null;

        if (params.length > 0) {
            request = params[0];
        } else if(mRequest != null) {
            request = mRequest;
        } else {
            Log.e(TAG, "Wrong argument");
            mException = new FetchException();
            return null;
        }

        try {
            return getResponse(request);
        } catch (FetchException e) {
            mException = e;
            return null;
        }

    }

    private JSONObject getResponse(Request request) throws FetchException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String response = null;

        String stringParams = "";
        if (request.getParams() != null) {
            stringParams = "?";
            for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                stringParams = stringParams + entry.getKey() + "=" + entry.getValue() + "&";
            }
        }


        try {
            URL url = new URL(mBaseUrl + request.getPath() + stringParams);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(request.getMethod());
            String sid = mProperties.getString(PREF_FIELD_SID, "");
            if (!sid.isEmpty()) {
                connection.addRequestProperty("Cookie", "mmbsid=" + sid);
            }
            if (request.getPost() != null) {
                OutputStream output = connection.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                writer.write(request.getPost().toString());
                writer.flush();
            }
            connection.connect();


            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new FetchException();
            }

            InputStream input = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (input == null) {
                throw new FetchException();
            }
            reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                throw new FetchException();
            }

            if (connection.getHeaderFields().containsKey("Set-Cookie")) {
                List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
                for (String cookie : cookies) {
                    Matcher matcher = Pattern.compile("mmbsid=([^;]+);").matcher(cookie);
                    if (matcher.find()) {
                        String newSid = matcher.group(1);
                        if (!newSid.equals(sid)) {
                            mProperties.edit().putString(PREF_FIELD_SID, newSid).apply();
                        }
                        break;
                    }
                }
            }

            response = buffer.toString();
            Log.v(TAG, "Response " + response);
        } catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new FetchException();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing json", e);
            throw new FetchException();
        }
    }


}
