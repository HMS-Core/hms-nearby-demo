package com.wmq.hms.nearby.beaconmanager.softbeacon;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.huawei.hms.kit.awareness.barrier.BarrierStatus;

public class HmsIntentService extends IntentService {
    private static final String TAG = "BeaconBroadcaster";

    public HmsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        Intent serviceIntent = new Intent(this, BroadcasterService.class);
//        this.startService(serviceIntent);

        Log.i(TAG, "Beacon barrier notification!");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}