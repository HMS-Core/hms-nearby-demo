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

package com.huawei.hms.nearby.stores.softbeacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.huawei.hms.nearby.stores.BeaconApplication;
import com.huawei.hms.nearby.stores.ui.Constant;

import static android.content.Context.MODE_PRIVATE;

public class BleUtil {
    private static final String TAG = "BeaconBroadcaster";

    private static BluetoothLeAdvertiser bluetoothLeAdv;

    private static BluetoothAdapter bluetoothAdapter;

    private static String uuid;

    public static void startAdvertise(String deviceId) {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!bluetoothAdapter.isEnabled()) {
            return;
        }
        if (bluetoothLeAdv == null) {
            bluetoothLeAdv = bluetoothAdapter.getBluetoothLeAdvertiser();
        }

        uuid = deviceId;

        AdvertiseSettings settings = createAdvertiseSettings();
        AdvertiseData data = createAdvertiseData();
        //-59是 measuredPower,一般设备默认都是-59，这里固定了
        Log.e(TAG, "开始广播");
        bluetoothLeAdv.startAdvertising(settings, data, advCb);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void stopAdvertise() {
        bluetoothLeAdv.stopAdvertising(advCb);
        bluetoothLeAdv.stopAdvertisingSet(new AdvertisingSetCallback() {
            @Override
            public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                Log.e(TAG, "stop");
            }
        });
    }

    private static AdvertiseCallback advCb = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            Log.e(TAG, "开启广播成功");
        }

        public void onStartFailure(int errorCode) {
            Log.e(TAG, "onStartFailure errorCode=" + errorCode);
        }
    };

    private static AdvertiseSettings createAdvertiseSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);

        builder.setTimeout(0);
        builder.setTxPowerLevel(SafeSharedPreferences.getInstance(BeaconApplication.context, Constant.SP_FILE_NAME,
                MODE_PRIVATE).getInt(Constant.IBEACON_POWER, AdvertiseSettings.ADVERTISE_TX_POWER_HIGH));
        return builder.build();
    }

    private static AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        String beaconType = "0215"; //按照apple iBeacon协议
        String measuredPower = Conversion.formatStringLenth(2,
                Integer.toHexString(SafeSharedPreferences.getInstance(BeaconApplication.context, Constant.SP_FILE_NAME,
                        MODE_PRIVATE).getInt(Constant.IBEACON_POWER_RSSI, -56)), '0');
        String dataStr = beaconType + uuid + measuredPower;
        Log.d(TAG, "AdvertiseSettings dataStr : " + dataStr);

        byte[] data = Conversion.hexStringToBytes(dataStr);
        mDataBuilder.addManufacturerData(0x004C, data);//004c是厂商id，代表apple公司
        return mDataBuilder.build();
    }
}
