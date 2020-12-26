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

package com.huawei.hms.nearby.stores.beaconbase.model;

import com.google.gson.annotations.SerializedName;

/**
 * Restful client param for queryBeaconList
 *
 * @since 2019-11-19
 */
public class QueryBeaconListParam {
    @SerializedName("upOrDown")
    int upOrDown;

    @SerializedName("pageSize")
    int pageSize;

    @SerializedName("sequence")
    long sequence;

    /**
     * Constructor
     *
     * @param upOrDown Page forward or Page backward
     * @param pageSize Specify beacon count per page
     * @param sequence If page forward, the maximum value of the previous page;
     *        if page backward, the minimum value of the previous page.
     */
    public QueryBeaconListParam(int upOrDown, int pageSize, long sequence) {
        this.upOrDown = upOrDown;
        this.pageSize = pageSize;
        this.sequence = sequence;
    }

    public int getUpOrDown() {
        return upOrDown;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getSequence() {
        return sequence;
    }

}
