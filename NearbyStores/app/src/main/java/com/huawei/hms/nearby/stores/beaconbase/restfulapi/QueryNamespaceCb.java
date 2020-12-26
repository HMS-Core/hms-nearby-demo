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
 * Query Namespace Request
 *
 * @since 2020-01-03
 */
public class QueryNamespaceCb implements Callback<QueryNamespaceRes> {
    private static final int FAIL_UNKNOW_QUERY_NAME = -1;

    private static final int FAIL_TIMEOUT_QUERY_NAME = -5;

    private static final String TAG = "QueryNamespaceCb";

    /**
     * Semaphone for wait cloud request complete
     */
    private Semaphore semaphoreQueryName;

    /**
     * QueryNamespaceTypeRequest result
     */
    private long result;

    /**
     * Response for QueryNamspaceRequest
     */
    private QueryNamespaceRes queryNamespaceRes;

    /**
     * Constructor
     */
    public QueryNamespaceCb() {
        semaphoreQueryName = new Semaphore(0);
        result = FAIL_TIMEOUT_QUERY_NAME;
    }

    public QueryNamespaceRes getQueryNamespaceRes() {
        return queryNamespaceRes;
    }

    /**
     * Wait query namespace request complete, and get the result code
     *
     * @return query namespace request result:
     *         -1 failed to post the request
     *         0 success
     *         100 - 510 HTML status code return by html response
     */
    public long waitComplete() {
        try {
            if (semaphoreQueryName.tryAcquire(3, TimeUnit.SECONDS)) {
                return result;
            }
            return FAIL_UNKNOW_QUERY_NAME;
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
            return FAIL_UNKNOW_QUERY_NAME;
        }
    }

    @Override
    public void onResponse(Call<QueryNamespaceRes> call, Response<QueryNamespaceRes> response) {
        if (response.isSuccessful()) {
            queryNamespaceRes = response.body();
            result = 0;
            semaphoreQueryName.release();
            Log.i(TAG, "Response OK");
            return;
        }

        try {
            String errString = response.errorBody().string();
            Log.e(TAG, "Response Err: " + errString);
            CloudResponseError error = new Gson().fromJson(errString, CloudResponseError.class);
            queryNamespaceRes.setResponseError(error);
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, "errorBody get failed:" + e.getMessage());
        }
        result = response.code();
        semaphoreQueryName.release();
    }

    @Override
    public void onFailure(Call<QueryNamespaceRes> call, Throwable throwable) {
        Log.e(TAG, "Request fail for:" + throwable.getMessage());
        result = FAIL_UNKNOW_QUERY_NAME;
        semaphoreQueryName.release();
    }
}
