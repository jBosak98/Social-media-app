package com.jakubbosak.udemycourse.yora.dialogs;


import android.app.DialogFragment;
import android.os.Bundle;

import com.jakubbosak.udemycourse.yora.infrastructure.ActionScheduler;
import com.jakubbosak.udemycourse.yora.infrastructure.YoraApplication;
import com.squareup.otto.Bus;

public class BaseDialogFragment extends DialogFragment {
    protected YoraApplication application;
    protected Bus bus;
    protected ActionScheduler scheduler;

    @Override
    public void onCreate (Bundle savedState){
        super.onCreate(savedState);
        application = (YoraApplication) getActivity().getApplication();
        scheduler = new ActionScheduler(application);

        bus = application.getBus();
        bus.register(this);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        scheduler.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        scheduler.onResume();
    }
}
