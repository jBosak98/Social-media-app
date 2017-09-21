package com.jakubbosak.udemycourse.yora.services;

import android.util.Log;

import com.jakubbosak.udemycourse.yora.infrastructure.YoraApplication;

public class Module{
    public static void register(YoraApplication application) {
        Log.e("MODULE", "IN MEMORY REGISTER METHOD CALLED");
        new InMemoryAccountService(application);
        new InMemoryContactsService(application);
        new InMemoryMessagesService(application);
    }
}