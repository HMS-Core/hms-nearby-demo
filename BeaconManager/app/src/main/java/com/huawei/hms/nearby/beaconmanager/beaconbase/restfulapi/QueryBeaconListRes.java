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

package com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi;

import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconBriefInfo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Query Beacon List Response
 *
 * @since 2019-11-13
 */
public class QueryBeaconListRes extends CloudResponse {
    @SerializedName("moreBeacon")
    private int moreBeacon;

    @SerializedName("beacons")
    private ArrayList<BeaconBriefInfo> beaconList;

    public int getMoreBeacon() {
        return moreBeacon;
    }

    public ArrayList<BeaconBriefInfo> getBeaconList() {
        return beaconList;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "QueryBeaconListRes{moreBeacon:%d, beaconList:%s, error:%s}", moreBeacon,
            (beaconList == null) ? "" : beaconList.toString(), getErrMsg());
    }
}
