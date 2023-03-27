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

package com.huawei.hms.nearby.beacondemo.merchants.softbeacon;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.hms.nearby.beacondemo.merchants.utils.BleUtil;
import com.huawei.hms.nearby.beacondemo.merchants.utils.DeviceIdUtil;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private static final String TAG = "BeaconBroadcaster";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "bluetooth switch status changed");
        switch(intent.getAction()){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch(blueState){
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // 开始扫描
                        Log.e(TAG, "bluetooth switch is ON");
                        BleUtil.startAdvertise(DeviceIdUtil.getDeviceId());
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        break;
                }
                break;
        }

    }
}
