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

package com.huawei.hms.nearby.beacondemo.consumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import com.huawei.hms.nearby.message.BeaconInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * 接收扫描到beacon的广播
 *
 * @since 2023-02-23
 */
public class BeaconBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BeaconBroadcastReceiver";

    public static final String ACTION_SCAN_ONFOUND_RESULT = "com.huawei.hms.nearby.action.ONFOUND_BEACON";

    public static final String ACTION_SCAN_BEACON_RESULT = "com.huawei.hms.nearby.beacondemo.action.ONFOUND_BEACON";

    public static final String KEY_SCAN_ONFOUND_FLAG = "SCAN_ONFOUND_FLAG"; // 0为丢失1为发现

    public static final String KEY_SCAN_BEACON_DATA = "SCAN_BEACON"; // 发现或丢失的beacon信息

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e(TAG, "intent is null");
            return;
        }
        Log.i(TAG, "onReceive:" + intent);
        String action = intent.getAction();
        if (ACTION_SCAN_ONFOUND_RESULT.equals(action)) {
            int onFound = intent.getIntExtra(KEY_SCAN_ONFOUND_FLAG, -1);
            Log.i(TAG, "onReceive onFound, isFound:" + onFound);
            List<BeaconInfo> beaconIds = intent.getParcelableArrayListExtra(KEY_SCAN_BEACON_DATA);
            if (beaconIds == null) {
                Log.w(TAG, "beaconIds is null");
                return;
            }
            for (BeaconInfo beacon : beaconIds) {
                Log.i(TAG, "onReceive onFound, isFound:" + onFound + ", beaconId:" + beacon.getBeaconId());
            }
            Intent newIntent = new Intent();
            newIntent.putExtra(KEY_SCAN_ONFOUND_FLAG, onFound);
            newIntent.putParcelableArrayListExtra(KEY_SCAN_BEACON_DATA, (ArrayList<? extends Parcelable>) beaconIds);
            newIntent.setAction(ACTION_SCAN_BEACON_RESULT);
            context.sendBroadcast(newIntent);
        }
    }
}
