package com.jakubbosak.udemycourse.yora.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.services.Account;
import com.squareup.otto.Subscribe;

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private Button loginButton;
    private Callbacks callbacks;
    private View progressBar;
    private EditText usernameText;
    private EditText passwordText;
    private String defaultLoginButtonText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle SavedState){
        View view = inflater.inflate(R.layout.fragment_login, root, false);

        loginButton = (Button) view.findViewById(R.id.fragment_login_loginButton);
        loginButton.setOnClickListener(this);

        progressBar = view.findViewById(R.id.fragment_login_progress);
        usernameText = (EditText) view.findViewById(R.id.fragment_login_userName);
        passwordText = (EditText) view.findViewById(R.id.fragment_login_password);

        defaultLoginButtonText = loginButton.getText().toString();

        return view;

    }

    @Override
    public void onClick(View view) {
        if(view == loginButton){
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setText(" ");
            usernameText.setEnabled(false);
            passwordText.setEnabled(false);

            bus.post(new Account.LoginWithUsernameRequest(
                    usernameText.getText().toString(),
                    passwordText.getText().toString()));
        }
    }

    @Subscribe
    public void onLoginWithUsername(Account.LoginWithUsernameResponse response){
        response.showErrorToast(getActivity());

        if (response.didSucceed()) {
            callbacks.onLoggedIn();
            return;
        }
        usernameText.setError(response.getPropertyError("userName"));
        usernameText.setEnabled(true);

        passwordText.setError(response.getPropertyError("password"));
        passwordText.setEnabled(true);

        loginButton.setText(defaultLoginButtonText);

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }
    @Override
    public void onDetach(){
        super.onDetach();
        callbacks = null;
    }
    public interface Callbacks {
        void onLoggedIn();
    }
}
