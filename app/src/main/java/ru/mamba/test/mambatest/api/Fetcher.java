package ru.mamba.test.mambatest.api;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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

import ru.mamba.test.mambatest.LoginActivity;
import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.api.callback.Callback;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.callback.Callback2;
import ru.mamba.test.mambatest.api.callback.Callback3;
import ru.mamba.test.mambatest.api.controller.Controller;

import ru.mamba.test.mambatest.api.exception.NotAuthException;
import ru.mamba.test.mambatest.fetcher.ApiException;
import ru.mamba.test.mambatest.fetcher.ConnectionException;
import ru.mamba.test.mambatest.fetcher.FetchException;
import ru.mamba.test.mambatest.fetcher.JsonException;
import ru.mamba.test.mambatest.fetcher.Session;


public class Fetcher extends AsyncTask<Request, Void, Controller[]> {

    private static final String TAG = Fetcher.class.getSimpleName();

    private Activity mActivity;

    private Session mSession;

    private Callback mCallback;

    private Controller[] mControllers;

    private ProgressDialog mDialog;

    private boolean mReauthorise = false;

    public Fetcher(Activity activity, Callback callback) {
        mActivity = activity;
        mSession = Session.getInstance(activity);
        mCallback = callback;
    }

    public void fetch(Controller... controllers) {
        mControllers = controllers;
        execute(buildRequest(mControllers));
    }

    private Request buildRequest(Controller[] controllers) {
        if (controllers.length == 1) {
            return controllers[0].getRequest();
        }

        JSONObject batch = new JSONObject();
        JSONArray container = new JSONArray();
        try {
            for (Controller controller: controllers) {
                container.put(controller.getRequest().getRequestForBatch());
            }
            batch.put("sysRequestsContainer", container);
        } catch (JSONException e) {
            Log.e(TAG, "json creating error", e);
        }

        return new Request("/", Request.POST, null, batch);
    }

    private void saveResponses(String json) throws FetchException {
        try {
            JSONObject jsonResponse = new JSONObject(json);
            if (mControllers.length > 1) {
                JSONArray container = jsonResponse.getJSONArray("sysResponsesContainer");
                for (int i = 0; i < container.length(); i++) {
                    mControllers[i].setResponse(new Response(container.getJSONObject(i)));
                }
            } else {
                mControllers[0].setResponse(new Response(jsonResponse));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing json", e);
            throw new JsonException();
        } catch (NotAuthException e) {
            mReauthorise = true;
        }
    }

    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage(mActivity.getResources().getString(R.string.loading));
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Controller[] doInBackground(Request... params) {
        try {
            saveResponses(sendRequest(params.length > 0 ? params[0] : null));
        } catch (FetchException e) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(Controller[] controllers) {
        mDialog.dismiss();
        if (mReauthorise) {
            getSession().setSid("");
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
            return;
        }
        if (mControllers.length == 1 && mCallback instanceof Callback1) {
            ((Callback1)mCallback).onResponse(mControllers[0].getModel());
        } else if (mControllers.length == 2 && mCallback instanceof Callback2) {
            ((Callback2)mCallback).onResponse(mControllers[0].getModel(), mControllers[1].getModel());
        } else if (mControllers.length == 3 && mCallback instanceof Callback3) {
            ((Callback3)mCallback).onResponse(mControllers[0].getModel(), mControllers[1].getModel(), mControllers[2].getModel());
        } else {

        }
    }

    protected String sendRequest(Request request) throws FetchException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String response = null;

        try {
            connection = (HttpURLConnection)request.getURL().openConnection();
            connection.setRequestMethod(request.getMethod());

            getSession().setCookie(connection);

            if (request.getPost() != null) {
                OutputStream output = connection.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                writer.write(request.getRawPost());
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

        return response;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Session getSession() {
        return mSession;
    }
}
