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

package com.huawei.hms.nearby.beacondemo.consumer.ui;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.beacon.BeaconPicker;
import com.huawei.hms.nearby.beacon.GetBeaconOption;
import com.huawei.hms.nearby.beacon.TriggerOption;
import com.huawei.hms.nearby.beacondemo.R;
import com.huawei.hms.nearby.beacondemo.common.BaseActivity;
import com.huawei.hms.nearby.beacondemo.consumer.BeaconBroadcastReceiver;
import com.huawei.hms.nearby.beacondemo.consumer.model.StoreAdapterInfo;
import com.huawei.hms.nearby.beacondemo.consumer.utils.JsonUtils;
import com.huawei.hms.nearby.message.BeaconInfo;

import java.util.ArrayList;

public class NewStoreActivity extends BaseActivity {
    private static final String TAG = "BeaconNewStoreActivity";

    private TextView addLogContent;

    private BroadcastReceiver scanResultReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Start StatusMonitoring.onReceive");
            String action = intent.getAction();
            switch (action) {
                case BeaconBroadcastReceiver.ACTION_SCAN_BEACON_RESULT: {
                    StringBuffer sb = new StringBuffer();
                    int isFound = intent.getIntExtra(BeaconBroadcastReceiver.KEY_SCAN_ONFOUND_FLAG, -1);
                    Log.i(TAG, "onReceive onFound, isFound:" + isFound);
                    if (isFound == 1) {
                        sb.append("Find the merchant!");
                    } else {
                        sb.append("Lose the merchant!");
                    }
                    ArrayList<BeaconInfo> beaconIds = intent.getParcelableArrayListExtra(
                        BeaconBroadcastReceiver.KEY_SCAN_BEACON_DATA);
                    if(beaconIds == null){
                        return;
                    }
                    for (BeaconInfo beacon : beaconIds) {
                        Log.i(TAG, "onReceive onFound, isFound:" + isFound + ", beaconId:" + beacon.getBeaconId());
                        sb.append("\nbeaconId:" + beacon.getBeaconId());

                        byte[] content = beacon.getContent();
                        if (content != null && content.length > 0) {
                            String messageContent = new String(beacon.getContent(), UTF_8);
                            sb.append("\n" + parseContent(messageContent));
                        }
                    }
                    appendToLogs(sb.toString());
                    break;
                }
                default: {
                    break;
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_new_beacon_scan);
        registerBeaconScan();
        setTitle(R.string.new_store);
        intView();
        registerScanResultReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        unRegisterBeaconScan();
        unregisterReceiver(scanResultReceiver);
    }

    private void intView() {
        addLogContent = findViewById(R.id.tv_log_info);
        addLogContent.setText("Start looking for nearby stores...");
    }

    private void registerScanResultReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BeaconBroadcastReceiver.ACTION_SCAN_BEACON_RESULT);
        registerReceiver(scanResultReceiver, intentFilter);
    }

    private void registerBeaconScan() {
        TriggerOption triggerOption = new TriggerOption.Builder().setTriggerMode(2)
            .setTriggerClassName(BeaconBroadcastReceiver.class.getName())
            .build();
        Intent intent = new Intent();
        intent.putExtra(GetBeaconOption.KEY_TRIGGER_OPTION, triggerOption);
        BeaconPicker beaconPicker = new BeaconPicker.Builder()
            .includeBeaconId("6bff00f723fdf7471402", BeaconPicker.BEACON_TYPE_IBEACON)
            .includeNamespaceType("namespace", "type", "6bff00f723fdf7471402", BeaconPicker.BEACON_TYPE_IBEACON)
            .build();
        GetBeaconOption getBeaconOption = new GetBeaconOption.Builder().picker(beaconPicker).build();
        Nearby.getBeaconEngine(this)
            .registerScanTask(intent, getBeaconOption)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.i(TAG, "registerScanTask success");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.i(TAG, "registerScanTask fail:" + e.getMessage());
                }
            });
    }

    private void unRegisterBeaconScan() {
        TriggerOption triggerOption = new TriggerOption.Builder().setTriggerMode(2)
            .setTriggerClassName(BeaconBroadcastReceiver.class.getName())
            .build();
        Intent intent = new Intent();
        intent.putExtra(GetBeaconOption.KEY_TRIGGER_OPTION, triggerOption);
        Nearby.getBeaconEngine(this).unRegisterScanTask(intent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "unRegisterScanTask success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "unRegisterScanTask fail:" + e.getMessage());
            }
        });
    }

    public void appendToLogs(CharSequence msg) {
        StringBuffer existLog = new StringBuffer();
        existLog.append(DateFormat.format("HH:mm:ss", System.currentTimeMillis()) + ": " + "\n");
        existLog.append(msg);
        existLog.append("\n");
        existLog.append("\n");
        existLog.append(addLogContent.getText().toString());
        addLogContent.setText(existLog.toString());
    }

    private String parseContent(String messageContent) {
        StoreAdapterInfo storeAdapterInfo = JsonUtils.json2Object(messageContent, StoreAdapterInfo.class);
        if (storeAdapterInfo == null) {
            return "error message";
        }
        String storeName = storeAdapterInfo.getStoreName();
        if (storeName == null) {
            return "no storeName";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("\nnotice:" + storeAdapterInfo.getNotice());
        sb.append("\name:" + storeAdapterInfo.getStoreName());
        sb.append("\ndesc:" + storeAdapterInfo.getStoreDesc());
        sb.append("\nimageurl:" + storeAdapterInfo.getStoreImageUrl());

        return sb.toString();
    }
}
