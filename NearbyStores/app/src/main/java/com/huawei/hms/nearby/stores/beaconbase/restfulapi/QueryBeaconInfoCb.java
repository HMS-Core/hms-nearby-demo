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

package com.huawei.hms.nearby.stores.beaconbase.restfulapi;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Query Beacon Info Request
 *
 * @since 2019-11-18
 */
public class QueryBeaconInfoCb implements Callback<QueryBeaconInfoRes> {
    private static final int FAIL_UNKNOW_QUERY_BEACON = -1;

    private static final int FAIL_TIMEOUT_QUERY_BEACON = -5;

    private static final String TAG = "QueryBeaconInfoCb";

    /**
     * Semaphone for wait cloud request complete
     */
    private Semaphore semaphoreQueryBeaconInfo;

    /**
     * Cloud request result
     */
    private long result;

    /**
     * Response for cloud request
     */
    private QueryBeaconInfoRes queryBeaconInfoRes;

    public QueryBeaconInfoRes getQueryBeaconInfoRes() {
        return queryBeaconInfoRes;
    }

    /**
     * Constructor
     */
    public QueryBeaconInfoCb() {
        semaphoreQueryBeaconInfo = new Semaphore(0);
        result = FAIL_TIMEOUT_QUERY_BEACON;
    }

    /**
     * Wait query beacon response for this request
     *
     * @return result code
     */
    public long waitComplete() {
        try {
            if (semaphoreQueryBeaconInfo.tryAcquire(3, TimeUnit.SECONDS)) {
                return result;
            }
            return FAIL_UNKNOW_QUERY_BEACON;
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
            return FAIL_UNKNOW_QUERY_BEACON;
        }
    }

    @Override
    public void onResponse(Call<QueryBeaconInfoRes> call, Response<QueryBeaconInfoRes> response) {
        if (response.isSuccessful()) {
            queryBeaconInfoRes = response.body();
            result = 0;
            semaphoreQueryBeaconInfo.release();
            Log.i(TAG, "Response OK");
            return;
        }

        try {
            String errString = response.errorBody().string();
            Log.e(TAG, "Response Err: " + errString);
            CloudResponseError error = new Gson().fromJson(errString, CloudResponseError.class);
            queryBeaconInfoRes.setResponseError(error);
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, "errorBody get failed:" + e.getMessage());
        }
        result = response.code();
        semaphoreQueryBeaconInfo.release();
    }

    @Override
    public void onFailure(Call<QueryBeaconInfoRes> call, Throwable throwable) {
        Log.e(TAG, "Request fail for:" + throwable.getMessage());
        result = FAIL_UNKNOW_QUERY_BEACON;
        semaphoreQueryBeaconInfo.release();
    }
}
