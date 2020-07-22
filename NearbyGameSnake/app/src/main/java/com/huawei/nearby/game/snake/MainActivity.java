package com.huawei.nearby.game.snake;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.Manifest;
import android.os.Bundle;

public class MainActivity extends AndroidApplication {
    private static final String[] REQUIRED_PERMISSIONS = new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,};

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private App _app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        _app = new App(this);
        initialize(_app, config);
    }
}
