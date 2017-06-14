package com.jakubbosak.udemycourse.yora.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.activities.LoginActivity;

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private Button loginButton;
    private Callbacks callbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle SavedState){
        View view = inflater.inflate(R.layout.fragment_login, root, false);

        loginButton = (Button) view.findViewById(R.id.fragment_login_loginButton);
        loginButton.setOnClickListener(this);

        return view;

    }


    @Override
    public void onClick(View view) {
        if(view == loginButton){
            application.getAuth().getUser().setLoggedIn(true);
            callbacks.onLoggedIn();
        }
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
