package com.jakubbosak.udemycourse.yora.infrastructure;

import android.app.Application;

import com.jakubbosak.udemycourse.yora.services.Module;
import com.jakubbosak.udemycourse.yora.services.BusService;


public class YoraApplication extends Application {
    private Auth auth;
    private BusService bus;

    public YoraApplication(){
        bus = new BusService();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        auth = new Auth(this);
        Module.register(this);
    }
    public Auth getAuth(){
        return auth;
    }

    public BusService getBus() { return  bus;}

}
