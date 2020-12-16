package com.wmq.hms.nearby.beaconmanager;

import android.app.Application;
import android.content.Context;

public class BeaconApplication extends Application {

    public static Context context = null ;

    @Override
    public void onCreate() {
        super.onCreate();
        if(null == context){
            context = getApplicationContext();
        }
    }

}
