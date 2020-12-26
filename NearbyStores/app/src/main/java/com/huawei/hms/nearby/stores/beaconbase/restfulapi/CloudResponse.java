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

/**
 * Cloud Response Object
 *
 * @since 2019-10-30
 */
public class CloudResponse {
    /**
     * Cloud response error
     */
    protected transient CloudResponseError responseError;

    /**
     * Get cloud error info
     *
     * @return error info
     */
    public String getErrMsg() {
        if (responseError != null) {
            return responseError.getErrMsg();
        } else {
            return "Success";
        }
    }

    public CloudResponseError getResponseError() {
        return responseError;
    }

    public void setResponseError(CloudResponseError responseError) {
        this.responseError = responseError;
    }
}
