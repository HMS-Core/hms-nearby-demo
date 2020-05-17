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

package com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon;

import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconUtil;

import java.util.UUID;

/**
 * EddystoneUid
 *
 * @since 2019-11-14
 */
public class EddystoneUid extends Beacon {
    /**
     * Eddystone service uuid (0xfeaa)
     */
    public static final UUID SERVICE_UUID = UUID.fromString("0000feaa-0000-1000-8000-00805f9b34fb");

    /**
     * Eddystone service data filter
     */
    public static final byte EDDYSTONE_TYPE_UID = 0x00;

    /**
     * Eddystone service data filter
     */
    public static final byte[] DATA_FILTER = new byte[] {0x00};

    /**
     * Eddystone service data filter mask *
     */
    public static final byte[] DATA_FILTER_MASK = new byte[] {(byte) 0xf};

    /**
     * service data length of EddystoneUid
     * |type(1)|txpower(1)|namespace id(10)|instance(6)|
     */
    public static final int DATA_LEN = 18;

    /**
     * service data length of EddystoneUid
     * some eddystone device ble advertising data length is 20B
     * |type(1)|txpower(1)|namespace id(10)|instance(6)|RFU(2)|
     */
    public static final int DATA_LEN2 = 20;

    /**
     * name space ID length of EddystoneUid
     */
    public static final int NAME_SPACE_ID_LEN = 10;

    /**
     * Eddystone frame type offset in service data
     */
    public static final byte TYPE_OFFSET = 0;

    /**
     * instance Id length of EddystoneUid
     */
    public static final int INSTANCE_ID_LEN = 6;

    /**
     * BEACON ID length of EddystoneUid
     */
    public static final int BEACON_ID_LEN = 16;

    /**
     * Eddystone txpower offset in service data
     */
    private static final byte TXPOWER_OFFSET = 1;

    /**
     * name space ID offset of EddystoneUid
     */
    private static final int NAME_SPACE_ID_OFFSET = 2;

    /**
     * instance Id offset of EddystoneUid
     */
    private static final int INSTANCE_ID_OFFSET = 12;

    private static final int TX_POWER_CALIBRATE = 41;

    private EddystoneUid(int rssi, byte[] serviceData) {
        beaconType = BEACON_EDDYSTONE_UID;
        this.rssi = rssi;
        txPower = serviceData[TXPOWER_OFFSET];
        txPower -= TX_POWER_CALIBRATE;
        beaconId = new byte[NAME_SPACE_ID_LEN + INSTANCE_ID_LEN];
        System.arraycopy(serviceData, NAME_SPACE_ID_OFFSET, beaconId, 0, NAME_SPACE_ID_LEN + INSTANCE_ID_LEN);
    }

    @Override
    public String getHexId() {
        return BeaconUtil.bytesToHexString(beaconId);
    }

    public static byte[] getDataFilter() {
        return DATA_FILTER;
    }

    public static byte[] getDataFilterMask() {
        return DATA_FILTER_MASK;
    }

    /**
     * Builder for UID eddystone instance
     *
     * @since 2019-11-04
     */
    public static class Builder {
        private byte[] serviceData;

        private int rssi;

        /**
         * Specify rssi for EddystoneUid
         *
         * @param rssi ble rssi
         * @return EddystoneUid Builder
         */
        public EddystoneUid.Builder setRssi(int rssi) {
            this.rssi = rssi;
            return this;
        }

        /**
         * Specify Service Data for EddystoneUid
         *
         * @param serviceData Service Data
         * @return EddystoneUid Builder
         */
        public EddystoneUid.Builder setServiceData(byte[] serviceData) {
            this.serviceData = serviceData;
            return this;
        }

        /**
         * Create an EddystoneUid instance
         *
         * @return EddystoneUid instance
         */
        public EddystoneUid build() {
            return new EddystoneUid(rssi, serviceData);
        }
    }
}
