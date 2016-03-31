package ru.mamba.test.mambatest.fetcher;

import android.app.Activity;
import android.app.ProgressDialog;
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
import java.util.Map;

import ru.mamba.test.mambatest.R;

public abstract class ApiFetcher2 extends AsyncTask<Request, Void, Response> {

    protected static final String TAG = ApiFetcher2.class.getCanonicalName();

    private static final String sBaseUrl = "http://api.mobile-api.ru/v5.2.20.0/";

    private Activity mActivity;

    private Session mSession;

    private Request mRequest;

    public ProgressDialog mDialog;

    public ApiFetcher2(Activity activity) {
        mActivity = activity;
        mSession = new Session(activity);
    }


    protected Activity getActivity() {
        return mActivity;
    }

    protected Session getSession() {
        return mSession;
    }

    public Request getRequest() {
        return mRequest;
    }

    public void setRequest(Request request) {
        mRequest = request;
    }

    protected Request getRequest(Request request) throws FetchException {
        if (request != null) {
            mRequest = request;
        }
        if (mRequest != null) {
            return mRequest;
        }
        throw new FetchException();
    }

    protected Response getResponse(Request request) throws FetchException {
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
            URL url = new URL(sBaseUrl + request.getPath() + stringParams);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(request.getMethod());

            getSession().setCookie(connection);

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
                getSession().saveSid(connection.getHeaderFields().get("Set-Cookie"));
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
            return new Response(new JSONObject(response));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing json", e);
            throw new FetchException();
        }
    }

    private void uiErrorExecute(Response response) {
        // TODO Обработка ошибок
    }

    protected void onPreExecute() {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage(mActivity.getResources().getString(R.string.loading));
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(true);
        mDialog.show();
    }


    @Override
    protected Response doInBackground(Request... params) {
        try {
            return getResponse(getRequest(params.length > 0 ? params[0] : null));
        } catch (FetchException e) {
            return new Response(e);
        }

    }

    @Override
    protected void onPostExecute(Response response) {
        mDialog.dismiss();
        JSONObject json = response.getJson();
        if (json != null) {
            try {
                if (this instanceof Autharize && !json.getBoolean("isAuth")) {
                    onPostExecute(getSession().restoreSession(getRequest()));
                }
                uiExecute(response);
            } catch (JSONException e) {
                response.setError(new FetchException());
                uiErrorExecute(response);
            }
        } else  {
            uiErrorExecute(response);
        }
    }

    protected abstract void uiExecute(Response response) throws JSONException;
}
