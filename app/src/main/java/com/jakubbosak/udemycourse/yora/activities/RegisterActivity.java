package com.jakubbosak.udemycourse.yora.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jakubbosak.udemycourse.yora.R;



public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText usernameText;
    private EditText emailText;
    private EditText passwordText;
    private Button registerButton;
    private View progressBar;


    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);

        setContentView(R.layout.activity_register);
        findView();

        registerButton.setOnClickListener(this);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void findView() {
        usernameText = (EditText) findViewById(R.id.activity_register_userName);
        emailText = (EditText) findViewById(R.id.activity_register_email);
        passwordText = (EditText) findViewById(R.id.activity_register_password);
        registerButton = (Button) findViewById(R.id.activity_register_registerButton);
        progressBar = findViewById(R.id.activity_register_progressBar);
    }

    @Override
    public void onClick(View view) {
        if(view == registerButton){
            application.getAuth().getUser().setLoggedIn(true);

            setResult(RESULT_OK);
            finish();
        }

    }
}
