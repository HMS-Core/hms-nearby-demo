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

import com.huawei.hms.nearby.beaconmanager.beaconbase.model.QueryBeaconListParam;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Beacon Restful Interface
 *
 * @since 2019-11-13
 */
public interface BeaconRestfulInterface {
    /**
     * Query Beacon Status Restful API
     *
     * @param beaconId beacon id
     * @return BeaconStatusRes
     */
    @GET("beaconRegisterStatus/{beaconId}")
    Call<BeaconStatusRes> queryBeaconRegisterStatus(@Path("beaconId") String beaconId);

    /**
     * Register Beacon Restful API
     *
     * @param param Param
     * @return CloudResponse
     */
    @POST("registerBeacon")
    Call<CloudResponse> registerBeacon(@Body BeaconInfoCloudReqParam param);

    /**
     * Update Beacon Restful API
     *
     * @param param Param
     * @return CloudResponse
     */
    @PUT("updateBeaconInfo")
    Call<CloudResponse> updateBeaconInfo(@Body BeaconInfoCloudReqParam param);

    /**
     * Delete Beacon Restful API
     *
     * @param beaconId beaconId
     * @return CloudResponse
     */
    @DELETE("{beaconId}")
    Call<CloudResponse> deleteBeacon(@Path("beaconId") String beaconId);

    /**
     * Active Beacon Restful API
     *
     * @param beaconId beaconId
     * @return CloudResponse
     */
    @POST("activeBeacon")
    Call<CloudResponse> activeBeacon(@Body BeaconIdCloudReqParam beaconId);

    /**
     * Deactive Beacon Restful API
     *
     * @param beaconId beaconId
     * @return CloudResponse
     */
    @POST("deactiveBeacon")
    Call<CloudResponse> deactiveBeacon(@Body BeaconIdCloudReqParam beaconId);

    /**
     * Abandon Beacon Restful API
     *
     * @param beaconId beaconId
     * @return CloudResponse
     */
    @POST("abandonBeacon")
    Call<CloudResponse> abandonBeacon(@Body BeaconIdCloudReqParam beaconId);

    /**
     * Query Beacon List
     *
     * @param param QueryBeaconListParam
     * @return QueryBeaconListRes
     */
    @POST("queryBeaconList")
    Call<QueryBeaconListRes> queryBeaconList(@Body QueryBeaconListParam param);

    /**
     * Query Beacon List
     *
     * @param beaconId beaconId
     * @return QueryBeaconInfoRes
     */
    @GET("queryBeaconInfo/{beaconId}")
    Call<QueryBeaconInfoRes> queryBeaconInfo(@Path("beaconId") String beaconId);

    /**
     * Get all Attachments belong to current appid for Beacon specified by beaconId
     *
     * @param beaconId beaconId
     * @return QueryBeaconAttachmentRes
     */
    @GET("queryBeaconAttachment/{beaconId}")
    Call<QueryBeaconAttachmentRes> queryBeaconAttachment(@Path("beaconId") String beaconId);

    /**
     * Update Beacon Attachments
     *
     * @param beaconId beacon id
     * @param attachment Attachment
     * @return CloudResponse
     */
    @POST("updateAttachment/{beaconId}")
    Call<UpdateAttachmentRes> updateAttachment(@Path("beaconId") String beaconId,
        @Body UpdateAttachmentParam attachment);

    /**
     * Delete Beacon Attachments
     *
     * @param beaconId beaconId
     * @param attachmentId attachmentId
     * @return CloudResponse
     */
    @DELETE("deleteAttachment/{beaconId}/{attachmentId}")
    Call<CloudResponse> deleteAttachment(@Path("beaconId") String beaconId, @Path("attachmentId") String attachmentId);

    /**
     * Query all namespace type for namespace
     *
     * @return QueryNamespaceTypeListRes
     */
    @GET("queryNamespace")
    Call<QueryNamespaceRes> queryNamespaceList();
}
