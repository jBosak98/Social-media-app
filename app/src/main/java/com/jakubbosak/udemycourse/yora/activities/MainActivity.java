package com.jakubbosak.udemycourse.yora.activities;

import android.os.Bundle;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.views.MainNavDrawer;


public class MainActivity extends BaseAunthenticatedActivity {
    @Override
    protected void onYoraCreate(Bundle savedState) {
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Inbox");
        setNavDrawer(new MainNavDrawer(this));

    }
}