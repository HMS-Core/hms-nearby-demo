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

import com.huawei.hms.nearby.beaconmanager.BuildConfig;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconBaseLog;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.Attachment;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconInfo;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.Namespace;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.QueryBeaconListParam;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Beacon Restful class
 *
 * @since 2019-11-13
 */
public class BeaconRestfulClient {
    /**
     * Callback when Access Token Time Expired
     */
    public interface AccessTokenTimeExpiredCallback {
        /**
         * Callback when Access Token Time Expired
         */
        void accessTokenTimeExpired();
    }

    /**
     * ERR_FAIL
     */
    public static final long ERR_FAIL = -1;

    /**
     * ERR_ACCESS_TOKEN_NEEDED
     */
    public static final long ERR_ACCESS_TOKEN_NEEDED = -2;

    private static final String TAG = BeaconRestfulClient.class.getSimpleName();

    private static final String HOST = BuildConfig.MESSAGE_HOST;

    private static final String PORT = BuildConfig.MESSAGE_PORT;

    private static volatile BeaconRestfulClient sInstance = null;

    private String baseUrl;

    private HeaderParamsInterceptor headerParamsInterceptor;

    private Retrofit retrofit;

    private BeaconRestfulInterface restfulService;

    private String accessToken;

    private AccessTokenTimeExpiredCallback tokenTimeExpiredCallback;

    private final ScheduledExecutorService scheduledExecutorService = ScheduledExecutors.newScheduledThreadPool(1);

    /**
     * Constructor
     */
    private BeaconRestfulClient() {
        tokenTimeExpiredCallback = null;
        if (PORT.length() != 0) {
            baseUrl = String.format(Locale.ENGLISH, "https://%s:%s/WiseCloudNearbyBeaconService/v1/", HOST, PORT);
        } else {
            baseUrl = String.format(Locale.ENGLISH, "https://%s/WiseCloudNearbyBeaconService/v1/", HOST);
        }

        headerParamsInterceptor = new HeaderParamsInterceptor();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.callTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .hostnameVerifier(new BrowserCompatHostnameVerifier())
            .addInterceptor(headerParamsInterceptor);
        OkHttpClient httpClient = httpClientBuilder.build();

        retrofit = new Retrofit.Builder().client(httpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .callbackExecutor(new CallBackExecutor())
            .build();

        restfulService = retrofit.create(BeaconRestfulInterface.class);
    }

    /**
     * NullOnEmptyConverterFactory
     */
    private class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
            Retrofit retrofit) {
            Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return body -> {
                Object obj = null;
                if (body.contentLength() != 0) {
                    obj = delegate.convert(body);
                }
                return obj;
            };
        }
    }

    /**
     * CallBackExecutor
     */
    private class CallBackExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            scheduledExecutorService.execute(command);
        }
    }

    /**
     * Get single instance of BeaconRestfulClient.
     *
     * @return The instance of BeaconRestfulClient.
     */
    public static BeaconRestfulClient getInstance() {
        if (sInstance == null) {
            synchronized (BeaconRestfulClient.class) {
                if (sInstance == null) {
                    sInstance = new BeaconRestfulClient();
                }
            }
        }
        return sInstance;
    }

    /**
     * Check whether Access Token had been set
     *
     * @return true when Access Token had been set, else false
     */
    public boolean isAccessTokenSet() {
        if ((accessToken == null) || (accessToken.length() == 0)) {
            return false;
        }
        return true;
    }

    /**
     * for advance connection
     *
     * @param accessToken access token return by OAuth
     */
    public void setAccessToken(String accessToken) {
        if ((accessToken == null) || (accessToken.length() == 0)) {
            return;
        }
        this.accessToken = accessToken;
        headerParamsInterceptor.setAuthorization(accessToken);
    }

    /**
     * Access token timeout
     */
    public void setAccessTokenTimeout() {
        accessToken = null;
        if (tokenTimeExpiredCallback != null) {
            tokenTimeExpiredCallback.accessTokenTimeExpired();
        }
    }

    /**
     * Set callback to info user that access token time expired, need relogin
     *
     * @param callback callback
     */
    public void setAccessTokenTimeExpiredCallback(AccessTokenTimeExpiredCallback callback) {
        tokenTimeExpiredCallback = callback;
    }

    /**
     * Get beacon register status
     *
     * @param beaconId beacon id
     * @param callback ResultCallback
     */
    public long queryBeaconRegisterStatus(String beaconId, Callback<BeaconStatusRes> callback) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }

        BeaconBaseLog.d(TAG, "queryBeaconRegisterStatus: " + beaconId);
        restfulService.queryBeaconRegisterStatus(beaconId).enqueue(callback);
        return 0;
    }

    /**
     * Register a beacon to cloud
     *
     * @param beaconInfo beacon to register
     * @return register result:
     *         ERR_FAIL -- failed to post the request
     *         0 -- success
     *         100 - 510 HTML status code return by html response
     */
    public long registerBeacon(BeaconInfo beaconInfo) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }
        if (!beaconInfo.validityCheck()) {
            return ERR_FAIL;
        }
        BeaconBaseLog.d(TAG, "registerBeacon: " + beaconInfo.getBeaconId());
        BeaconInfoCloudReqParam param = new BeaconInfoCloudReqParam(beaconInfo);
        CloudRequest request = new CloudRequest();
        restfulService.registerBeacon(param).enqueue(request);
        return request.waitComplete();
    }

    /**
     * Update beacon information to cloud
     *
     * @param beaconInfo beaconInfo Beacon Information
     * @return register result:
     *         ERR_FAIL failed to post the request
     *         0 success
     *         100 - 510 HTML status code return by html response
     */
    public long updateBeacon(BeaconInfo beaconInfo) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }
        if (!beaconInfo.validityCheck()) {
            return ERR_FAIL;
        }
        BeaconBaseLog.d(TAG, "updateBeaconInfo: " + beaconInfo.getBeaconId());
        BeaconInfoCloudReqParam param = new BeaconInfoCloudReqParam(beaconInfo);
        CloudRequest request = new CloudRequest();
        restfulService.updateBeaconInfo(param).enqueue(request);
        return request.waitComplete();
    }

    /**
     * Unregister beacon from cloud
     *
     * @param beaconId beacon Id
     * @return register result:
     *         ERR_FAIL failed to post the request
     *         0 success
     *         100 - 510 HTML status code return by html response
     */
    public long unRegisterBeacon(String beaconId) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }
        BeaconBaseLog.d(TAG, "deleteBeacon: " + beaconId);
        CloudRequest request = new CloudRequest();
        restfulService.deleteBeacon(beaconId).enqueue(request);
        return request.waitComplete();
    }

    /**
     * Active beacon from cloud, customer can get the attachment of that beacon
     *
     * @param beaconId Beacon Id
     * @return register result:
     *         ERR_FAIL -- failed to post the request
     *         0 -- success
     *         100 - 510 HTML status code return by html response
     */
    public long activeBeacon(String beaconId) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }
        BeaconBaseLog.d(TAG, "activeBeacon: " + beaconId);
        BeaconIdCloudReqParam param = new BeaconIdCloudReqParam(beaconId);
        CloudRequest request = new CloudRequest();
        restfulService.activeBeacon(param).enqueue(request);
        return request.waitComplete();
    }

    /**
     * DeActive beacon from cloud, customer can't get the attachment of that beacon
     *
     * @param beaconId Beacon Id
     * @return register result:
     *         ERR_FAIL -- failed to post the request
     *         0 -- success
     *         100 - 510 HTML status code return by html response
     */
    public long deactiveBeacon(String beaconId) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }
        BeaconBaseLog.d(TAG, "deactiveBeacon: " + beaconId);
        BeaconIdCloudReqParam param = new BeaconIdCloudReqParam(beaconId);
        CloudRequest request = new CloudRequest();
        restfulService.deactiveBeacon(param).enqueue(request);
        return request.waitComplete();
    }

    /**
     * Decommission beacon from cloud
     *
     * @param beaconId Beacon Id
     * @return register result:
     *         ERR_FAIL -- failed to post the request
     *         0 -- success
     *         100 - 510 HTML status code return by html response
     */
    public long abandonBeacon(String beaconId) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }
        BeaconBaseLog.d(TAG, "abandonBeacon: " + beaconId);
        BeaconIdCloudReqParam param = new BeaconIdCloudReqParam(beaconId);
        CloudRequest request = new CloudRequest();
        restfulService.abandonBeacon(param).enqueue(request);
        return request.waitComplete();
    }

    /**
     * Query Beacon information
     *
     * @param beaconId Specify which beacon to query
     * @return BeaconInfo of specified beacon
     */
    public BeaconInfo queryBeaconInfo(String beaconId) {
        BeaconInfo beaconInfo = null;
        if (accessToken == null) {
            return beaconInfo;
        }

        BeaconBaseLog.d(TAG, "queryBeaconInfo: " + beaconId);
        QueryBeaconInfoRequest request = new QueryBeaconInfoRequest();
        restfulService.queryBeaconInfo(beaconId).enqueue(request);
        if (request.waitComplete() != 0) {
            return beaconInfo;
        }
        QueryBeaconInfoRes response = request.getQueryBeaconInfoRes();
        beaconInfo = response.getBeaconInfo();
        beaconInfo.convertPropertisFromStr();
        return beaconInfo;
    }

    /**
     * Query Beacons bylongs to the current huawei account and appId, retrun at most 100 beacons for each query
     *
     * @param param QueryBeaconListParam
     * @param callback ResultCallback
     */
    public void queryBeaconList(QueryBeaconListParam param, Callback<QueryBeaconListRes> callback) {
        if (accessToken == null) {
            return;
        }
        BeaconBaseLog.d(TAG, String.format(Locale.ENGLISH, "queryBeaconList, upOrDown:%d, seq:%d", param.getUpOrDown(),
            param.getSequence()));
        restfulService.queryBeaconList(param).enqueue(callback);
    }

    /**
     * Get all Attachments for Beacon specified by beaconId
     *
     * @param beaconId beaconId Specify which beacon to query
     * @return the attachment list of specified beacon
     */
    public ArrayList<Attachment> queryBeaconAttachment(String beaconId) {
        ArrayList<Attachment> attachments = null;
        if (accessToken == null) {
            return attachments;
        }
        BeaconBaseLog.d(TAG, "queryBeaconAttachment: " + beaconId);
        QueryBeaconAttachmentRequest request = new QueryBeaconAttachmentRequest();
        restfulService.queryBeaconAttachment(beaconId).enqueue(request);
        if (request.waitComplete() != 0) {
            return attachments;
        }
        QueryBeaconAttachmentRes response = request.getQueryBeaconAttachmentRes();
        attachments = response.getAttachments();
        if (attachments == null) {
            return new ArrayList<>();
        }
        return attachments;
    }

    /**
     * Add attachments of beacon to cloud
     *
     * @param beaconId beacon id
     * @param attachment attachment
     * @return update result:
     *         ERR_FAIL -- failed to post the request
     *         0 -- success
     *         100 - 510 HTML status code return by html response
     */
    public Attachment addAttachment(String beaconId, Attachment attachment) {
        Attachment attachmentReturn = null;
        if (accessToken == null) {
            return attachmentReturn;
        }
        BeaconBaseLog.d(TAG,
            String.format(Locale.ENGLISH, "addAttachment: beacon:%s, attachment:%s", beaconId, attachment.toString()));
        UpdateAttachmentParam param = new UpdateAttachmentParam(attachment);
        UpdateAttachmentRequest request = new UpdateAttachmentRequest();
        restfulService.updateAttachment(beaconId, param).enqueue(request);
        if (request.waitComplete() != 0) {
            return attachmentReturn;
        }
        attachmentReturn = request.getUpdateAttachmentRes().getAttachment();
        return attachmentReturn;
    }

    /**
     * Delete attachments of beacon devices
     *
     * @param beaconId beacon id
     * @param attachmentId attachment Id
     * @return delete result:
     *         ERR_FAIL -- failed to post the request
     *         0 -- success
     *         100 - 510 HTML status code return by html response
     */
    public long deleteAttachment(String beaconId, String attachmentId) {
        if (accessToken == null) {
            return ERR_ACCESS_TOKEN_NEEDED;
        }
        BeaconBaseLog.d(TAG,
            String.format(Locale.ENGLISH, "deleteAttachment: beacon:%s, attachment:%s", beaconId, attachmentId));
        CloudRequest request = new CloudRequest();
        restfulService.deleteAttachment(beaconId, attachmentId).enqueue(request);
        return request.waitComplete();
    }

    /**
     * Get all Namespaces
     *
     * @return the Namespace list
     */
    public ArrayList<Namespace> queryNamespace() {
        ArrayList<Namespace> namespaces = null;
        if (accessToken == null) {
            return namespaces;
        }
        BeaconBaseLog.d(TAG, "queryNamespace");
        QueryNamespaceRequest request = new QueryNamespaceRequest();
        restfulService.queryNamespaceList().enqueue(request);
        if (request.waitComplete() != 0) {
            return namespaces;
        }
        QueryNamespaceRes response = request.getQueryNamespaceRes();
        namespaces = response.getNamespaceList();
        if (namespaces == null) {
            return new ArrayList<>();
        }
        return namespaces;
    }
}
