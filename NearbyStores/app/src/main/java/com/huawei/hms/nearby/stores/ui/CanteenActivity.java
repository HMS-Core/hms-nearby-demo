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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.discovery.BleSignal;
import com.huawei.hms.nearby.discovery.Distance;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.MessagePicker;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.StatusCallback;
import com.huawei.hms.nearby.stores.R;
import com.huawei.hms.nearby.stores.canteen.CanteenAdapter;
import com.huawei.hms.nearby.stores.canteen.model.CanteenAdapterInfo;
import com.huawei.hms.nearby.stores.canteen.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CanteenActivity extends BaseActivity {
    private static final String TAG = "NearbyStoresTAG";
    private static final int THREAD_SLEEP_TIME = 500;
    private static final int MSG_INFO_EMPTY = 1000;
    private Context mContext;
    private TextView searchTipTv;
    private LinearLayout loadingLayout;
    private CanteenAdapter canteenAdapter;
    private MessageEngine messageEngine;
    private List<CanteenAdapterInfo> canteenAdapterInfoList;
    private Map<String, CanteenAdapterInfo> canteenAdapterInfoMap;
    private MessageHandler mMessageHandler;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private BroadcastReceiver stateChangeReceiver =
            new BroadcastReceiver() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "Start StatusMonitoring.onReceive");
                    String action = intent.getAction();
                    switch (action) {
                        case BluetoothDevice.ACTION_ACL_DISCONNECTED: {
                            MainActivity.showWarnDialog(getApplicationContext(), Constant.BLUETOOTH_WARN);
                            break;
                        }
                        case BluetoothAdapter.ACTION_STATE_CHANGED: {
                            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                            switch (blueState) {
                                case BluetoothAdapter.STATE_OFF: {
                                    MainActivity.showWarnDialog(getApplicationContext(), Constant.BLUETOOTH_WARN);
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                            break;
                        }
                        case ConnectivityManager.CONNECTIVITY_ACTION: {
                            operateConnectivityAction(context);
                            break;
                        }
                        case LocationManager.PROVIDERS_CHANGED_ACTION: {
                            Object object = getSystemService(Context.LOCATION_SERVICE);
                            if (!(object instanceof LocationManager)) {
                                MainActivity.showWarnDialog(getApplicationContext(), Constant.GPS_WARN);
                                return;
                            }
                            LocationManager locationManager = (LocationManager) object;
                            if (!locationManager.isLocationEnabled()) {
                                MainActivity.showWarnDialog(getApplicationContext(), Constant.GPS_WARN);
                            }
                            break;
                        }
                        case BluetoothDevice.ACTION_ACL_CONNECTED:
                        default: {
                            break;
                        }
                    }
                }
            };


    @SuppressLint("HandlerLeak")
    private Handler mChangeUiHander = new Handler() {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            if (msg.what == MSG_INFO_EMPTY) {
                if (canteenAdapterInfoList.isEmpty()) {
                    loadingLayout.setVisibility(View.GONE);
                    searchTipTv.setText(R.string.search_tip);
                }
                canteenAdapter.setDatas(canteenAdapterInfoList);
            }

        }
    };

    private void operateConnectivityAction(Context context) {
        Object object = context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(object instanceof ConnectivityManager)) {
            MainActivity.showWarnDialog(this, Constant.NETWORK_WARN);
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) object;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            MainActivity.showWarnDialog(this, Constant.NETWORK_WARN);
            return;
        }
        int networkType = activeNetworkInfo.getType();
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_WIFI: {
                break;
            }
            default: {
                MainActivity.showWarnDialog(this, Constant.NETWORK_WARN);
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_canteen);
        setTitle("Consumer");
        intView();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        Future future = executorService.submit(() -> {
            try {
                Thread.sleep(THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                Log.i(TAG, "Thread sleep error", e);
            }
            startScanning();
        });
        try {
            future.get();
        } catch (ExecutionException e) {
            Log.i(TAG, "Thread executorService error", e);
        } catch (InterruptedException e) {
            Log.i(TAG, "Thread executorService error", e);
        }
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
        if (messageEngine != null && mMessageHandler != null) {
            Log.i(TAG, "unget");
            messageEngine.unget(mMessageHandler);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void intView() {
        canteenAdapterInfoMap = new HashMap<>();
        canteenAdapterInfoList = new ArrayList<>();
        mContext = this;
        messageEngine = Nearby.getMessageEngine(this);
        messageEngine.registerStatusCallback(
                new MyStatusCallback());
        ListView listView = findViewById(R.id.lv_canteen);
        canteenAdapter = new CanteenAdapter(mContext, canteenAdapterInfoList);
        listView.setAdapter(canteenAdapter);
        searchTipTv = findViewById(R.id.tv_search_canteen_tip);
        searchTipTv.setText(R.string.search_tip);
        loadingLayout = findViewById(R.id.ll_loading);
    }

    private void init() {
        registerStatusReceiver();
    }

    private void registerStatusReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        mContext.registerReceiver(stateChangeReceiver, intentFilter);
    }

    private void startScanning() {
        Log.i(TAG, "startScanning");
        mMessageHandler =
                new MessageHandler() {
                    @Override
                    public void onFound(Message message) {
                        super.onFound(message);
                        doOnFound(message);
                    }

                    @Override
                    public void onLost(Message message) {
                        super.onLost(message);
                        doOnLost(message);
                    }

                    @Override
                    public void onDistanceChanged(Message message, Distance distance) {
                        super.onDistanceChanged(message, distance);
                    }

                    @Override
                    public void onBleSignalChanged(Message message, BleSignal bleSignal) {
                        super.onBleSignalChanged(message, bleSignal);
                    }
                };
        MessagePicker msgPicker = new MessagePicker.Builder().includeAllTypes().build();
        Policy policy = Policy.BLE_ONLY;
        GetOption getOption = new GetOption.Builder().setPicker(msgPicker).setPolicy(policy).build();
        Task<Void> task = messageEngine.get(mMessageHandler, getOption);
        task.addOnFailureListener(
                e -> {
                    Log.e(TAG, "Login failed:", e);
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        int errorStatusCode = apiException.getStatusCode();
                        if (errorStatusCode == StatusCode.STATUS_MESSAGE_AUTH_FAILED) {
                            Toast.makeText(mContext, R.string.configuration_error, Toast.LENGTH_SHORT).show();
                        } else if (errorStatusCode == StatusCode.STATUS_MESSAGE_APP_UNREGISTERED) {
                            Toast.makeText(mContext, R.string.permission_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, R.string.start_get_beacon_message_failed, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.start_get_beacon_message_failed, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void doOnLost(Message message) {
        if (message == null) {
            return;
        }
        String type = message.getType();
        if (type == null) {
            return;
        }
        String messageContent = new String(message.getContent(), UTF_8);
        Log.d(TAG, "onLost:" + messageContent + " type:" + type);

        if (type.equalsIgnoreCase(Constant.TYPE_ATTACHMENT)) {
            operateOnLost(messageContent);
        }
    }

    private void operateOnLost(String messageContent) {
        CanteenAdapterInfo messageCanteen = JsonUtils.json2Object(messageContent, CanteenAdapterInfo.class);
        if (messageCanteen == null) {
            return;
        }
        String canteenName = messageCanteen.getCanteenName();
        if (canteenName == null) {
            return;
        }
        Log.d(TAG, "canteenName:" + canteenName);
        if (!canteenAdapterInfoMap.containsKey(canteenName)) {
            return;
        }

        canteenAdapterInfoMap.remove(canteenName);
        Iterator<CanteenAdapterInfo> it = canteenAdapterInfoList.iterator();
        while (it.hasNext()) {
            CanteenAdapterInfo canteenAdapterInfo = it.next();
            if (canteenName.equals(canteenAdapterInfo.getCanteenName())) {
                canteenAdapterInfoList.remove(canteenAdapterInfo);
                if (canteenAdapterInfoList.isEmpty()) {
                    android.os.Message message = new android.os.Message();
                    message.what = MSG_INFO_EMPTY;
                    mChangeUiHander.sendMessage(message);
                }
                break;
            }
        }
    }

    private void doOnFound(Message message) {
        if (message == null) {
            return;
        }
        String type = message.getType();
        if (type == null) {
            return;
        }
        String messageContent = new String(message.getContent(), UTF_8);
        Log.d(TAG, "New Message:" + messageContent + " type:" + type);

        if (type.equalsIgnoreCase(Constant.TYPE_ATTACHMENT)) {
            operateOnFound(messageContent);
        } else {
            Log.e(TAG, "Unknown Message:" + messageContent + " type:" + type);
        }

    }

    private void operateOnFound(String messageContent) {
        CanteenAdapterInfo canteenAdapterInfo = JsonUtils.json2Object(messageContent, CanteenAdapterInfo.class);
        if (canteenAdapterInfo == null) {
            return;
        }
        String canteenName = canteenAdapterInfo.getCanteenName();
        if (canteenName == null) {
            return;
        }
        Log.d(TAG, "canteenName:" + canteenName);

        if (canteenAdapterInfoMap.containsKey(canteenName)) {
            Log.d(TAG, "canteenName already exists");
            return;
        }

        String notice = canteenAdapterInfo.getNotice();
        String imageUrl = canteenAdapterInfo.getCanteenImageUrl();
        int requestCode = getRequestCode(canteenName);
        canteenAdapterInfo.setNotice(notice);
        canteenAdapterInfo.setCanteenImageUrl(imageUrl);
        canteenAdapterInfo.setShowNotice(true);
        canteenAdapterInfo.setRequestCode(requestCode);

        canteenAdapterInfoMap.put(canteenName, canteenAdapterInfo);
        canteenAdapterInfoList.add(canteenAdapterInfo);

        runOnUiThread(
                () -> {
                    searchTipTv.setText(R.string.found_tip);
                    loadingLayout.setVisibility(View.GONE);
                    canteenAdapter.setDatas(canteenAdapterInfoList);
                });
    }

    private int getRequestCode(String canteenName) {
        int requestCode = 0;
        for (int i = 0; i < canteenName.length(); i++) {
            requestCode += canteenName.charAt(i) * i;
        }
        return requestCode;
    }

    static class MyStatusCallback extends StatusCallback {
        @Override
        public void onPermissionChanged(boolean isPermissionGranted) {
            super.onPermissionChanged(isPermissionGranted);
            Log.d(TAG, "onPermissionChanged:" + isPermissionGranted);
        }
    }

    static class MyOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
