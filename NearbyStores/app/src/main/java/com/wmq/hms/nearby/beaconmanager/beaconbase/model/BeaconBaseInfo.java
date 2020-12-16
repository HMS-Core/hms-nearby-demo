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

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Beacon Register Status Param
 *
 * @since 2019-11-13
 */
public class BeaconBaseInfo implements Parcelable {
    /**
     * The Creator
     */
    public static final Parcelable.Creator<BeaconBaseInfo> CREATOR = new BeaconBaseInfoCreator();

    /**
     * beacon id max length
     */
    public static final int ID_MAX_LEN = 64;

    /**
     * beacon id
     */
    @SerializedName("beaconId")
    protected String beaconId;

    /**
     * beacon type
     */
    @SerializedName("beaconType")
    protected int beaconType;

    public BeaconBaseInfo() {
        this("", 0);
    }

    /**
     * Constructor
     *
     * @param beaconId The beacon id
     * @param beaconType The beacon type
     */
    public BeaconBaseInfo(String beaconId, int beaconType) {
        this.beaconId = beaconId;
        this.beaconType = beaconType;
    }

    /**
     * Check whether the length of each field of BeaconInfo is validity
     *
     * @return true on check ok, false on check failed
     */
    public boolean validityCheck() {
        if ((beaconId == null) || (beaconId.length() == 0) || (beaconId.length() > ID_MAX_LEN)) {
            return false;
        }

        return true;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public int getBeaconType() {
        return beaconType;
    }

    public void setBeaconType(int beaconType) {
        this.beaconType = beaconType;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "BeaconBaseInfo{beaconId:%s, beaconType:%d}", beaconId, beaconType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(beaconId);
        dest.writeInt(beaconType);
    }

    /**
     * BeaconBaseInfoCreator
     */
    public static class BeaconBaseInfoCreator implements Creator<BeaconBaseInfo> {
        /**
         * Constructor of DistanceImpl
         */
        public BeaconBaseInfoCreator() {
        }

        @Override
        public BeaconBaseInfo createFromParcel(Parcel source) {
            return new BeaconBaseInfo(source.readString(), source.readInt());
        }

        @Override
        public BeaconBaseInfo[] newArray(int size) {
            return new BeaconBaseInfo[size];
        }
    }

}
