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

package com.huawei.hms.nearby.stores.ui;

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
     * BEACONS_ID
     */
    public static final String BEACONS_ID = "beacons_id";

    /**
     * BEACONS_INFO
     */
    public static final String BEACONS_LIST_POSTION = "beacons_list_positon";

    /**
     * BEACONS_SOFT
     */
    public static final String BEACONS_SOFT = "beacons_soft_position";

    /**
     * BEACONS_INFO
     */
    public static final String BEACONS_INFO = "beacons_info";

    /**
     * BEACONS_INFO_SOFT
     */
    public static final String BEACONS_INFO_SOFT = "beacons_info_soft";

    /**
     * BEACONS_ATTACHMENT_TYPE
     */
    public static final String BEACONS_ATTACHMENT_TYPE = "beacons_attachment_type";

    /**
     * BEACONS_ATTACHMENT
     */
    public static final String BEACONS_ATTACHMENT = "beacons_attachment";

    /**
     * AC_OP_SIGN_IN
     */
    public static final int AC_OP_SIGN_IN = 8000;

    /**
     * AC_OP_EDIT_BEACON
     */
    public static final int AC_OP_EDIT_BEACON = 8001;

    /**
     * AC_OP_EDIT_BEACON
     */
    public static final int AC_OP_VIEW_BEACON = 8002;

    /**
     * AC_OP_EDIT_BEACON_SPULEMENT
     */
    public static final int AC_OP_EDIT_BEACON_SPULEMENT = 8003;

    /**
     * AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS
     */
    public static final int AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS = 8004;

    /**
     * AC_OP_REQUEST_STORAGE_PERMISSIONS
     */
    public static final int AC_OP_REQUEST_STORAGE_PERMISSIONS = 8005;

    /**
     * AC_OP_EDIT_SOFT_BEACON
     */
    public static final int AC_OP_EDIT_SOFT_BEACON = 8006;

    /**
     * Canteen Name Str
     */
    public static final String CANTEEN_NAME = "canteenName";
    /**
     * Channel Id
     */
    public static final String CHANNEL_ID = "canteen";
    /**
     * Channel Name
     */
    public static final String CHANNEL_NAME = "Set meal recommend";
    /**
     * Canteen A Name
     */
    public static final String CANTEEN_A_NAME = "Canteen A";
    /**
     * Canteen B Name
     */
    public static final String CANTEEN_B_NAME = "Canteen B";
    /**
     * Canteen C Name
     */
    public static final String CANTEEN_C_NAME = "Canteen C";
    /**
     * Canteen A Request Code
     */
    public static final int CANTEEN_A_REQUEST_CODE = 0;
    /**
     * Canteen B Request Code
     */
    public static final int CANTEEN_B_REQUEST_CODE = 0;
    /**
     * Canteen C Request Code
     */
    public static final int CANTEEN_C_REQUEST_CODE = 0;
    /**
     * Notice Str
     */
    public static final String NOTICE = "notice";
    /**
     * Canteen Str
     */
    public static final String CANTEEN = "canteen";
    /**
     * Notification title
     */
    public static final String NOTIFICATION_TITLE = "Discover a new canteen";
    /**
     * Notification subtitle
     */
    public static final String NOTIFICATION_SUBTITLE = "Click \"View Details\" for more information.";
    /**
     * Bluetooth warn
     */
    public static final String BLUETOOTH_WARN =
            "Bluetooth is disconnected, this operation will cause the application to be out of normal use!";
    /**
     * Bluetooth error
     */
    public static final String BLUETOOTH_ERROR = "Bluetooth is invalid! Turn on Bluetooth and run this app again.";
    /**
     * Network error
     */
    public static final String NETWORK_ERROR =
            "No Internet access! Make sure you have Internet access and run this app again.";
    /**
     * Network warn
     */
    public static final String NETWORK_WARN =
            "The network is disconnected, this operation will cause the application to be out of normal use!";
    /**
     * Gps warn
     */
    public static final String GPS_WARN =
            "GPS is turned off, this operation will cause the application to be out of normal use!";
    /**
     * Gps error
     */
    public static final String GPS_ERROR = "GPS is invalid! Turn on GPS and run this app again.";

    /**
     * Soft Beacon state choice
     */
    public static final String SP_FILE_NAME = "switch_state";

    /**
     * Soft Beacon power
     */
    public static final String SP_FILE_POWER_NAME = "beacon_power";

    /**
     * Soft Beacon state
     */
    public static final String SWITCH_STATE_KEY = "state";

    /**
     * Notice Str
     */
    public static final String TYPE_NOTICE = "notice";

    /**
     * ImageUrl Str
     */
    public static final String TYPE_IMAGEURL = "imageUrl";

    /**
     * Name Str
     */
    public static final String TYPE_NAME = "name";

    /**
     * Desc Str
     */
    public static final String TYPE_DESC = "desc";

    /**
     * Attachment Str
     */
    public static final String TYPE_ATTACHMENT = "attachment";

    /**
     * UID type
     */
    public static final int TYPE_UID = 3;

    /**
     * IBeacon type
     */
    public static final int TYPE_IBEACON = 1;

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
