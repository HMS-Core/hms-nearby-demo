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

package com.huawei.hms.nearby.message.device;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.huawei.hms.nearby.message.GetCallback;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.PutCallback;
import com.huawei.hms.nearby.message.PutOption;

public class MessageClient {
    private static final String TAG = MessageClient.class.getSimpleName();

    private static final int PUBLISH_TTL = 240;

    private MessagesClient mGMSEngine;

    private MessageEngine mHMSEngine;

    public MessageClient(Context context) {
        mGMSEngine = Nearby.getMessagesClient(context);
        mHMSEngine = com.huawei.hms.nearby.Nearby.getMessageEngine(context);
    }

    public void subscribe(MessageListener listener, MessageHandler handler, String mode) {
        String[] modes = mode.split("\\+");
        if (modes[0].equals(NearbyUtil.GMS_ONLY)) {
            Log.i(TAG, "GMS start subscribe...");
            Strategy strategy = new Strategy.Builder().setTtlSeconds(Strategy.TTL_SECONDS_INFINITE)
                    .build();
            SubscribeOptions subscribeOptions = new SubscribeOptions.Builder()
                    .setStrategy(strategy)
                    .setCallback(new MySubscribeCallback())
                    .build();
            mGMSEngine.subscribe(listener, subscribeOptions).addOnCompleteListener(gmsTask -> {
                Log.i(TAG, "GMS subscribe successful: " + gmsTask.isSuccessful());
                if (modes.length == 2) {
                    Log.i(TAG, "HMS start subscribe...");
                    Policy policy = new Policy.Builder().setTtlSeconds(Policy.POLICY_TTL_SECONDS_INFINITE)
                            .build();
                    GetOption getOption = new GetOption.Builder().setPolicy(policy)
                            .setCallback(new MyGetCallback())
                            .build();
                    mHMSEngine.get(handler, getOption).addOnCompleteListener(hmsTask -> {
                        Log.i(TAG, "HMS subscribe successful: " + hmsTask.isSuccessful());
                    });
                }
            });
        }
        if (modes[0].equals(NearbyUtil.HMS_ONLY)) {
            Log.i(TAG, "HMS start subscribe...");
            Policy policy = new Policy.Builder().setTtlSeconds(Policy.POLICY_TTL_SECONDS_INFINITE)
                    .build();
            GetOption getOption = new GetOption.Builder().setPolicy(policy)
                    .setCallback(new MyGetCallback())
                    .build();
            mHMSEngine.get(handler, getOption).addOnCompleteListener(hmsTask -> {
                Log.i(TAG, "HMS subscribe successful: " + hmsTask.isSuccessful());
                if (modes.length == 2) {
                    Log.i(TAG, "GMS start subscribe...");
                    Strategy strategy = new Strategy.Builder().setTtlSeconds(Strategy.TTL_SECONDS_INFINITE)
                            .build();
                    SubscribeOptions subscribeOptions = new SubscribeOptions.Builder().setStrategy(strategy)
                            .setCallback(new MySubscribeCallback())
                            .build();
                    mGMSEngine.subscribe(listener, subscribeOptions).addOnCompleteListener(gmsTask -> {
                        Log.i(TAG, "GMS subscribe successful: " + gmsTask.isSuccessful());
                    });
                }
            });
        }
    }

    public void unsubscribe(MessageListener listener, MessageHandler handler, String mode) {
        String[] modes = mode.split("\\+");
        for (String singleMode : modes) {
            if (singleMode.equals(NearbyUtil.GMS_ONLY)) {
                Log.i(TAG, "GMS start unsubscribe...");
                mGMSEngine.unsubscribe(listener).addOnCompleteListener(task -> {
                    Log.i(TAG, "GMS unsubscribe successful: " + task.isSuccessful());
                });
            }
            if (singleMode.equals(NearbyUtil.HMS_ONLY)) {
                Log.i(TAG, "HMS start unsubscribe...");
                mHMSEngine.unget(handler).addOnCompleteListener(task -> {
                    Log.i(TAG, "HMS unsubscribe successful: " + task.isSuccessful());
                });
            }
        }
    }

    public void publish(Message gmsMessage, com.huawei.hms.nearby.message.Message hmsMessage, String mode) {
        String[] modes = mode.split("\\+");
        if (modes[0].equals(NearbyUtil.GMS_ONLY)) {
            Log.i(TAG, "GMS start publish...");
            Strategy strategy = new Strategy.Builder().setTtlSeconds(PUBLISH_TTL)
                    .build();
            PublishOptions publishOptions = new PublishOptions.Builder().setStrategy(strategy)
                    .setCallback(new MyPublishCallback())
                    .build();
            mGMSEngine.publish(gmsMessage, publishOptions).addOnCompleteListener(gmsTask -> {
                Log.i(TAG, "GMS publish successful: " + gmsTask.isSuccessful());
                if (modes.length == 2) {
                    Log.i(TAG, "HMS start publish...");
                    Policy policy = new Policy.Builder().setTtlSeconds(PUBLISH_TTL)
                            .build();
                    PutOption putOption = new PutOption.Builder().setPolicy(policy)
                            .setCallback(new MyPutCallback())
                            .build();
                    mHMSEngine.put(hmsMessage, putOption).addOnCompleteListener(hmsTask -> {
                        Log.i(TAG, "HMS publish successful: " + hmsTask.isSuccessful());
                    });
                }
            });
        }
        if (modes[0].equals(NearbyUtil.HMS_ONLY)) {
            Log.i(TAG, "HMS start publish...");
            Policy policy = new Policy.Builder().setTtlSeconds(PUBLISH_TTL)
                    .build();
            PutOption putOption = new PutOption.Builder().setPolicy(policy)
                    .setCallback(new MyPutCallback())
                    .build();
            mHMSEngine.put(hmsMessage, putOption).addOnCompleteListener(hmsTask -> {
                Log.i(TAG, "HMS publish successful: " + hmsTask.isSuccessful());
                if (modes.length == 2) {
                    Log.i(TAG, "GMS start publish...");
                    Strategy strategy = new Strategy.Builder().setTtlSeconds(PUBLISH_TTL)
                            .build();
                    PublishOptions publishOptions = new PublishOptions.Builder().setStrategy(strategy)
                            .setCallback(new MyPublishCallback())
                            .build();
                    mGMSEngine.publish(gmsMessage, publishOptions).addOnCompleteListener(gmsTask -> {
                        Log.i(TAG, "GMS publish successful: " + gmsTask.isSuccessful());
                    });
                }
            });
        }
    }

    public void unpublish(Message gmsMessage, com.huawei.hms.nearby.message.Message hmsMessage, String mode) {
        String[] modes = mode.split("\\+");
        for (String singleMode : modes) {
            if (singleMode.equals(NearbyUtil.GMS_ONLY)) {
                Log.i(TAG, "GMS start unpublish...");
                mGMSEngine.unpublish(gmsMessage).addOnCompleteListener(gmsTask -> {
                    Log.i(TAG, "GMS unpublish successful: " + gmsTask.isSuccessful());
                });
            }
            if (singleMode.equals(NearbyUtil.HMS_ONLY)) {
                Log.i(TAG, "HMS start unpublish...");
                mHMSEngine.unput(hmsMessage).addOnCompleteListener(hmsTask -> {
                    Log.i(TAG, "HMS unpublish successful: " + hmsTask.isSuccessful());
                });
            }
        }
    }

    static class MyPutCallback extends PutCallback {
        @Override
        public void onTimeout() {
            Log.i(TAG, "HMS publish expired");
        }
    }

    static class MyGetCallback extends GetCallback {
        @Override
        public void onTimeout() {
            Log.i(TAG, "HMS subscribe expired");
        }
    }

    static class MySubscribeCallback extends SubscribeCallback {
        @Override
        public void onExpired() {
            Log.i(TAG, "GMS subscribe expired");
        }
    }

    static class MyPublishCallback extends PublishCallback {
        @Override
        public void onExpired() {
            Log.i(TAG, "GMS publish expired");
        }
    }
}
