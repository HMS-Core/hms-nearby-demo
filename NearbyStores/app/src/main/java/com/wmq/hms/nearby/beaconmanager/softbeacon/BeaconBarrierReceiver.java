package com.wmq.hms.nearby.beaconmanager.softbeacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.hms.kit.awareness.barrier.BarrierStatus;

public class BeaconBarrierReceiver extends BroadcastReceiver {
    private static final String TAG = "BeaconBroadcaster";

    @Override
    public void onReceive(Context context, Intent intent) {
//        startService(context);
        Log.i(TAG, " status:statusstatusstatus");
        BarrierStatus barrierStatus = BarrierStatus.extract(intent);
        String label = barrierStatus.getBarrierLabel();
        switch(barrierStatus.getPresentStatus()) {
            case BarrierStatus.TRUE:
                Log.i(TAG, label + " status:true");
                break;
            case BarrierStatus.FALSE:
                Log.i(TAG, label + " status:false");
                break;
            case BarrierStatus.UNKNOWN:
                Log.i(TAG, label + " status:unknown");
                break;
        }
    }

    private void startService(Context context) {
        Intent serviceIntent = new Intent(context, BroadcasterService.class);
        context.startService(serviceIntent);
    }

}
