package com.wmq.hms.nearby.beaconmanager.softbeacon;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
                        //开始扫描
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
