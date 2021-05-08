/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.nearby.stores.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
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

import com.huawei.hms.nearby.stores.R;
import com.huawei.hms.nearby.stores.beaconbase.ServiceAccountSignInClient;
import com.huawei.hms.nearby.stores.beaconbase.restfulapi.BeaconRestfulClient;
import com.huawei.hms.nearby.stores.canteen.permission.PermissionHelper;
import com.huawei.hms.nearby.stores.canteen.permission.PermissionInterface;
import com.huawei.hms.nearby.stores.canteen.utils.BluetoothCheckUtil;
import com.huawei.hms.nearby.stores.canteen.utils.GpsCheckUtil;
import com.huawei.hms.nearby.stores.canteen.utils.NetCheckUtil;
import com.huawei.hms.nearby.stores.softbeacon.BroadcasterService;
import com.huawei.hms.nearby.stores.softbeacon.DeviceIdUtil;
import com.huawei.hms.nearby.stores.softbeacon.SafeSharedPreferences;

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
    private static String TAG = MainActivity.class.getSimpleName();

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
        initCanteen();
        initSoftBeacon();
        checkPhonePermission();

        initializeAccountKey();
        initDeviceId();
    }

    private void initDeviceId() {
        deviceId = DeviceIdUtil.getDeviceId();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void initCanteen() {
        requestPermissions(this, this);
        checkHardwareEnabled();
    }

    private void initView() {
        Button consumer = findViewById(R.id.btn_consumer);
        Button merchants = findViewById(R.id.btn_merchants);

        consumer.setOnClickListener(click -> {
            if (!checkCanteenPermission()) {
                Toast.makeText(this, "no permission!.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isHardwareEnabled) {
                Toast.makeText(this, "hardware fail!.", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, CanteenActivity.class));
        });

        merchants.setOnClickListener(click -> startActivity(new Intent(this, MerchantsActivity.class)));
    }

    private void initSoftBeacon() {
        BluetoothAdapter.getDefaultAdapter().enable();
        SafeSharedPreferences.getInstance(this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putBoolean(Constant.SWITCH_STATE_KEY, false);

        checkAndRequestPermissions();
    }

    private boolean requestPermissions(Activity activity, PermissionInterface permissionInterface) {
        mPermissionHelper = new PermissionHelper(activity, permissionInterface);
        mPermissionHelper.requestPermissions();
        return true;
    }

    @Override
    public int getPermissionsRequestCode() {
        return REQUEST_CODE;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
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
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION};
        List<String> mPermissionsDoNotGrant = new ArrayList<>();

        mPermissionsDoNotGrant.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionsDoNotGrant.add(permission);
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, permissions[0])
                    != PackageManager.PERMISSION_GRANTED) {
                mPermissionsDoNotGrant.add(permissions[0]);
            }
        }

        if (mPermissionsDoNotGrant.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    mPermissionsDoNotGrant.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    private void checkPhonePermission() {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 27);
        }
    }

    /**
     * show AlertDialog
     *
     * @param context context
     * @param title   title
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
        builder.setNegativeButton("Confirm", new CanteenActivity.MyOnClickListener());
        builder.show();
    }

    private boolean checkCanteenPermission() {
        int blueTooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int blueToothAd = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int accessWifi = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int changeWifi = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int accessCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if ((blueTooth == PackageManager.PERMISSION_GRANTED)
                && (blueToothAd == PackageManager.PERMISSION_GRANTED)
                && (accessWifi == PackageManager.PERMISSION_GRANTED)
                && (changeWifi == PackageManager.PERMISSION_GRANTED)
                && (internet == PackageManager.PERMISSION_GRANTED)
                && (accessCoarse == PackageManager.PERMISSION_GRANTED)
                && (accessFine == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        requestPermissions(this, this);
        return false;
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
            inputStream = getResources().getAssets().open("736430079244746839103515611private.json");
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
        loginServiceAccountResult(serviceAccountKey);
    }

    private void loginServiceAccountResult(String serviceAccountKey) {
        ServiceAccountSignInClient signInClient = ServiceAccountSignInClient.buildFromJsonData(serviceAccountKey);
        if (signInClient.signIn() != 0) {
            return;
        }
        BeaconRestfulClient.getInstance().setAccessToken(signInClient.getJwt());
        BeaconRestfulClient.getInstance().setAccessTokenTimeExpiredCallback(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(this::login);
        });
    }

}
