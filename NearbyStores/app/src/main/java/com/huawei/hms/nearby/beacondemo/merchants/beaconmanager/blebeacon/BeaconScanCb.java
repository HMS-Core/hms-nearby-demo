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

package com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.blebeacon;

/**
 * BeaconScanCb
 *
 * @since 2019-11-14
 */
public abstract class BeaconScanCb {
    /**
     * Callback when scan could not be started.
     *
     * @param result result code SCAN_FAILED_* in {@link android.bluetooth.le.ScanCallback}
     * @param info detail information
     */
    public void onFaild(int result, String info) {
    }

    /**
     * Callback When nearby sharing data has been found.
     *
     * @param beacon beacon scan result
     */
    public void onFound(Beacon beacon) {
    }
}
