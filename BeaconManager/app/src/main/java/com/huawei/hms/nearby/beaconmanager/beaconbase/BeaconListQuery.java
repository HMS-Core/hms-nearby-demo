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

package com.huawei.hms.nearby.beaconmanager.beaconbase;

import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconBriefInfo;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.QueryBeaconListParam;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.QueryBeaconListRequest;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.QueryBeaconListRes;

import java.util.ArrayList;

/**
 * Query Beacon List owned by current user
 *
 * @since 2019-11-18
 */
public class BeaconListQuery {
    private static final String TAG = BeaconListQuery.class.getSimpleName();

    private static final int PAGE_SIZE = 10;

    private static final int OP_CODE_NEXT = 1;

    private static final int OP_CODE_PREV = 2;

    private long minSeq;

    private long maxSeq;

    private boolean moreBeacon;

    /**
     * Constructor
     */
    public BeaconListQuery() {
        minSeq = 0;
        maxSeq = 0;
        moreBeacon = true;
    }

    /**
     * Whether has more beacon on server side
     *
     * @return true when has more beacon on server side
     */
    public boolean hasMoreBeacon() {
        return moreBeacon;
    }

    /**
     * Restart query all beacons from the first page
     */
    public void reStart() {
        minSeq = 0;
        maxSeq = 0;
        moreBeacon = true;
    }

    /**
     * Get next page Beacon list
     *
     * @return moreBeacon and Beacon list
     */
    public ArrayList<BeaconBriefInfo> nextPage() {
        if (!moreBeacon) {
            return new ArrayList<>();
        }
        QueryBeaconListRequest request = new QueryBeaconListRequest();
        QueryBeaconListParam param = new QueryBeaconListParam(OP_CODE_NEXT, PAGE_SIZE, maxSeq);
        BeaconRestfulClient.getInstance().queryBeaconList(param, request);
        if (request.waitComplete() != 0) {
            return new ArrayList<>();
        }
        QueryBeaconListRes res = request.getCloudRes();
        if (res == null) {
            return new ArrayList<>();
        }
        moreBeacon = (res.getMoreBeacon() == 0) ? false : true;
        ArrayList<BeaconBriefInfo> beaconList = res.getBeaconList();
        if (beaconList == null) {
            return new ArrayList<>();
        }
        if (!beaconList.isEmpty()) {
            minSeq = beaconList.get(0).getSequence();
            maxSeq = beaconList.get(beaconList.size() - 1).getSequence();
        }
        return beaconList;
    }

    /**
     * Get prev page Beacon list
     *
     * @return moreBeacon and Beacon list
     */
    public ArrayList<BeaconBriefInfo> prePage() {
        if (!moreBeacon) {
            return new ArrayList<>();
        }
        QueryBeaconListRequest request = new QueryBeaconListRequest();
        QueryBeaconListParam param = new QueryBeaconListParam(OP_CODE_PREV, PAGE_SIZE, minSeq);
        BeaconRestfulClient.getInstance().queryBeaconList(param, request);
        if (request.waitComplete() != 0) {
            return new ArrayList<>();
        }
        QueryBeaconListRes res = request.getCloudRes();
        if (res == null) {
            return new ArrayList<>();
        }
        moreBeacon = (res.getMoreBeacon() == 0) ? false : true;
        ArrayList<BeaconBriefInfo> beaconList = res.getBeaconList();
        if (!beaconList.isEmpty()) {
            minSeq = beaconList.get(0).getSequence();
            maxSeq = beaconList.get(beaconList.size() - 1).getSequence();
        }
        return beaconList;
    }
}
