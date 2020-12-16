package com.wmq.hms.nearby.beaconmanager.softbeacon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.wmq.hms.nearby.beaconmanager.R;
import com.wmq.hms.nearby.beaconmanager.ui.Constant;

public class BroadcasterService extends Service {

    private static final String TAG = "BeaconBroadcaster";

    private static final int NOTIFICATION_ID = 2;

    private NotificationManager mNotificationManager;

    private Notification mNotification;

    private BroadcastReceiver mCloseSoftBeacon = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (TextUtils.equals(action, "click close soft beacon")) {
                mNotificationManager.cancel(NOTIFICATION_ID);
                SafeSharedPreferences.getInstance(context, Constant.SP_FILE_NAME, MODE_PRIVATE)
                        .putBoolean(Constant.SWITCH_STATE_KEY, false);
                stopService(new Intent(context, BroadcasterService.class));
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service 启动");
        BleUtil.startAdvertise(DeviceIdUtil.getDeviceId());
        initNotification();
        startForeground(1, mNotification);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(new NotificationChannel("soft notification", "name", NotificationManager.IMPORTANCE_DEFAULT));
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_view);

        IntentFilter filterClick = new IntentFilter();
        filterClick.addAction("click close soft beacon");
        registerReceiver(mCloseSoftBeacon, filterClick);

        Intent intentPre = new Intent("click close soft beacon");
        PendingIntent pendIntentClick = PendingIntent.getBroadcast(this, 0, intentPre, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_remote, pendIntentClick);

        mNotification = new NotificationCompat.Builder(this)
                .setContent(remoteViews)
                .setContentTitle("soft beacon")
                .setContentText("start broadcasts soft beacon")
                .setWhen(SystemClock.elapsedRealtime())
                .setSmallIcon(R.mipmap.canteen_a)
                .setAutoCancel(true)
                .setChannelId("soft notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "BroadcasterService onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "BroadcasterService onUnbind");
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG, "BroadcasterService onRebind");
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        BleUtil.stopAdvertise();
        mNotificationManager.cancel(NOTIFICATION_ID);
        unregisterReceiver(mCloseSoftBeacon);
    }
}
