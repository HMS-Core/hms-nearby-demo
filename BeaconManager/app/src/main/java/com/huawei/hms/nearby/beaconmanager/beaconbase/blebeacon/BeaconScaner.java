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

package com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;

import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconBaseLog;

import java.util.ArrayList;
import java.util.List;

/**
 * ble beacon scan operations
 *
 * @author c00439704
 * @since 2019-11-14
 */
public class BeaconScaner {
    /**
     * error code, common error
     */
    public static final int ERR_COMMON = 201;

    /**
     * error code, no bluetooth found
     */
    public static final int ERR_NO_BT_ADAPTER = 202;

    /**
     * error code, ble not enabled
     */
    public static final int ERR_BT_NOT_ENABLED = 203;

    /**
     * error code, bluetooth not support scan
     */
    public static final int ERR_BT_NOT_SUPPORT_SCAN = 204;

    private static final String TAG = BeaconScaner.class.getSimpleName();

    private static final int BLE_ENABLE_WAIT_MS = 100;

    private static final int BLE_ENABLE_WAIT_RETRY = 20;

    private BluetoothAdapter bluetoothAdapter = null;

    private BluetoothLeScanner bleScanner = null;

    private ScanCallbackWarp scanCallback = null;

    private BeaconScanCb scanCb = null;

    /**
     * constructor
     */
    public BeaconScaner() {
    }

    private int obtainBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "fail to get bluetooth adapter");
                return ERR_NO_BT_ADAPTER;
            }
        }
        return 0;
    }

    private boolean enableBle() {
        if (bluetoothAdapter.isEnabled()) {
            return true;
        }

        if (!bluetoothAdapter.enable()) {
            BeaconBaseLog.e(TAG, "bluetooth enable failed");
            return false;
        }
        int retry = 0;
        while (retry < BLE_ENABLE_WAIT_RETRY) {
            if (bluetoothAdapter.isEnabled()) {
                return true;
            }
            SystemClock.sleep(BLE_ENABLE_WAIT_MS);
            retry++;
        }
        BeaconBaseLog.e(TAG, "bluetooth not enable");
        return false;
    }

    private int obtainBleScanner() {
        int ret = obtainBluetoothAdapter();
        if (ret != 0) {
            return ret;
        }

        if (!enableBle()) {
            BeaconBaseLog.e(TAG, "bluetooth not enable");
            return ERR_BT_NOT_ENABLED;
        }

        if (bleScanner == null) {
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bleScanner == null) {
                return ERR_BT_NOT_SUPPORT_SCAN;
            }
        }
        return 0;
    }

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder settingBuilder = new ScanSettings.Builder();
        settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settingBuilder.setLegacy(false);
            settingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        }
        ScanSettings scanSettings = settingBuilder.build();
        return scanSettings;
    }

    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> filters = new ArrayList<>();
        /* eddyStoneUid filter */
        ScanFilter.Builder eddyStoneUidFilterBuilder = new ScanFilter.Builder();
        eddyStoneUidFilterBuilder.setServiceData(new ParcelUuid(EddystoneUid.SERVICE_UUID),
                EddystoneUid.getDataFilter(), EddystoneUid.getDataFilterMask());
        /* iBeacon filter */
        ScanFilter.Builder iBeaconFilterBuilder = new ScanFilter.Builder();
        iBeaconFilterBuilder.setManufacturerData(IBeacon.COMPANY_ID,
                IBeacon.getDataFilter(), IBeacon.getDataFilterMask());
        filters.add(eddyStoneUidFilterBuilder.build());
        filters.add(iBeaconFilterBuilder.build());
        return filters;
    }

    /**
     * Start beacon scan process
     *
     * @param scanCb Callback used to deliver sharing data scan result
     * @return result code ERR_* in {@link BeaconScaner}
     */
    public int startSacn(BeaconScanCb scanCb) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return ERR_COMMON;
        }
        if (scanCallback != null) {
            return ERR_COMMON;
        }

        int ret = obtainBleScanner();
        if (ret != 0) {
            BeaconBaseLog.e(TAG, "fail to get BleScanner");
            return ret;
        }

        /* create scan settings */
        ScanSettings scanSettings = buildScanSettings();
        if (scanSettings == null) {
            BeaconBaseLog.e(TAG, "fail to create scanSettings");
            return ERR_COMMON;
        }

        /* create scan filters */
        List<ScanFilter> filters = buildScanFilters();
        this.scanCb = scanCb;
        scanCallback = new ScanCallbackWarp();
        try {
            bleScanner.startScan(filters, scanSettings, scanCallback);
            BeaconBaseLog.d(TAG, "start scan");
            return 0;
        } catch (IllegalStateException e) {
            BeaconBaseLog.e(TAG, e.getMessage());
            scanCallback = null;
        }
        return ERR_COMMON;
    }

    /**
     * stop beacon scan process
     */
    public void stopScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (scanCallback == null) {
            return;
        }

        try {
            bleScanner.stopScan(scanCallback);
        } catch (IllegalStateException e) {
            BeaconBaseLog.e(TAG, e.getMessage());
        }
        scanCallback = null;
        BeaconBaseLog.d(TAG, "stop scan");
    }

    private class ScanCallbackWarp extends ScanCallback {
        private void beaconFond(ScanResult result) {
            ScanRecord record = result.getScanRecord();
            if (record == null) {
                return;
            }
            byte[] data = record.getServiceData(new ParcelUuid(EddystoneUid.SERVICE_UUID));
            if ((data != null) && ((data.length == EddystoneUid.DATA_LEN) || (data.length == EddystoneUid.DATA_LEN2))
                && (data[EddystoneUid.TYPE_OFFSET] == EddystoneUid.EDDYSTONE_TYPE_UID)) {
                EddystoneUid.Builder builder = new EddystoneUid.Builder();
                builder.setRssi(result.getRssi());
                builder.setServiceData(data);
                scanCb.onFound(builder.build());
                return;
            }
            data = record.getManufacturerSpecificData(IBeacon.COMPANY_ID);
            if ((data != null) && (data.length == IBeacon.DATA_LEN)) {
                IBeacon.Builder builder = new IBeacon.Builder();
                builder.setRssi(result.getRssi());
                builder.setManufacturerData(data);
                scanCb.onFound(builder.build());
                return;
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            beaconFond(result);
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                beaconFond(result);
            }
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            bleScanner.stopScan(scanCallback);
            scanCallback = null;
            super.onScanFailed(errorCode);
            String result = "";
            switch (errorCode) {
                case 0: {
                    result += "Success!!";
                    break;
                }
                case ScanCallback.SCAN_FAILED_ALREADY_STARTED: {
                    result += "Already started.";
                    break;
                }
                case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED: {
                    result += "App cannot be registered.";
                    break;
                }
                case ScanCallback.SCAN_FAILED_INTERNAL_ERROR: {
                    result += "Internal error.";
                    break;
                }
                case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED: {
                    result += "This feature is not supported.";
                    break;
                }
                default: {
                    result += "Unknown error";
                    break;
                }
            }

            BeaconBaseLog.e(TAG, result);
            scanCb.onFaild(errorCode, result);
        }
    }
}
