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

import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconBaseLog;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Query Beacon List Request
 *
 * @since 2020-01-15
 */
public class QueryBeaconListRequest implements Callback<QueryBeaconListRes> {
    private static final String TAG = QueryBeaconListRequest.class.getSimpleName();

    private static final int FAIL_UNKNOW = -1;

    private static final int FAIL_TIMEOUT = -5;

    /**
     * Semaphone for wait cloud request complete
     */
    private Semaphore semaphore;

    /**
     * Cloud request result
     */
    private long result;

    private QueryBeaconListRes beaconListRes;

    /**
     * Constructor
     */
    public QueryBeaconListRequest() {
        semaphore = new Semaphore(0);
        beaconListRes = null;
        result = FAIL_TIMEOUT;
    }

    /**
     * Get the cloud response data
     *
     * @return cloud response data
     */
    public QueryBeaconListRes getCloudRes() {
        return beaconListRes;
    }

    /**
     * Wait cloud request complete, and get the result code
     *
     * @return Cloud request result:
     *         -1 failed to post the request
     *         0 success
     *         100 - 510 HTML status code return by html response
     */
    public long waitComplete() {
        try {
            semaphore.tryAcquire(3, TimeUnit.SECONDS);
            return result;
        } catch (InterruptedException e) {
            BeaconBaseLog.e(TAG, e.toString());
            return FAIL_UNKNOW;
        }
    }

    @Override
    public void onResponse(Call<QueryBeaconListRes> call, Response<QueryBeaconListRes> response) {
        if (response.isSuccessful()) {
            beaconListRes = response.body();
            result = 0;
            semaphore.release();
            return;
        }
        try {
            String errString = response.errorBody().string();
            BeaconBaseLog.e(TAG, "Response Err:" + errString);
            CloudResponseError error = new Gson().fromJson(errString, CloudResponseError.class);
            beaconListRes.setResponseError(error);
        } catch (IOException | JsonSyntaxException e) {
            BeaconBaseLog.e(TAG, "errorBody get failed:" + e.getMessage());
        }
        result = response.code();
        semaphore.release();
    }

    @Override
    public void onFailure(Call<QueryBeaconListRes> call, Throwable throwable) {
        BeaconBaseLog.e(TAG, "Request fail for:" + throwable.getMessage());
        result = FAIL_UNKNOW;
        semaphore.release();
    }
}
