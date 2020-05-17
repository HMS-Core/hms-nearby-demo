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

package com.huawei.hms.nearby.beaconmanager.beaconbase.model;

import com.google.gson.annotations.SerializedName;

/**
 * Attachment Cloud Req Param
 *
 * @since 2020-01-13
 */
public class AttachmentCloudReqParam {
    @SerializedName("attachment")
    private Attachment attachment;

    /**
     * Constructor
     *
     * @param attachment attachment
     */
    public AttachmentCloudReqParam(Attachment attachment) {
        this.attachment = attachment;
    }
}
