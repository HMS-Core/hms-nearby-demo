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

package com.wmq.hms.nearby.beaconmanager.beaconbase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Locale;

/**
 * Beacon Register Parameter
 *
 * @since 2019-11-13
 */
public class BeaconInfo extends BeaconBaseInfo {
    /**
     * The Creator
     */
    public static final Parcelable.Creator<BeaconInfo> CREATOR = new BeaconInfoCreator();

    /**
     * beacon description max length
     */
    public static final int BEACON_DESC_MAX_LEN = 256;

    /**
     * place id max length
     */
    public static final int PLACE_ID_MAX_LEN = 256;

    /**
     * longitude max
     */
    public static final float LONGITUDE_MAX = 180.0f;

    /**
     * longitude min
     */
    public static final float LONGITUDE_MIN = -180.0f;

    /**
     * latitude max
     */
    public static final float LATITUDE_MAX = 90.0f;

    /**
     * latitude min
     */
    public static final float LATITUDE_MIN = -90.0f;

    /**
     * indoorLevel max length
     */
    public static final int INDOORLEVEL_MAX_LEN = 64;

    /**
     * stability max length
     */
    public static final int STABILITY_MAX_LEN = 16;

    /**
     * properties max length
     */
    public static final int PROPERTIES_MAX_CNT = 2;

    /**
     * properties max count
     */
    public static final int PROPERTIES_MAX_LEN = 256;

    /**
     * beacon description, optional field
     */
    @SerializedName("beaconDesc")
    protected String beaconDesc;

    /**
     * place information, optional field
     */
    @SerializedName("placeId")
    protected String placeId;

    /**
     * longitude, optional field
     */
    @SerializedName("longitude")
    protected float longitude;

    /**
     * latitude, optional field
     */
    @SerializedName("latitude")
    protected float latitude;

    /**
     * indoor floor level, optional field
     */
    @SerializedName("indoorLevel")
    protected String indoorLevel;

    /**
     * stability, canbe one of "Stable/Portable/Mobile/Roving/Not defined", optional field
     */
    @SerializedName("stability")
    protected String stability;

    /**
     * properties of the beacon, optional field
     */
    @SerializedName("properties")
    protected String propertiesStr;

    /**
     * properties of the beacon, optional field
     */
    protected transient HashMap<String, String> properties;

    @SerializedName("status")
    private int status;

    public BeaconInfo() {
        this("", 0, "", "", 0.0f, 0.0f, "", "Not defined", null, 0);
    }

    /**
     * Constructor
     *
     * @param beaconId The beacon id
     * @param beaconType The beacon type
     * @param beaconDesc beacon description
     * @param placeId location information
     * @param longitude longitude
     * @param latitude latitude
     * @param indoorLevel floor level
     * @param stability Stable;Portable;Mobile;Roving;Not defined
     * @param properties properties (at most 2) of beacon
     * @param status beacon status
     */
    public BeaconInfo(String beaconId, int beaconType, String beaconDesc, String placeId, float longitude,
        float latitude, String indoorLevel, String stability, HashMap<String, String> properties, int status) {
        super(beaconId, beaconType);
        this.beaconDesc = beaconDesc;
        this.placeId = placeId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.indoorLevel = indoorLevel;
        this.stability = stability;
        this.properties = properties;
        this.status = status;
        convertPropertisToStr();
    }

    /**
     * Create a BeaconInfo instance from current BeaconInfo
     *
     * @return BeaconInfo instance
     */
    public BeaconBaseInfo getBaseInfo() {
        return new BeaconBaseInfo(beaconId, beaconType);
    }

    /**
     * Check whether the length of each field of BeaconInfo is validity
     *
     * @return true on check ok, false on check failed
     */
    @Override
    public boolean validityCheck() {
        if (!super.validityCheck()) {
            return false;
        }

        if ((beaconDesc != null) && (beaconDesc.length() > BEACON_DESC_MAX_LEN)) {
            return false;
        }
        if ((placeId != null) && (placeId.length() > PLACE_ID_MAX_LEN)) {
            return false;
        }
        if ((longitude < LONGITUDE_MIN) || (longitude > LONGITUDE_MAX)) {
            return false;
        }
        if ((latitude < LATITUDE_MIN) || (latitude > LATITUDE_MAX)) {
            return false;
        }
        if ((indoorLevel != null) && (indoorLevel.length() > INDOORLEVEL_MAX_LEN)) {
            return false;
        }
        if ((stability != null) && (stability.length() > STABILITY_MAX_LEN)) {
            return false;
        }
        if (properties != null) {
            if ((properties.size() > PROPERTIES_MAX_CNT) || (propertiesStr.length() > PROPERTIES_MAX_LEN)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert properties' type form Map to Json string
     */
    public void convertPropertisFromStr() {
        if ((propertiesStr == null) || (propertiesStr.length() == 0)) {
            properties = null;
            return;
        }
        properties = new Gson().fromJson(propertiesStr, HashMap.class);
    }

    /**
     * Convert properties' type form Json string to Map
     */
    public void convertPropertisToStr() {
        if (properties == null) {
            propertiesStr = "";
            return;
        }
        propertiesStr = new Gson().toJson(properties);
    }

    public String getBeaconDesc() {
        return beaconDesc;
    }

    public void setBeaconDesc(String beaconDesc) {
        this.beaconDesc = beaconDesc;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getIndoorLevel() {
        return indoorLevel;
    }

    public void setIndoorLevel(String indoorLevel) {
        this.indoorLevel = indoorLevel;
    }

    public String getStability() {
        return stability;
    }

    public void setStability(String stability) {
        this.stability = stability;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
        convertPropertisToStr();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
            "BeaconInfo{beaconId:%s, beaconType%d, beaconDesc:%s, placeId:%s, longitude:%s, latitude:%s, "
                + "indoorLevel:%s, stability:%s, properties:%s, status:%d",
            beaconId, beaconType, (beaconDesc == null) ? "NIL" : beaconDesc, (placeId == null) ? "NIL" : placeId,
            longitude, latitude, (indoorLevel == null) ? "NIL" : indoorLevel, stability,
            (propertiesStr == null) ? "NIL" : propertiesStr, status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(beaconId);
        dest.writeInt(beaconType);
        dest.writeString(beaconDesc);
        dest.writeString(placeId);
        dest.writeFloat(longitude);
        dest.writeFloat(latitude);
        dest.writeString(indoorLevel);
        dest.writeString(stability);
        dest.writeMap(properties);
        dest.writeInt(status);
    }

    /**
     * BeaconInfoCreator
     */
    public static class BeaconInfoCreator implements Creator<BeaconInfo> {
        /**
         * Constructor of DistanceImpl
         */
        public BeaconInfoCreator() {
        }

        @Override
        public BeaconInfo createFromParcel(Parcel source) {
            return new BeaconInfo(source.readString(), source.readInt(), source.readString(), source.readString(),
                source.readFloat(), source.readFloat(), source.readString(), source.readString(),
                source.readHashMap(HashMap.class.getClassLoader()), source.readInt());
        }

        @Override
        public BeaconInfo[] newArray(int size) {
            return new BeaconInfo[size];
        }
    }
}
