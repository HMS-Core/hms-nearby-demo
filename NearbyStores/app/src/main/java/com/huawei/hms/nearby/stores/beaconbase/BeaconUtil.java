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

package com.huawei.hms.nearby.stores.beaconbase;

import android.util.Base64;

import com.huawei.hms.nearby.stores.beaconbase.blebeacon.Beacon;
import com.huawei.hms.nearby.stores.beaconbase.model.BeaconStatus;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Hex String Utils
 *
 * @since 2019-11-11
 */
public class BeaconUtil {
    private static final int HEX_CHAR_BYTES = 2;

    private static final double DECAY_FACTOR = 2.5;

    private static final double POW_BASE = 10;

    private BeaconUtil() {
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes byte array
     * @return hex string
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * HEX_CHAR_BYTES);
        for (int index = 0; index < bytes.length; index++) {
            builder.append(String.format(Locale.ENGLISH, "%02x", bytes[index]));
        }
        return builder.toString();
    }

    /**
     * Calculate the distance based on rssi received
     *
     * @param rssi rssi received
     * @param txPower power level at 1 meter, in dBm
     * @return distance in meter
     */
    public static double calDistance(int rssi, int txPower) {
        double power = (double) (txPower - rssi) / (POW_BASE * DECAY_FACTOR);
        double distance = Math.pow(POW_BASE, power);
        BigDecimal decimal = new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_UP);
        return decimal.doubleValue();
    }

    /**
     * Convert beacon status to string
     *
     * @param status beacon status
     * @return beacon status string
     */
    public static String beaconStatus2String(int status) {
        String str;
        switch (status) {
            case BeaconStatus.UNREGISTERED: {
                str = "UNREGISTERED";
                break;
            }
            case BeaconStatus.INIT: {
                str = "INIT";
                break;
            }
            case BeaconStatus.ACTIVE: {
                str = "ACTIVE";
                break;
            }
            case BeaconStatus.DEACTIVE: {
                str = "DEACTIVE";
                break;
            }
            case BeaconStatus.ABANDONED: {
                str = "ABANDONED";
                break;
            }
            default: {
                str = "UNKNOWN";
                break;
            }
        }
        return str;
    }

    /**
     * Convert beacon type to string
     *
     * @param type beacon type
     * @return beacon status string
     */
    public static String beaconType2String(int type) {
        String str;
        switch (type) {
            case Beacon.BEACON_IBEACON: {
                str = "iBeacon";
                break;
            }
            case Beacon.BEACON_EDDYSTONE_UID: {
                str = "Eddystone-UID";
                break;
            }
            case Beacon.BEACON_EDDYSTONE_EID: {
                str = "Eddystone-EID";
                break;
            }
            case Beacon.BEACON_ALT_EDDYSTONE: {
                str = "AltBeacon";
                break;
            }
            default: {
                str = "Unknown";
                break;
            }
        }
        return str;
    }

    /**
     * Base64 encode
     *
     * @param data data
     * @return base64 string
     */
    public static String bytesToBase64Str(byte[] data) {
        if (data == null) {
            return "";
        }

        byte[] encodeData = Base64.encode(data, Base64.NO_WRAP);
        String strBase64 = new String(encodeData, Charset.forName("UTF-8"));
        return strBase64;
    }

    /**
     * Base64 decode
     *
     * @param strBase64 base64 string
     * @return data
     */
    public static byte[] base64StrToBytes(String strBase64) {
        if ((strBase64 == null) || (strBase64.length() == 0)) {
            return new byte[0];
        }
        byte[] data = Base64.decode(strBase64.getBytes(Charset.forName("UTF-8")), Base64.NO_WRAP);
        return data;
    }
}
