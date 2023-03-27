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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Add comm HTTP HEADER for all request
 *
 * @since 2019-11-06
 */
public final class CommHeaderIntercept implements Interceptor {
    private static final String TAG = "CommHeaderIntercept";

    private static final int MAX_RANDOM_CODE_NUM = 7;

    private static final int OFFSET = 538309;

    private static final String S_DATE_FORMAT = "yyyyMMddHHmmssSSS";

    private String authorization;

    /**
     * Constructor
     */
    CommHeaderIntercept() {
        authorization = null;
    }

    public void setAuthorization(String accessToken) {
        authorization = String.format(Locale.ENGLISH, "Bearer %s", accessToken);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
            .addHeader("srcTranID", new SimpleDateFormat(S_DATE_FORMAT).format(new Date()) + getRandomCode())
            .addHeader("timeStamp", String.valueOf(System.currentTimeMillis() / 1000))
            .addHeader("Accept", "application/json");

        if (authorization != null && authorization.length() != 0) {
            requestBuilder.addHeader("Authorization", authorization);
        }
        Request request = requestBuilder.build();

        Response response = chain.proceed(request);
        Log.d(TAG, "response code:" + response.code() + " info:" + response.message());
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            BeaconRestfulClient.getInstance().setAccessTokenTimeout();
        }
        return response;
    }

    private static String getRandomCode() {
        StringBuffer sb = new StringBuffer();
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            String codeList = "1234567890";

            for (int i = 0; i < MAX_RANDOM_CODE_NUM; i++) {
                int code = secureRandom.nextInt(codeList.length() - 1);
                sb.append(codeList.substring(code, code + 1));
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException!!!");
        }

        return sb.toString();
    }
}
