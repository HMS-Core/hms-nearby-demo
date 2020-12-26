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
 * Query Beacon Attachment Request
 *
 * @since 2019-11-18
 */
public class QueryAttachmentCb implements Callback<QueryBeaconAttachmentRes> {
    private static final int FAIL_UNKNOW_QUERY_ATTACHMENT = -1;

    private static final int FAIL_TIMEOUT_QUERY_ATTACHMENT = -5;

    private static final String TAG = "QueryAttachmentCb";

    /**
     * Semaphone for wait cloud request complete
     */
    private Semaphore semaphoreQueryAttachment;

    /**
     * Cloud request result
     */
    private long result;

    /**
     * Response for cloud request
     */
    private QueryBeaconAttachmentRes queryBeaconAttachmentRes;

    public QueryBeaconAttachmentRes getQueryBeaconAttachmentRes() {
        return queryBeaconAttachmentRes;
    }

    /**
     * Constructor
     */
    public QueryAttachmentCb() {
        semaphoreQueryAttachment = new Semaphore(0);
        result = FAIL_TIMEOUT_QUERY_ATTACHMENT;
    }

    /**
     * Wait query attachment response for this request
     *
     * @return result code
     */
    public long waitComplete() {
        try {
            if (semaphoreQueryAttachment.tryAcquire(3, TimeUnit.SECONDS)) {
                return result;
            }
            return FAIL_UNKNOW_QUERY_ATTACHMENT;
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
            return FAIL_UNKNOW_QUERY_ATTACHMENT;
        }
    }

    @Override
    public void onResponse(Call<QueryBeaconAttachmentRes> call, Response<QueryBeaconAttachmentRes> response) {
        if (response.isSuccessful()) {
            queryBeaconAttachmentRes = response.body();
            result = 0;
            semaphoreQueryAttachment.release();
            Log.i(TAG, "Response OK");
            return;
        }

        try {
            String errString = response.errorBody().string();
            Log.e(TAG, "Response Err: " + errString);
            CloudResponseError error = new Gson().fromJson(errString, CloudResponseError.class);
            queryBeaconAttachmentRes.setResponseError(error);
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, "errorBody get failed:" + e.getMessage());
        }
        result = response.code();
        semaphoreQueryAttachment.release();
        return;
    }

    @Override
    public void onFailure(Call<QueryBeaconAttachmentRes> call, Throwable throwable) {
        Log.e(TAG, "Request fail for:" + throwable.getMessage());
        result = FAIL_UNKNOW_QUERY_ATTACHMENT;
        semaphoreQueryAttachment.release();
    }
}
