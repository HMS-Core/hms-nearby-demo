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
import com.wmq.hms.nearby.beaconmanager.beaconbase.BeaconUtil;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Beacon attachmentId
 *
 * @since 2019-11-13
 */
public class Attachment implements Parcelable {
    /**
     * The Creator
     */
    public static final Parcelable.Creator<Attachment> CREATOR = new Attachment.AttachmentCreator();

    /**
     * namespace max len
     */
    public static final int NAMESPACE_MAX_LEN = 32;

    /**
     * type max len
     */
    public static final int TYPE_MAX_LEN = 16;

    /**
     * value max len
     */
    public static final int VAL_MAX_LEN = 2048;

    @SerializedName("attachmentId")
    private String attachmentId;

    @SerializedName("namespace")
    private String namespace;

    @SerializedName("type")
    private String type;

    @SerializedName("value")
    private String value;

    /**
     * Constructor
     *
     * @param attachmentId attachment id
     * @param namespace namespace
     * @param type namespace type
     * @param value attachment data
     */
    public Attachment(String attachmentId, String namespace, String type, String value) {
        this.attachmentId = attachmentId;
        this.namespace = namespace;
        this.type = type;
        this.value = value;
    }

    /**
     * Constructor
     *
     * @param namespace namespace
     * @param type namespace type
     * @param value attachment data
     */
    public Attachment(String namespace, String type, String value) {
        this(null, namespace, type, value);
    }

    /**
     * Generate attachment id
     *
     * @return attachment id
     */
    public static String genAttachmentId() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date(System.currentTimeMillis());
        SecureRandom random = new SecureRandom();
        return String.format(Locale.ENGLISH, "%s_%08x", simpleDateFormat.format(date), random.nextInt());
    }

    /**
     * Check whether the length of each field of BeaconInfo is validity
     *
     * @return true on check ok, false on check failed
     */
    public boolean validityCheck() {
        if ((namespace == null || (namespace.length() == 0)) || (namespace.length() > NAMESPACE_MAX_LEN)) {
            return false;
        }
        if ((type == null || (type.length() == 0)) || (type.length() > TYPE_MAX_LEN)) {
            return false;
        }
        if ((value == null) || (value.length() == 0)) {
            return false;
        }
        byte[] data = BeaconUtil.base64StrToBytes(value);
        if ((data.length == 0) || (data.length > VAL_MAX_LEN)) {
            return false;
        }
        return true;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String valueBase64) {
        value = valueBase64;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "attachment{attachmentId:%s, namespace:%s, type:%s, value:%s}",
            (attachmentId == null ? "NIL" : attachmentId), namespace, type, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attachmentId);
        dest.writeString(namespace);
        dest.writeString(type);
        dest.writeString(value);
    }

    /**
     * DistanceCreator
     */
    public static class AttachmentCreator implements Creator<Attachment> {
        /**
         * Constructor of DistanceImpl
         */
        public AttachmentCreator() {
        }

        @Override
        public Attachment createFromParcel(Parcel source) {
            String aId = source.readString();
            String aName = source.readString();
            String aType = source.readString();
            String aVal = source.readString();
            return new Attachment(aId, aName, aType, aVal);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    }
}
