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

package com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.restfulapi;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Beacon Register Status Response
 *
 * @since 2019-11-13
 */
public class BeaconStatusRes extends CloudResponse {
    @SerializedName("status")
    private int registerStatus = 0;

    public int getRegisterStatus() {
        return registerStatus;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "BeaconStatusRes{registerStatus:%d, error:%s}", registerStatus,
            getErrMsg());
    }
}
