package com.soumya.wwdablu.barqr;

import android.app.Application;

import io.realm.Realm;
import timber.log.Timber;

public class BarQRApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize Realm (we are using Realm instead of using SQLite)
        Realm.init(this);

        //Debug Tree for log
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + "[" + element.getLineNumber() + "]";
                }
            });

        //Release Tree for log
        } else {
            Timber.plant(new ReleaseTree());
        }
    }
}
