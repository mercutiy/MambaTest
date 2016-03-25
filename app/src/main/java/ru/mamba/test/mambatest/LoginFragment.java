package ru.mamba.test.mambatest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.FetchException;
import ru.mamba.test.mambatest.fetcher.Request;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = LoginFragment.class.getCanonicalName();

    Button mLoginButton;

    EditText mEditLogin;

    EditText mEditPassword;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginButton = (Button)view.findViewById(R.id.button_login);
        mEditLogin = (EditText)view.findViewById(R.id.edit_text_login);
        mEditPassword = (EditText)view.findViewById(R.id.edit_text_password);

        mLoginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_login) {
            JSONObject jsonRequest = new JSONObject();
            try {
                jsonRequest.put("login", mEditLogin.getText().toString());
                jsonRequest.put("password", mEditPassword.getText().toString());
            } catch (JSONException e) {
                Log.e(TAG, "JSON creating error", e);
                return;
            }
            Request request = new Request(
                "login/",
                Request.POST,
                null,
                jsonRequest
            );

            new LoginFetcher(getActivity()).execute(request);
        }
    }

    private class LoginFetcher extends ApiFetcher {

        public LoginFetcher(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null) {
                Toast.makeText(getActivity(), R.string.notice_wrong_login, Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getActivity(), R.string.notice_right_login, Toast.LENGTH_LONG).show();

            try {
                mProperties
                    .edit()
                    .putString(PREF_FIELD_SECRET, json.getString("authSecret"))
                    .apply();
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e);
            }

            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            getActivity().startActivity(intent);
        }
    }

}
