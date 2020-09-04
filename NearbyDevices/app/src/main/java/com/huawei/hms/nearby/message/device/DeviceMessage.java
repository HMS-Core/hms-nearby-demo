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

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;

import java.nio.charset.Charset;

public final class DeviceMessage {
    private static final Gson GSON = new Gson();
    private static volatile DeviceMessage sDeviceMessage;

    public static Message newNearbyGMSMessage(String instanceId) {
        if (sDeviceMessage == null) {
            synchronized (DeviceMessage.class) {
                if (sDeviceMessage == null) {
                    sDeviceMessage = new DeviceMessage(instanceId);
                }
            }
        }
        return new Message(GSON.toJson(sDeviceMessage).getBytes(Charset.forName("UTF-8")));
    }

    public static com.huawei.hms.nearby.message.Message newNearbyHMSMessage(String instanceId) {
        if (sDeviceMessage == null) {
            synchronized (DeviceMessage.class) {
                if (sDeviceMessage == null) {
                    sDeviceMessage = new DeviceMessage(instanceId);
                }
            }
        }
        return new com.huawei.hms.nearby.message.Message(GSON.toJson(sDeviceMessage)
                .getBytes(Charset.forName("UTF-8")));
    }

    private DeviceMessage(String uuid) {
    }
}