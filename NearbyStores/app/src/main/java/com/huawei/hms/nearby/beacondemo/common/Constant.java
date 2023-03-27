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

package com.huawei.hms.nearby.beacondemo.common;

/**
 * Constant
 *
 * @since 2019-11-14
 */
public class Constant {
    /**
     * BEACONS_EDIT_STATUS
     */
    public static final String BEACONS_EDIT_STATUS = "beacons_edit_status";

    /**
     * BEACONS_INFO
     */
    public static final String BEACONS_LIST_POSTION = "beacons_list_positon";

    /**
     * BEACONS_INFO
     */
    public static final String BEACONS_INFO = "beacons_info";

    /**
     * AC_OP_EDIT_BEACON
     */
    public static final int AC_OP_EDIT_BEACON = 8001;

    /**
     * AC_OP_EDIT_BEACON
     */
    public static final int AC_OP_VIEW_BEACON = 8002;

    /**
     * AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS
     */
    public static final int AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS = 8004;

    /**
     * AC_OP_EDIT_SOFT_BEACON
     */
    public static final int AC_OP_EDIT_SOFT_BEACON = 8006;

    /**
     * Bluetooth warn
     */
    public static final String BLUETOOTH_WARN
        = "Bluetooth is disconnected, this operation will cause the application to be out of normal use!";

    /**
     * Bluetooth error
     */
    public static final String BLUETOOTH_ERROR = "Bluetooth is invalid! Turn on Bluetooth and run this app again.";

    /**
     * Network error
     */
    public static final String NETWORK_ERROR
        = "No Internet access! Make sure you have Internet access and run this app again.";

    /**
     * Network warn
     */
    public static final String NETWORK_WARN
        = "The network is disconnected, this operation will cause the application to be out of normal use!";

    /**
     * Gps warn
     */
    public static final String GPS_WARN
        = "GPS is turned off, this operation will cause the application to be out of normal use!";

    /**
     * Gps error
     */
    public static final String GPS_ERROR = "GPS is invalid! Turn on GPS and run this app again.";

    /**
     * Soft Beacon state choice
     */
    public static final String SP_FILE_NAME = "switch_state";

    /**
     * Soft Beacon state
     */
    public static final String SWITCH_STATE_KEY = "state";

    /**
     * Attachment Str
     */
    public static final String TYPE_ATTACHMENT = "attachment";

    /**
     * IBeacon set self
     */
    public static final String IBEACON_SET_SELF = "set_self";

    /**
     * IBeacon power
     */
    public static final String IBEACON_POWER = "ibeacon_power";

    /**
     * IBeacon power
     */
    public static final String IBEACON_POWER_RSSI = "ibeacon_power_rssi";
}
