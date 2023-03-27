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

package com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.restfulapi;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.nearby.beacondemo.common.BeaconApplication;
import com.huawei.hms.nearby.beacondemo.common.Constant;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.blebeacon.Beacon;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.blebeacon.BeaconScanCb;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.blebeacon.BeaconScaner;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.blebeacon.IBeacon;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.callback.BeaconDiscoverCallback;
import com.huawei.hms.nearby.beacondemo.merchants.softbeacon.Conversion;
import com.huawei.hms.nearby.beacondemo.merchants.softbeacon.SafeSharedPreferences;
import com.huawei.hms.nearby.beacondemo.merchants.utils.BeaconUtil;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.huawei.hms.nearby.beacondemo.common.MainActivity.deviceId;

/**
 * Beacon Discover
 *
 * @since 2019-11-16
 */
public class BeaconDiscover {
    private static final String TAG = "BeaconDiscover";

    private static volatile BeaconDiscover sInstance = null;

    private Handler expireTimeHandler;

    private BeaconScaner beaconScaner;

    private BeaconScanCbWrapper beaconScanCb;

    private Map<String, Beacon> proximityBeaconMap;

    private ArrayList<Beacon> beaconCanbeRegisterList;

    private ArrayList<Beacon> beaconRegisteredList;

    private BeaconDiscoverCallback callback;

    /**
     * Constructor
     */
    private BeaconDiscover() {
        beaconScaner = new BeaconScaner();
        proximityBeaconMap = new ConcurrentHashMap<>();
        beaconCanbeRegisterList = new ArrayList<>();
        beaconRegisteredList = new ArrayList<>();
    }

    /**
     * Get single instance of BeaconDiscover.
     *
     * @return The instance of BeaconDiscover.
     */
    public static BeaconDiscover getInstance() {
        if (sInstance == null) {
            synchronized (BeaconDiscover.class) {
                if (sInstance == null) {
                    sInstance = new BeaconDiscover();
                }
            }
        }
        return sInstance;
    }

    /**
     * Starts a delayed Runnable that will cause the BLE Advertising to timeout and stop after a
     * set amount of time.
     */
    private void setTimeout(long expireTime) {
        expireTimeHandler = new Handler();
        expireTimeHandler.postDelayed(new BeaconScanExpireRunnable(), expireTime);
    }

    private class BeaconScanExpireRunnable implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "beacon scan time expired, stop scan!");
            stopScanNearbyBeacon();
        }
    }

    /**
     * Scan nearby beacons
     *
     * @param time     Specify ble beacon scan time, in second; 0: no timeout
     * @param callback Callback when fond a nearby beacon callback Callback for fond a nearby
     *                 beacon(unregistered or registered by current user)
     */
    public void startScanNearbyBeacon(int time, BeaconDiscoverCallback callback) {
        if (beaconScanCb != null) {
            return;
        }
        this.callback = callback;
        proximityBeaconMap.clear();
        beaconCanbeRegisterList.clear();
        beaconRegisteredList.clear();
        initSoftBeacon();
        beaconScanCb = new BeaconScanCbWrapper();
        int ret = beaconScaner.startSacn(beaconScanCb);
        if (ret != 0) {
            beaconScanCb = null;
            return;
        }
        if (time > 0) {
            long milliSeconds = TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS);
            setTimeout(milliSeconds);
        }
    }

    private void initSoftBeacon() {
        String beaconType = "0215"; // 按照apple iBeacon协议
        String measuredPower = Conversion.formatStringLenth(2,
                Integer.toHexString(SafeSharedPreferences.getInstance(BeaconApplication.context, Constant.SP_FILE_NAME,
                        MODE_PRIVATE).getInt(Constant.IBEACON_POWER_RSSI, -56)), '0');
        String dataStr = beaconType + deviceId + measuredPower;
        Beacon beacon = new IBeacon.Builder()
                .setRssi(-59)
                .setManufacturerData(Conversion.hexStringToBytes(dataStr))
                .build();

        String beaconId = beacon.getHexId();
        Log.i(TAG,
                String.format(Locale.ENGLISH, "%s:%s, rssi:%d, txPower:%d, dis:%f",
                        BeaconUtil.beaconType2String(beacon.getBeaconType()), beaconId, beacon.getRssi(),
                        beacon.getTxPower(), BeaconUtil.calDistance(beacon.getRssi(), beacon.getTxPower())));
        BeaconDiscover.QueryBeaconStatusCb queryBeaconStatusCb = new BeaconDiscover.QueryBeaconStatusCb(beacon);
        BeaconRestfulClient.getInstance().queryBeaconRegisterStatus(beaconId, queryBeaconStatusCb);
    }

    /**
     * Stop scan nearby beacons
     */
    public void stopScanNearbyBeacon() {
        if (beaconScanCb == null) {
            return;
        }
        beaconScaner.stopScan();
        beaconScanCb = null;
        callback.onCompelte();
    }

    /**
     * Get nearby beacon that not registered on cloud, and can be used to register
     *
     * @return beacon list
     */
    public ArrayList<Beacon> getBeaconCanbeRegisterList() {
        return beaconCanbeRegisterList;
    }

    /**
     * Get nearby beacon that had been registered on cloud under current app by current user
     *
     * @return beacon list
     */
    public ArrayList<Beacon> getBeaconRegisteredList() {
        return beaconRegisteredList;
    }

    /**
     * BeaconScan Callback Wrapper
     *
     * @since 2019-11-14
     */
    private class BeaconScanCbWrapper extends BeaconScanCb {
        @Override
        public void onFaild(int result, String info) {
            Log.e("BeaconScanCbWrapper",
                    String.format(Locale.ENGLISH, "Scan failed, errcode:%d, info:%s", result, info));
        }

        @Override
        public void onFound(Beacon beacon) {
            String beaconId = beacon.getHexId();
            if (proximityBeaconMap.containsKey(beaconId)) {
                return;
            }
            proximityBeaconMap.put(beaconId, beacon);
            Log.i(TAG,
                    String.format(Locale.ENGLISH, "%s:%s, rssi:%d, txPower:%d, dis:%f",
                            BeaconUtil.beaconType2String(beacon.getBeaconType()), beaconId, beacon.getRssi(),
                            beacon.getTxPower(), BeaconUtil.calDistance(beacon.getRssi(), beacon.getTxPower())));
            QueryBeaconStatusCb queryBeaconStatusCb = new QueryBeaconStatusCb(beacon);
            long err = BeaconRestfulClient.getInstance().queryBeaconRegisterStatus(beaconId, queryBeaconStatusCb);
            if (err != 0) {
                beaconCanbeRegisterList.add(beacon);
            }
        }
    }

    /**
     * Cloud server response Callback for Query Beacon Register Status
     *
     * @since 2019-11-15
     */
    public class QueryBeaconStatusCb implements Callback<BeaconStatusRes> {
        private static final int BEACON_UNREGISTER = 0;

        private static final int BEACON_REGISTERED = 1;

        private Beacon beacon;

        QueryBeaconStatusCb(Beacon beacon) {
            this.beacon = beacon;
        }

        @Override
        public void onResponse(Call<BeaconStatusRes> call, Response<BeaconStatusRes> response) {
            if (!response.isSuccessful()) {
                proximityBeaconMap.remove(beacon.getHexId());
                Log.e(TAG,
                        String.format(Locale.ENGLISH, "Failed, code:%d, info:%s", response.code(), response.message()));
                return;
            }

            BeaconStatusRes res = response.body();
            int sts = res.getRegisterStatus();

            Log.e(TAG, "BeaconId:" + beacon.getHexId() + ", status:" + sts);
            if (sts == BEACON_REGISTERED) {
                beaconRegisteredList.add(beacon);
            } else if (sts == BEACON_UNREGISTER && !TextUtils.equals(beacon.getHexId(), deviceId.toLowerCase(Locale.ROOT))) {
                /* beacon not registered on cloud */
                beaconCanbeRegisterList.add(beacon);
            } else {
                /* beacon registered by others */
                Log.e(TAG, "Beacon was registered by others.");
            }
        }

        @Override
        public void onFailure(Call<BeaconStatusRes> call, Throwable t) {
            Log.d(TAG, "onFailure to check beacon: " + beacon.getHexId());
            proximityBeaconMap.remove(beacon.getHexId());
        }
    }
}
