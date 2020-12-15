package com.wmq.hms.nearby.beaconmanager.softbeacon;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.BarrierClient;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BarrierQueryRequest;
import com.huawei.hms.kit.awareness.barrier.BarrierStatus;
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest;
import com.huawei.hms.kit.awareness.barrier.BeaconBarrier;
import com.huawei.hms.kit.awareness.status.BeaconStatus;

import java.util.ArrayList;
import java.util.List;

public class AwarenessUtil {
    private static final String TAG = "BeaconBroadcaster";

    public static final String BARRIER_KEY = "BleBroadcasterBarrier";

    public static final String NAMESPACE = "dev736430079244481219";

    public static final String TYPE = "hwwallet";

    public static void addBarrier(Context context) {
        List<BeaconStatus.Filter> filters = new ArrayList<>();
        filters.add(BeaconStatus.Filter.match(NAMESPACE, TYPE));
//        filters.add(BeaconStatus.Filter.match());
        AwarenessBarrier beaconBarrier = BeaconBarrier.discover(filters);

        BarrierUpdateRequest addRequest = new BarrierUpdateRequest
                .Builder()
                .addBarrier(BARRIER_KEY, beaconBarrier, buildPendingIntent(context))
                .build();

        Awareness.getBarrierClient(context)
                .updateBarriers(addRequest)
                .addOnSuccessListener(bVoid -> {
                    Log.e(TAG, "Beacon围栏注册成功");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Beacon围栏注册失败", e);
                });
    }

    private static PendingIntent buildPendingIntent(Context context) {
        return PendingIntent.getService(context, 0, new Intent(context, HmsIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void restartBarrier(Context context) {
        BarrierUpdateRequest deleteRequest = new BarrierUpdateRequest
                .Builder()
                .deleteBarrier(BARRIER_KEY)
                .build();

        BarrierClient client = Awareness.getBarrierClient(context);
        client.queryBarriers(BarrierQueryRequest.all())
                .addOnSuccessListener(barrierQueryResponse -> {
                    BarrierStatus barrierStatus = barrierQueryResponse.getBarrierStatusMap().getBarrierStatus(BARRIER_KEY);
                    if (barrierStatus != null) {
                        client.updateBarriers(deleteRequest)
                                .addOnSuccessListener(aVoid -> {
                                    addBarrier(context);
                                    Log.e(TAG, "Beacon围栏取消成功");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Beacon围栏取消失败", e);
                                });
                    } else {
                        addBarrier(context);
                    }
                });

    }

}
