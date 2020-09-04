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

package com.huawei.hms.nearby.message.device;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.huawei.hms.nearby.NearbyApiContext;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.device.utils.BluetoothCheckUtil;
import com.huawei.hms.nearby.message.device.utils.Constants;
import com.huawei.hms.nearby.message.device.utils.LocationCheckUtil;
import com.huawei.hms.nearby.message.device.utils.NetCheckUtil;
import com.huawei.hms.nearby.message.device.utils.PermissionUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SwitchCompat mPublishSwitch;

    private SwitchCompat mSubscribeSwitch;

    private AppCompatSpinner mModeSpinner;

    private String mMode;

    private Message mGmsMessage;

    private com.huawei.hms.nearby.message.Message mHmsMessage;

    private MessageClient mMessageEngine;

    private MessageListener mMessageListener;

    private MessageHandler mMessageHandler;

    private ArrayAdapter<String> mNearbyDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mModeSpinner = findViewById(R.id.mode_spinner);
        List<String> modeData = NearbyUtil.getSupportMode(this);
        if (!modeData.isEmpty()) {
            mMode = modeData.get(0);
        }
        mModeSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                R.layout.simple_spinner_dropdown_item,
                modeData));
        mModeSpinner.refreshDrawableState();
        mModeSpinner.setOnItemSelectedListener(this);
        mSubscribeSwitch = findViewById(R.id.subscribe_switch);
        mPublishSwitch = findViewById(R.id.publish_switch);

        mGmsMessage = DeviceMessage.newNearbyGMSMessage(
                getUUID(getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE)));
        mHmsMessage = DeviceMessage.newNearbyHMSMessage(
                getUUID(getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE)));
        mMessageEngine = new MessageClient(this);
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                String content = new String(message.getContent(), Charset.forName("UTF-8"));
                if (!content.contains("mMessageBody")) {
                    return;
                }
                mNearbyDevicesArrayAdapter.add("GMS: " + content);
                Log.i(TAG, "GMS onFound message: " + content);
            }

            @Override
            public void onLost(final Message message) {
                String content = new String(message.getContent(), Charset.forName("UTF-8"));
                if (!content.contains("mMessageBody")) {
                    return;
                }
                mNearbyDevicesArrayAdapter.remove("GMS: " + content);
                Log.i(TAG, "GMS onLost message: " + content);
            }
        };

        mMessageHandler = new MessageHandler() {
            @Override
            public void onFound(com.huawei.hms.nearby.message.Message message) {
                String content = new String(message.getContent(), Charset.forName("UTF-8"));
                if (!content.contains("mMessageBody")) {
                    return;
                }
                mNearbyDevicesArrayAdapter.add("HMS: " + content);;
                Log.i(TAG, "HMS onFound message: " + content);
            }

            @Override
            public void onLost(com.huawei.hms.nearby.message.Message message) {
                String content = new String(message.getContent(), Charset.forName("UTF-8"));
                if (!content.contains("mMessageBody")) {
                    return;
                }
                mNearbyDevicesArrayAdapter.remove("HMS: " + content);
                Log.i(TAG, "HMS onLost message: " + content);
            }
        };

        mSubscribeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMessageEngine.subscribe(mMessageListener, mMessageHandler, mMode);
                } else {
                    mMessageEngine.unsubscribe(mMessageListener, mMessageHandler, mMode);
                    mNearbyDevicesArrayAdapter.clear();
                }
            }
        });

        mPublishSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMessageEngine.publish(mGmsMessage, mHmsMessage, mMode);
                } else {
                    mMessageEngine.unpublish(mGmsMessage, mHmsMessage, mMode);
                }
            }
        });

        final List<String> nearbyDevicesArrayList = new ArrayList<>();
        mNearbyDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                nearbyDevicesArrayList);
        final ListView listView = findViewById(R.id.nearby_devices_list_view);
        if (listView != null) {
            listView.setAdapter(mNearbyDevicesArrayAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; ++i) {
            if (grantResults[i] != 0) {
                showWarnDialog(Constants.LOCATION_ERROR);
            }
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "Is support BLE advertiser: " + NearbyUtil.isSupportBleAdv());
        Log.i(TAG, "Is support BLE Scanner: " + NearbyUtil.isSupportBleScan());
        super.onStart();
        if (!BluetoothCheckUtil.isBlueEnabled()) {
            showWarnDialog(Constants.BLUETOOTH_ERROR);
            return;
        }

        if (!LocationCheckUtil.isLocationEnabled(this)) {
            showWarnDialog(Constants.LOCATION_SWITCH_ERROR);
            return;
        }

        if (!NetCheckUtil.isNetworkAvailable(this)) {
            showWarnDialog(Constants.NETWORK_ERROR);
            return;
        }

        String[] deniedPermission = PermissionUtil.getDeniedPermissions(this, new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
        if (deniedPermission.length > 0) {
            PermissionUtil.requestPermissions(this, deniedPermission, 10);
        }
    }

    @Override
    protected void onStop() {
        mSubscribeSwitch.setChecked(false);
        mPublishSwitch.setChecked(false);
        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() != R.id.mode_spinner) {
            return;
        }
        if (!(view instanceof TextView)) {
            return;
        }

        String mode = ((TextView) view).getText().toString();
        if (mode.equals(mMode)) {
            return;
        }
        if (mSubscribeSwitch.isChecked()) {
            mMessageEngine.unsubscribe(mMessageListener, mMessageHandler, mMode);
            mSubscribeSwitch.setChecked(false);
            mNearbyDevicesArrayAdapter.clear();
        }
        if (mPublishSwitch. isChecked()) {
            mMessageEngine.unpublish(mGmsMessage, mHmsMessage, mMode);
            mPublishSwitch.setChecked(false);
        }
        mMode = mode;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private static String getUUID(SharedPreferences sharedPreferences) {
        String uuid = sharedPreferences.getString("message_uuid", null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("message_uuid", uuid).apply();
        }
        return uuid;
    }

    private void showWarnDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warn);
        builder.setIcon(R.mipmap.warn);
        builder.setMessage(content);
        builder.setNegativeButton(R.string.btn_confirm, new MyOnClickListener());
        builder.show();
    }

    static class MyOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}