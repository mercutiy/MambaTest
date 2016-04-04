package ru.mamba.test.mambatest.fetcher;

import android.animation.FloatEvaluator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import ru.mamba.test.mambatest.LoginActivity;
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
                throw new ConnectionException();
            }

            InputStream input = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (input == null) {
                throw new ConnectionException();
            }
            reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                throw new ApiException();
            }

            if (connection.getHeaderFields().containsKey("Set-Cookie")) {
                getSession().saveSid(connection.getHeaderFields().get("Set-Cookie"));
            }

            response = buffer.toString();
            Log.v(TAG, "Response " + response);
        } catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new ConnectionException();
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
            throw new JsonException();
        }
    }

    private void uiErrorExecute(Response response) {
        FetchException error = response.getError();
        if (error != null) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setIcon(R.drawable.ic_action_error);
            try {
                throw error;
            } catch (ConnectionException e) {
                ad.setTitle(R.string.error_connect);
                ad.setMessage(R.string.error_message_connect);
            } catch (JsonException e) {
                ad.setTitle(R.string.error_json);
                ad.setMessage(R.string.error_message_json);
            } catch (ApiException e) {
                ad.setTitle(R.string.error_api);
                ad.setMessage(R.string.error_message_api);
            } catch (FetchException e) {
                ad.setTitle(R.string.error_common);
                ad.setMessage(R.string.error_message_common);
            }
            ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad.show();
        }
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
                    getSession().restoreSession(getRequest());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    return;
                }
                uiExecute(response);
            } catch (JSONException e) {
                response.setError(new JsonException());
                uiErrorExecute(response);
            }
        } else  {
            uiErrorExecute(response);
        }
    }

    protected abstract void uiExecute(Response response) throws JSONException;
}
