package ru.mamba.test.mambatest.api;

import android.app.Activity;
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

import ru.mamba.test.mambatest.activity.Login;
import ru.mamba.test.mambatest.R;
import ru.mamba.test.mambatest.api.callback.Callback;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.callback.Callback2;
import ru.mamba.test.mambatest.api.callback.Callback3;
import ru.mamba.test.mambatest.api.controller.Controller;

import ru.mamba.test.mambatest.api.exception.NotAuthException;
import ru.mamba.test.mambatest.api.exception.ApiException;
import ru.mamba.test.mambatest.api.exception.TooManyControllersException;
import ru.mamba.test.mambatest.helper.ErrorHandler;


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
        if (controllers.length < 1 || controllers.length > 3) {
            ErrorHandler.getInstance().handle(
                getActivity(),
                new TooManyControllersException("Wrong requests count in batch query")
            );
            return;
        }

        mControllers = controllers;

        Request request;
        try {
            request = buildRequest(mControllers);
        } catch (JSONException e) {
            ErrorHandler.getInstance().handle(getActivity(), e, "Cant generate batch request json");
            return;
        }

        execute(request);
    }

    private Request buildRequest(Controller[] controllers) throws JSONException {
        if (controllers.length == 1) {
            return controllers[0].getRequest();
        }

        JSONArray container = new JSONArray();
        for (Controller controller: controllers) {
            container.put(controller.getRequest().getRequestForBatch());
        }
        JSONObject batch = new JSONObject();
        batch.put("sysRequestsContainer", container);

        return new Request("/", Request.POST, null, batch);
    }

    private void saveResponses(String json) throws JSONException {
        try {
            JSONObject response = new JSONObject(json);
            if (mControllers.length > 1) {
                JSONArray container = response.getJSONArray("sysResponsesContainer");
                for (int i = 0; i < container.length(); i++) {
                    mControllers[i].setResponse(container.getJSONObject(i));
                }
            } else {
                mControllers[0].setResponse(response);
            }
        } catch (NotAuthException e) {
            mReauthorise = true;
            return;
        }
    }

    private void saveResponses(Throwable exception) {
        saveResponses(exception, null);
    }

    private void saveResponses(Throwable exception, String errorMessage) {
        for (Controller controller : mControllers) {
            controller.setError(exception);
            controller.setErrorMessage(errorMessage);
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
        String output;
        try {
            output = sendRequest(params.length > 0 ? params[0] : null);
        } catch (IOException e) {
            saveResponses(e, "Fetching api IO error");
            return mControllers;
        } catch (ApiException e) {
            saveResponses(e);
            return mControllers;
        }

        try {
            saveResponses(output);
        } catch (JSONException e) {
            saveResponses(e, "Unpredictable json layout in api response");
        }

        return mControllers;
    }

    @Override
    protected void onPostExecute(Controller[] controllers) {
        mDialog.dismiss();

        for (Controller controller : controllers) {
            if (controller.getError() != null) {
                ErrorHandler.getInstance().handle(getActivity(), controller.getError(), controller.getErrorMessage());
                return;
            }
        }

        if (mReauthorise) {
            getSession().setSid("");
            Intent intent = new Intent(getActivity(), Login.class);
            getActivity().startActivity(intent);
            return;
        }

        if (mControllers.length == 1 && mCallback instanceof Callback1) {
            ((Callback1)mCallback).onResponse(mControllers[0].getModel());
        } else if (mControllers.length == 2 && mCallback instanceof Callback2) {
            ((Callback2)mCallback).onResponse(mControllers[0].getModel(), mControllers[1].getModel());
        } else if (mControllers.length == 3 && mCallback instanceof Callback3) {
            ((Callback3)mCallback).onResponse(mControllers[0].getModel(), mControllers[1].getModel(), mControllers[2].getModel());
        }
    }

    protected String sendRequest(Request request) throws ApiException, IOException {
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
                throw new ApiException("Http response status " + connection.getResponseCode());
            }

            InputStream input = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (input == null) {
                throw new ApiException("Connection error");
            }
            reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                throw new ApiException("Empty api answer");
            }

            if (connection.getHeaderFields().containsKey("Set-Cookie")) {
                getSession().saveSid(connection.getHeaderFields().get("Set-Cookie"));
            }

            response = buffer.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
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
