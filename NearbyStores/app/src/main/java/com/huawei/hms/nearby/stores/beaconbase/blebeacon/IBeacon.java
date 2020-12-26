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

import com.huawei.hms.nearby.stores.beaconbase.BeaconUtil;

/**
 * IBeacon
 *
 * @since 2019-11-14
 */
public class IBeacon extends Beacon {
    /**
     * iBeacon company (0x004c)
     */
    public static final int COMPANY_ID = 0x004c;

    /**
     * Manufacturer data length of iBeacon
     * |beaconType(2)|uuid(16)|major(2)|minor(2)|txpower(1)|
     */
    public static final int DATA_LEN = 23;

    /**
     * uuid length in byte
     */
    public static final int UUID_LEN = 16;

    /**
     * major length in byte
     */
    public static final int MAJOR_LEN = 2;

    /**
     * minor length in byte
     */
    public static final int MINOR_LEN = 2;

    /**
     * BEACON ID length of EddystoneUid
     */
    public static final int BEACON_ID_LEN = 20;

    /**
     * iBeacon Manufacturer data filter
     */
    private static final byte[] DATA_FILTER_IBEACON = new byte[] {0x02, 0x15};

    /**
     * iBeacon Manufacturer data filter mask
     */
    private static final byte[] DATA_FILTER_MASK_IBEACON = new byte[] {(byte) 0xff, (byte) 0xff};

    private static final int UUID_OFFSET = 2;

    private static final int TXPOWER_OFFSET = 22;

    private IBeacon(int rssi, byte[] manufacturerData) {
        beaconType = BEACON_IBEACON;
        this.rssi = rssi;
        beaconId = new byte[UUID_LEN + MAJOR_LEN + MINOR_LEN];
        System.arraycopy(manufacturerData, UUID_OFFSET, beaconId, 0, UUID_LEN + MAJOR_LEN + MINOR_LEN);
        txPower = manufacturerData[TXPOWER_OFFSET];
    }

    @Override
    public String getHexId() {
        return BeaconUtil.bytesToHexString(beaconId);
    }

    public static byte[] getDataFilter() {
        return DATA_FILTER_IBEACON.clone();
    }

    public static byte[] getDataFilterMask() {
        return DATA_FILTER_MASK_IBEACON.clone();
    }

    /**
     * Builder for IBeacon instance
     *
     * @since 2019-11-04
     */
    public static class Builder {
        private byte[] manufacturerData;

        private int rssi;

        /**
         * Specify rssi for ibeacon
         *
         * @param rssi ble rssi
         * @return IBeacon Builder
         */
        public IBeacon.Builder setRssi(int rssi) {
            this.rssi = rssi;
            return this;
        }

        /**
         * Specify Manufacturer Data for ibeacon
         *
         * @param data Manufacturer Data
         * @return IBeacon Builder
         */
        public IBeacon.Builder setManufacturerData(byte[] data) {
            manufacturerData = data.clone();
            return this;
        }

        /**
         * Create an IBeacon instance
         *
         * @return IBeacon instance
         */
        public IBeacon build() {
            return new IBeacon(rssi, manufacturerData);
        }
    }
}
