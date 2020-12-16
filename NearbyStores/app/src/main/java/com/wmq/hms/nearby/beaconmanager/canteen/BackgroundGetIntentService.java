package com.wmq.hms.nearby.beaconmanager.canteen;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;

public class BackgroundGetIntentService extends IntentService {
    private static final String TAG = "NearbyStoresTAG";

    public BackgroundGetIntentService(String name) {
        super(name);
    }

    protected void onHandleIntent(Intent intent) {
        MessageEngine messageEngine = Nearby.getMessageEngine(this);
        messageEngine.handleIntent(intent, new MessageHandler() {
            @Override
            public void onFound(Message message) {
                Log.i(TAG, " onFound " + new String(message.getContent()));
            }

            @Override
            public void onLost(Message message) {
                Log.i(TAG, " onLost " + new String(message.getContent()));
            }
        });
    }
}
