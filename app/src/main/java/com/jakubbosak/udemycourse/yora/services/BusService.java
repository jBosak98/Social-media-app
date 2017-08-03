package com.jakubbosak.udemycourse.yora.services;

import com.squareup.otto.Bus;

import java.util.ArrayList;



public class BusService extends Bus {
    private ArrayList registeredObjects = new ArrayList<>();

    @Override
    public void register(Object object){
        if(!registeredObjects.contains(object)){
            registeredObjects.add(object);
            super.register(object);
        }
    }
    @Override
    public void unregister(Object object){
        if(registeredObjects.contains(object)) {
            registeredObjects.remove(object);
            super.unregister(object);
        }
    }
}
