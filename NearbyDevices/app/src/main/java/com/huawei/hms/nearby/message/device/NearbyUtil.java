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

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NearbyUtil
 *
 * @since 2020-02-21
 */
public class NearbyUtil {
    public static final String GMS_ONLY = "GMS";
    public static final String HMS_ONLY = "HMS";
    public static final String HMS_GMS = "HMS+GMS";
    public static final String GMS_HMS = "GMS+HMS";

    public static List<String> getSupportMode(Context context) {
        List<String> list = new ArrayList<>();
        boolean isSupportGMS = isGooglePlayServicesAvailable(context);
        boolean isSupportHMS = isHuaweiMobileServicesAvailable(context);
        if (isSupportGMS) {
            list.add(GMS_ONLY);
        }
        if (isSupportHMS) {
            list.add(HMS_ONLY);
        }

        if (isSupportGMS && isSupportHMS) {
            list.add(HMS_GMS);
            list.add(GMS_HMS);
        }

        return list;
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        Log.i("NearbyUtil", "GMS: " + com.google.android.gms.common.GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context));
        return !Arrays.asList(com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED,
                com.google.android.gms.common.ConnectionResult.SERVICE_MISSING,
                com.google.android.gms.common.ConnectionResult.SERVICE_INVALID)
                .contains(com.google.android.gms.common.GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(context));
    }

    public static boolean isHuaweiMobileServicesAvailable(Context context) {
        Log.i("NearbyUtil", "HMS: " + HuaweiApiAvailability.getInstance()
                .isHuaweiMobileServicesAvailable(context));
        return !Arrays.asList(ConnectionResult.SERVICE_DISABLED, ConnectionResult.SERVICE_MISSING,
                ConnectionResult.SERVICE_INVALID).contains(
                HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context));
    }

    public static boolean isSupportBleAdv() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }

        if (bluetoothAdapter.getBluetoothLeAdvertiser() == null) {
            return false;
        }

        return true;
    }

    public static boolean isSupportBleScan() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }

        if (bluetoothAdapter.getBluetoothLeScanner() == null) {
            return false;
        }

        return true;
    }
}
