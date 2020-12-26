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

package com.huawei.hms.nearby.stores.canteen;

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
