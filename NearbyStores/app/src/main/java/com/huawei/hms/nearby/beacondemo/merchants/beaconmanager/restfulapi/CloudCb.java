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
 * Cloud Request Object
 *
 * @since 2019-11-18
 */
public class CloudCb implements Callback<CloudResponse> {
    private static final int FAIL_UNKNOW_CLOUD= -1;

    private static final int FAIL_TIMEOUT_CLOUD= -5;

    private static final String TAG = "CloudCb";

    /**
     * Semaphone for wait cloud request complete
     */
    protected Semaphore semaphoreCloud;

    /**
     * Cloud request result
     */
    protected long result;

    /**
     * Response for cloud request
     */
    protected CloudResponse cloudResponse;

    /**
     * Constructor
     */
    public CloudCb() {
        semaphoreCloud = new Semaphore(0);
        result = FAIL_TIMEOUT_CLOUD;
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
            if (semaphoreCloud.tryAcquire(3, TimeUnit.SECONDS)) {
                return result;
            }
            return FAIL_UNKNOW_CLOUD;
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
            return FAIL_UNKNOW_CLOUD;
        }
    }

    /**
     * Get the cloud response data
     *
     * @return cloud response data
     */
    public CloudResponse getCloudResponse() {
        return cloudResponse;
    }

    @Override
    public void onResponse(Call<CloudResponse> call, Response<CloudResponse> response) {
        if (response.isSuccessful()) {
            cloudResponse = new CloudResponse();
            result = 0;
            semaphoreCloud.release();
            Log.i(TAG, "Response OK");
            return;
        }

        result = response.code();
        try {
            String errString = response.errorBody().string();
            Log.e(TAG, "Response Err:" + errString);
            CloudResponseError error = new Gson().fromJson(errString, CloudResponseError.class);
            cloudResponse.setResponseError(error);
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, "errorBody get failed:" + e.getMessage());
        }
        semaphoreCloud.release();
    }

    @Override
    public void onFailure(Call<CloudResponse> call, Throwable throwable) {
        Log.e(TAG, "Request fail for:" + throwable.getMessage());
        result = FAIL_UNKNOW_CLOUD;
        semaphoreCloud.release();
    }
}
