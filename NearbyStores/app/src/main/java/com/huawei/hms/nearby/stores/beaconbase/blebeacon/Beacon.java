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

package com.huawei.hms.nearby.stores.beaconbase.blebeacon;

/**
 * Beacon
 *
 * @since 2019-11-14
 */
public abstract class Beacon {
    /**
     * IBEACON
     */
    public static final byte BEACON_IBEACON = 1;

    /**
     * EDDYSTONE_UID
     */
    public static final byte BEACON_EDDYSTONE_UID = 3;

    /**
     * EDDYSTONE_EID
     */
    public static final byte BEACON_EDDYSTONE_EID = 4;

    /**
     * ALT_EDDYSTONE
     */
    public static final byte BEACON_ALT_EDDYSTONE = 5;

    /**
     * beacon type
     */
    protected byte beaconType;

    /**
     * rssi
     */
    protected int rssi;

    /**
     * txPower
     */
    protected int txPower;

    /**
     * beacon Id
     */
    protected byte[] beaconId;

    /**
     * Get beaconid in HexString format
     *
     * @return HexString beaconid
     */
    public abstract String getHexId();

    public int getRssi() {
        return rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public byte getBeaconType() {
        return beaconType;
    }
}
