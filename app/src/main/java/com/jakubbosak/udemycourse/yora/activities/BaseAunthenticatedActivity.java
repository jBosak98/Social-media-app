package com.jakubbosak.udemycourse.yora.activities;

import android.content.Intent;
import android.os.Bundle;



public abstract class BaseAunthenticatedActivity extends BaseActivity {
    @Override
    protected final void onCreate(Bundle savedState){
        super.onCreate(savedState);

        if(!application.getAuth().getUser().isLoggedIn()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        onYoraCreate(savedState);
    }
    protected abstract void onYoraCreate(Bundle savedState);
}
