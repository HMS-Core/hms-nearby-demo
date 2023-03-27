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

package com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Beacon Brief Information
 *
 * @since 2019-11-13
 */
public class BeaconBriefInfo extends BeaconBaseInfo {
    @SerializedName("beaconDesc")
    private String beaconDesc;

    @SerializedName("status")
    private int status;

    /* internal use, only used for cloud query through page after page, APP should ignore this field */
    @SerializedName("sequence")
    private long sequence;

    /**
     * Constructor, create beacon info.
     *
     * @param beaconId beacon id
     * @param beaconType beacon type
     * @param beaconDesc beacon description
     * @param status beacon status
     */
    public BeaconBriefInfo(String beaconId, int beaconType, String beaconDesc, int status, long sequence) {
        this.beaconId = beaconId;
        this.beaconType = beaconType;
        this.beaconDesc = beaconDesc;
        this.status = status;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "BeaconBriefInfo{beaconId:%s, beaconType:%d,beaconDesc:%s,status:%d}",
            beaconId, beaconType, beaconDesc, status);
    }
}
