/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.hms.nearby.beacondemo.common;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.hms.nearby.beacondemo.R;
import com.huawei.hms.nearby.beacondemo.consumer.permission.PermissionHelper;
import com.huawei.hms.nearby.beacondemo.consumer.permission.PermissionInterface;
import com.huawei.hms.nearby.beacondemo.consumer.ui.NewStoreActivity;
import com.huawei.hms.nearby.beacondemo.consumer.ui.StoreActivity;
import com.huawei.hms.nearby.beacondemo.consumer.utils.BluetoothCheckUtil;
import com.huawei.hms.nearby.beacondemo.consumer.utils.GpsCheckUtil;
import com.huawei.hms.nearby.beacondemo.consumer.utils.NetCheckUtil;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.restfulapi.BeaconRestfulClient;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.ui.MerchantsActivity;
import com.huawei.hms.nearby.beacondemo.merchants.softbeacon.BroadcasterService;
import com.huawei.hms.nearby.beacondemo.merchants.softbeacon.SafeSharedPreferences;
import com.huawei.hms.nearby.beacondemo.merchants.utils.DeviceIdUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 *
 * @since 2019-11-14
 */
public class MainActivity extends AppCompatActivity implements PermissionInterface {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE = 8488;

    private static final int PERMISSION_REQUEST_CODE = 940;

    private PermissionHelper mPermissionHelper;

    private boolean isHardwareEnabled;

    public static String deviceId;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initStore();
        initSoftBeacon();
        checkPhonePermission();

        initializeAccountKey();
        initDeviceId();
    }

    private void initDeviceId() {
        deviceId = DeviceIdUtil.getDeviceId();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void initStore() {
        requestPermissions(this);
        checkHardwareEnabled();
    }

    private void initView() {
        Button newstore = findViewById(R.id.btn_new_store);
        Button store = findViewById(R.id.btn_store);
        Button merchants = findViewById(R.id.btn_merchants);

        newstore.setOnClickListener(click -> {
            if (!checkStorePermission()) {
                Toast.makeText(this, "no permission!.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isHardwareEnabled) {
                Toast.makeText(this, "hardware fail!.", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, NewStoreActivity.class));
        });

        store.setOnClickListener(click -> {
            if (!checkStorePermission()) {
                Toast.makeText(this, "no permission!.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isHardwareEnabled) {
                Toast.makeText(this, "hardware fail!.", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, StoreActivity.class));
        });

        merchants.setOnClickListener(click -> startActivity(new Intent(this, MerchantsActivity.class)));
    }

    private void initSoftBeacon() {
        // 软 Beacon 业务，暂时注释 BluetoothAdapter.getDefaultAdapter().enable();
        SafeSharedPreferences.getInstance(this, Constant.SP_FILE_NAME, MODE_PRIVATE)
            .putBoolean(Constant.SWITCH_STATE_KEY, false);

        checkAndRequestPermissions();
    }

    private void requestPermissions(PermissionInterface permissionInterface) {
        mPermissionHelper = new PermissionHelper(this, permissionInterface);
        mPermissionHelper.requestPermissions();
    }

    @Override
    public int getPermissionsRequestCode() {
        return REQUEST_CODE;
    }

    @Override
    public String[] getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[] {
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else {
            return new String[] {
                Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            };
        }

    }

    @Override
    public void requestPermissionsSuccess() {
        Log.i(TAG, "requestPermissionsSuccess");

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void checkHardwareEnabled() {
        if (!NetCheckUtil.isNetworkAvailable(this)) {
            showWarnDialog(this, Constant.NETWORK_ERROR);
            return;
        }
        if (!BluetoothCheckUtil.isBlueEnabled()) {
            showWarnDialog(this, Constant.BLUETOOTH_ERROR);
            return;
        }
        if (!GpsCheckUtil.isGpsEnabled(this)) {
            showWarnDialog(this, Constant.GPS_ERROR);
        }
        isHardwareEnabled = true;
    }

    @Override
    public void requestPermissionsFail() {
        finish();
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION
        };
        List<String> mPermissionsDoNotGrant = new ArrayList<>();

        mPermissionsDoNotGrant.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionsDoNotGrant.add(permission);
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionsDoNotGrant.add(permissions[0]);
            }
        }

        if (mPermissionsDoNotGrant.size() > 0) {
            ActivityCompat.requestPermissions(this, mPermissionsDoNotGrant.toArray(new String[0]),
                PERMISSION_REQUEST_CODE);
        }
    }

    private void checkPhonePermission() {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, 27);
        }
    }

    /**
     * show AlertDialog
     *
     * @param context context
     * @param title title
     * @param content content
     */
    public static void showAlertDialog(Context context, String title, String content) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setPositiveButton("Confirm", null);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static void showWarnDialog(Context context, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.warn);
        builder.setIcon(R.mipmap.warn);
        builder.setMessage(content);
        builder.setNegativeButton("Confirm", new StoreActivity.MyOnClickListener());
        builder.show();
    }

    private boolean checkStorePermission() {
        String[] permissionList = getPermissions();
        if (permissionList == null || permissionList.length <= 0) {
            return true;
        }

        for (String permission : permissionList) {
            int status = ContextCompat.checkSelfPermission(this, permission);
            if (status != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(this);
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, BroadcasterService.class));
        SafeSharedPreferences.getInstance(this, Constant.SP_FILE_NAME, MODE_PRIVATE)
            .putBoolean(Constant.SWITCH_STATE_KEY, false);
    }

    private void initializeAccountKey() {
        login();
    }

    private void login() {
        /*
         * Read service account file, and call the API from ServiceAccountSignInClient class to login and get the JWT.
         * Service Account file（***private.json）contains private key for logging into Nearby service.
         * Developer should provide the measure to protect the file from being tampered with.
         */
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = getResources().getAssets().open("99536292102645035107132009private.json");
            reader = new BufferedReader(new InputStreamReader((inputStream), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d(TAG, "login fail");
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(TAG, "login fail");
                }
            }
        }
        String serviceAccountKey = stringBuilder.toString();
        if (serviceAccountKey.length() == 0) {
            return;
        }
        Log.d(TAG, "Service account:" + serviceAccountKey);
        loginServiceAccountResult(serviceAccountKey);
    }

    private void loginServiceAccountResult(String serviceAccountKey) {
        ServiceAccountSignInClient signInClient = ServiceAccountSignInClient.buildFromJsonData(serviceAccountKey);
        if (signInClient.signIn() != 0) {
            Log.e(TAG, "Service account sign in error!");
            return;
        }

        Log.w(TAG, "Service account sign in success.............");
        BeaconRestfulClient.getInstance().setAccessToken(signInClient.getJwt());
        BeaconRestfulClient.getInstance().setAccessTokenTimeExpiredCallback(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(this::login);
        });
    }

}
