package com.example.administrator.myapplication.app;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

public class PicApplication extends Application {

    private static PicApplication myApplication;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    public static Context getAppContext() {
        return myApplication.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }
}
