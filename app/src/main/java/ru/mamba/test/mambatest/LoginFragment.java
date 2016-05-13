package ru.mamba.test.mambatest;

import android.app.Activity;
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

import ru.mamba.test.mambatest.api.Fetcher;
import ru.mamba.test.mambatest.api.callback.Callback1;
import ru.mamba.test.mambatest.api.controller.Login;
import ru.mamba.test.mambatest.fetcher.ApiFetcher;
import ru.mamba.test.mambatest.fetcher.Request;
import ru.mamba.test.mambatest.fetcher.Response;
import ru.mamba.test.mambatest.fetcher.Session;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, Callback1<Login.Model> {

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
            Fetcher fetcher = new Fetcher(getActivity(), this);
            fetcher.fetch(new Login(mEditLogin.getText().toString(), mEditPassword.getText().toString()));
        }
    }

    @Override
    public void onResponse(Login.Model login) {
        if (login.isSuccess()) {
            Toast.makeText(getActivity(), R.string.notice_right_login, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), login.getError(), Toast.LENGTH_LONG).show();
            return;
        }

        Session session = Session.getInstance(getActivity());
        session.setSecret(login.getAuthSecret());
        session.setAnketaId(login.getProfile().getId());

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        getActivity().startActivity(intent);
    }
}
