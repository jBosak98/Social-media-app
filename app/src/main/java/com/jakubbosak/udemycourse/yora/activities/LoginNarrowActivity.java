package com.jakubbosak.udemycourse.yora.activities;

import android.os.Bundle;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.fragments.LoginFragment;

/**
 * Created by root on 6/12/17.
 */

public class LoginNarrowActivity extends BaseActivity implements LoginFragment.Callbacks {
    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);
        setContentView(R.layout.activity_logic_narrow);
    }

    @Override
    public void onLoggedIn() {
        setResult(RESULT_OK);
        finish();
    }
}
