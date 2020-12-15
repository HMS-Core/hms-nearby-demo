package com.wmq.hms.nearby.beaconmanager.softbeacon;

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

public class BleUtil {
    private static final String TAG = "BeaconBroadcaster";

    private static BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private static BluetoothAdapter mBluetoothAdapter;

    private static String uuid;


    public static void startAdvertise(String deviceId) {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            return;
        }
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }

        uuid = deviceId;

        AdvertiseSettings settings = createAdvertiseSettings();
        AdvertiseData data = createAdvertiseData();
        //-59是 measuredPower,一般设备默认都是-59，这里固定了
        Log.e(TAG, "开始广播");
        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void stopAdvertise() {
        mBluetoothLeAdvertiser.stopAdvertising(mAdvCallback);
        mBluetoothLeAdvertiser.stopAdvertisingSet(new AdvertisingSetCallback() {
            @Override
            public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                Log.e(TAG, "stop");
            }
        });
    }

    private static AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
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
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        return builder.build();
    }

    private static AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        String beaconType = "0215"; //按照apple iBeacon协议
        String measuredPower = Conversion.formatStringLenth(2, Integer.toHexString(-59), '0');
        String dataStr = beaconType + uuid + measuredPower;
        Log.d(TAG, "AdvertiseSettings dataStr : " + dataStr);

        byte[] data = Conversion.hexStringToBytes(dataStr);
        mDataBuilder.addManufacturerData(0x004C, data);//004c是厂商id，代表apple公司
        return mDataBuilder.build();
    }
}
