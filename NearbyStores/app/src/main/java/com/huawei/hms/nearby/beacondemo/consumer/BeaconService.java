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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.huawei.hms.nearby.beacondemo.R;
import com.huawei.hms.nearby.message.BeaconInfo;

import java.util.List;

/**
 * 接收扫描到beacon的服务
 *
 * @since 2023-02-23
 */
public class BeaconService extends Service {
    private static final String TAG = "BeaconService";

    // 0：丢失；1：发现
    private static final String KEY_SCAN_ONFOUND_FLAG = "SCAN_ONFOUND_FLAG";

    // 发现或丢失的Beacon信息
    private static final String KEY_SCAN_BEACON_DATA = "SCAN_BEACON";

    private static final String BEACON_NOTIFY_CHANNEL_ID = "beacon_notification_channel_id";

    private static final String BEACON_NOTIFY_CHANNEL_NAME = "beacon_notification_channel_name";

    private static final int NOTIFICATION_SERVICE_ID = 101;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        new Handler().postDelayed(this::startForeground, 1000);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        startForeground();
        receiverBeaconInfo(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void startForeground() {
        // 通知栏逻辑可自行处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(BEACON_NOTIFY_CHANNEL_ID, BEACON_NOTIFY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, BEACON_NOTIFY_CHANNEL_ID)
            // 设置通知图标
            .setSmallIcon(R.mipmap.ic_launcher)
            // 设置通知标题
            .setContentTitle("信标提醒")
            // 设置通知内容
            .setContentText("发现信标").setAutoCancel(true).setOngoing(true).build();
        // Android 8.0以后系统不希望后台应用后台服务，所以在Service中需调用startForegroud()，否则可能出现ANR或者Crash
        startForeground(NOTIFICATION_SERVICE_ID, notification);
    }

    private void receiverBeaconInfo(Intent intent){
        if (intent == null) {
            Log.e(TAG, "intent is null");
            return;
        }
        int onFound = intent.getIntExtra(KEY_SCAN_ONFOUND_FLAG, 0);
        Log.i(TAG, "onFound:" + onFound);

        // 获取Beacon信息，做业务处理
        List<BeaconInfo> beaconList = intent.getParcelableArrayListExtra(KEY_SCAN_BEACON_DATA);
        if (beaconList == null) {
            Log.w(TAG, "beaconList is null");
            return;
        }
        for (BeaconInfo beacon : beaconList) {
            Log.i(TAG, "onReceive onFound, onFound:" + onFound + ", beaconId:" + beacon.getBeaconId());
        }

        // 通过获取到的Beacon信息，可以进行界面显示或是后台业务处理
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
