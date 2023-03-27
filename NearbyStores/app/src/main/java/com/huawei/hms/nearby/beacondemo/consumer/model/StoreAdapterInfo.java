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

package com.huawei.hms.nearby.beacondemo.consumer.model;

/**
 * Store Adapter Info
 *
 * @since 2019-12-13
 */
public class StoreAdapterInfo {
    private String storeImageUrl;
    private String storeName;
    private String storeDesc;
    private boolean isShowNotice;
    private String notice;
    private int requestCode;

    /**
     * Construct
     */
    public StoreAdapterInfo() {
        this(null, null, null);
    }

    /**
     * Construct
     *
     * @param storeName The Image of the Store
     * @param storeDesc The Image of the Store
     * @param storeImageUrl The Image of the Store
     */
    public StoreAdapterInfo(String storeName, String storeDesc, String storeImageUrl) {
        this.storeName = storeName;
        this.storeDesc = storeDesc;
        this.storeImageUrl = storeImageUrl;
    }

    public boolean isShowNotice() {
        return isShowNotice;
    }

    public void setShowNotice(boolean showNotice) {
        isShowNotice = showNotice;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getStoreImageUrl() {
        return storeImageUrl;
    }

    public void setStoreImageUrl(String storeImageUrl) {
        this.storeImageUrl = storeImageUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreDesc() {
        return storeDesc;
    }

    public void setStoreDesc(String storeDesc) {
        this.storeDesc = storeDesc;
    }

}
