package com.wmq.hms.nearby.beaconmanager.softbeacon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.wmq.hms.nearby.beaconmanager.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifiManager {
    private static volatile NotifiManager mSp;

    private NotificationManager mNotificationManager;

    private Notification mNotification;

    private Context mContext;

    private BroadcastReceiver mBroadcastReceiver;

    private int mNotififactionId;

    public NotifiManager(Context context, int notififactionId, BroadcastReceiver broadcastReceiver){
        this.mContext = context;
        this.mBroadcastReceiver = broadcastReceiver;
        this.mNotififactionId = notififactionId;
        init();
    }

    public static NotifiManager getInstance(Context context, int notififactionId, BroadcastReceiver broadcastReceiver) {
        if (mSp == null) {
            synchronized (NotifiManager.class) {
                if (mSp == null) {
                    mSp = new NotifiManager(context, notififactionId, broadcastReceiver);
                }
            }
        }
        return mSp;
    }

    public void init() {
        mNotificationManager = (android.app.NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(new NotificationChannel("soft notification", "name", android.app.NotificationManager.IMPORTANCE_DEFAULT));
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_view);

        IntentFilter filterClick = new IntentFilter();
        filterClick.addAction("click close soft beacon");
        mContext.registerReceiver(mBroadcastReceiver, filterClick);

        Intent intentPre = new Intent("click close soft beacon");
        PendingIntent pendIntentClick = PendingIntent.getBroadcast(mContext, 0, intentPre, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_remote, pendIntentClick);

        mNotification = new NotificationCompat.Builder(mContext)
                .setContent(remoteViews)
                .setContentTitle("soft beacon")
                .setContentText("start broadcasts soft beacon")
                .setWhen(SystemClock.elapsedRealtime())
                .setSmallIcon(R.mipmap.canteen_a)
                .setAutoCancel(true)
                .setChannelId("soft notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    public void notifyNotification() {
        mNotificationManager.notify(mNotififactionId, mNotification);
    }

    public void cancelNotification() {
        mNotificationManager.cancel(mNotififactionId);
    }
}
